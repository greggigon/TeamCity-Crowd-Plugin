package teamcity.crowd.plugin.loginmodule;

import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.user.User;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.groups.SUserGroup;
import jetbrains.buildServer.groups.UserGroup;
import jetbrains.buildServer.groups.UserGroupManager;
import jetbrains.buildServer.serverSide.auth.ServerPrincipal;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.UserModel;
import teamcity.crowd.plugin.CrowdPluginAuthenticationScheme;
import teamcity.crowd.plugin.PluginCrowdClient;
import teamcity.crowd.plugin.utils.CrowdPluginConfiguration;
import teamcity.crowd.plugin.utils.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;

import static java.lang.String.format;

public class LoggedInUserService {
    private final GroupNameToGroupKey groupNameToGroupKey;
    private final UserGroupManager userGroupManager;
    private final PluginCrowdClient pluginCrowdClient;
    private final UserModel userModel;
    private final LoggerFactory loggerFactory;
    private boolean shouldCreateGroups = false;


    public static final String ALL_USERS_GROUP = "All Users";
    public static final String CREATED_BY_PLUGIN_MESSAGE = "Created by Crowd Plugin";

    public LoggedInUserService(
            UserGroupManager userGroupManager,
            PluginCrowdClient pluginCrowdClient,
            UserModel userModel,
            LoggerFactory loggerFactory,
            CrowdPluginConfiguration crowdPluginConfiguration) {
        groupNameToGroupKey = new GroupNameToGroupKey(userGroupManager);
        this.userGroupManager = userGroupManager;
        this.pluginCrowdClient = pluginCrowdClient;
        this.userModel = userModel;
        this.loggerFactory = loggerFactory;
        this.shouldCreateGroups = crowdPluginConfiguration.shouldCreateGroups();
    }

    public ServerPrincipal updateMembership(User user) {

        final Optional<SUser> possibleAccount = findUserAccountFor(user);
        SUser teamCityUser;
        if (!possibleAccount.isPresent()) {
            teamCityUser = createUserAccount(user);
        } else {
            teamCityUser = updateUserDetails(possibleAccount.get(), user);
        }

        updateUserGroups(teamCityUser);

        return new ServerPrincipal(CrowdPluginAuthenticationScheme.REALM, user.getName());
    }

    private SUser updateUserDetails(SUser teamCityUser, User user) {
        final jetbrains.buildServer.users.User associatedUser = teamCityUser.getAssociatedUser();
        if ( ! associatedUser.getName().equals(user.getDisplayName()) ||
             ! associatedUser.getEmail().equals(user.getEmailAddress()) ) {
            teamCityUser.updateUserAccount(user.getName(), user.getDisplayName(), user.getEmailAddress());
        }
        return teamCityUser;
    }

    private void updateUserGroups(SUser teamCityUser) {
        Logger logger = loggerFactory.getServerLogger();
        final List<String> userGroupsInCrowd = userGroupsInCrowd(teamCityUser);
        final List<String> allTeamCityGroupsUserIsAMemberOfAlready = allTeamCityUserGroups(teamCityUser);
        final List<String> listOfGropusUserShouldBeRemovedFrom =
                groupsToBeRemovedFrom(allTeamCityGroupsUserIsAMemberOfAlready, userGroupsInCrowd);

        for (String userGroup : userGroupsInCrowd) {
            if (!allTeamCityGroupsUserIsAMemberOfAlready.contains(userGroup)) {
                SUserGroup teamCityGroup = userGroupManager.findUserGroupByName(userGroup);
                if (teamCityGroup == null && shouldCreateGroups) {
                    Optional<String> groupKey = groupNameToGroupKey.transform(userGroup);
                    if ( groupKey.isPresent() ) {
                        teamCityGroup = userGroupManager.createUserGroup(
                                groupKey.get(),
                                userGroup,
                                CREATED_BY_PLUGIN_MESSAGE);
                    }
                }
                if (teamCityGroup != null) {
                    teamCityGroup.addUser(teamCityUser);
                    logger.info(format("Added user [%s] to group [%s]", teamCityUser.getUsername(), userGroup));
                }
            }
        }
        for (String groupName : listOfGropusUserShouldBeRemovedFrom) {
            logger.info(format("Removing user [%s] from group [%s]", teamCityUser.getUsername(), groupName));
            final SUserGroup teamCityGroup = userGroupManager.findUserGroupByName(groupName);
            teamCityGroup.removeUser(teamCityUser);
        }

        teamCityUser.getUserGroups();
    }

    private List<String> groupsToBeRemovedFrom(List<String> temCityGroups, final List<String> crowdGroups) {
        return Lists.newArrayList(Collections2.filter(temCityGroups, new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return !crowdGroups.contains(input) && !ALL_USERS_GROUP.toLowerCase().equals(input.toLowerCase());
            }
        }));
    }

    private List<String> userGroupsInCrowd(SUser teamCityUser) {
        return Lists.newArrayList(Collections2.transform(
                pluginCrowdClient.getUserGroups(teamCityUser.getUsername()),
                new Function<Group, String>() {
            @Override
            public String apply(@Nullable Group input) {
                return input.getName();
            }
        }));
    }

    private List<String> allTeamCityUserGroups(SUser teamCityUser) {
        return Lists.newArrayList(Collections2.transform(
                teamCityUser.getUserGroups(),
                new Function<UserGroup, String>() {
            @Override
            public String apply(@Nullable UserGroup input) {
                return input.getName();
            }
        }));
    }

    private SUser createUserAccount(User user) {
        loggerFactory.getServerLogger().info("User [" + user.getName() + "] doesn't exists in TeamCity. Creating!");
        SUser userAccount = userModel.createUserAccount(CrowdPluginAuthenticationScheme.REALM, user.getName());
        userAccount.updateUserAccount(user.getName(), user.getDisplayName(), user.getEmailAddress());
        return userAccount;
    }

    private Optional<SUser> findUserAccountFor(User user) {
        final SUser userAccount = userModel.findUserAccount(CrowdPluginAuthenticationScheme.REALM, user.getName());
        if (userAccount != null) {
            return Optional.of(userAccount);
        }
        return Optional.absent();
    }

}
