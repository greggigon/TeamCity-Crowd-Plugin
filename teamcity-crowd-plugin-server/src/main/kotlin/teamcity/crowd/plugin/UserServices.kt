package teamcity.crowd.plugin

import com.atlassian.crowd.model.user.User
import com.intellij.openapi.diagnostic.Logger
import jetbrains.buildServer.groups.UserGroupManager
import jetbrains.buildServer.serverSide.auth.ServerPrincipal
import jetbrains.buildServer.users.UserModel
import teamcity.crowd.plugin.config.CrowdPluginConfiguration
import teamcity.crowd.plugin.utils.LoggerFactory
import jetbrains.buildServer.users.SUser
import teamcity.crowd.plugin.utils.GroupNameConverter


class LoggedInUserService(
        private val pluginCrowdClient: PluginCrowdClient,
        private val userModel: UserModel,
        private val groupsUpdater: GroupsUpdater,
        loggerFactory: LoggerFactory) {

    private val logger: Logger = loggerFactory.getServerLogger()

    fun updateMemebership(crowdUser: User): ServerPrincipal {
        val tcUser = findUserAccountFor(crowdUser)
        val createdOrUpdatedUser = if (tcUser == null) {
            createUserAccount(crowdUser)
        } else {
            updateUserDetails(tcUser, crowdUser)
        }
        val userGroupsInCrowd = userGroupsInCrowd(createdOrUpdatedUser)
        groupsUpdater.updateGroups(createdOrUpdatedUser, userGroupsInCrowd)

        return ServerPrincipal(CrowdPluginConfiguration.CROWD_REALM, crowdUser.name)
    }


    private fun findUserAccountFor(user: User): SUser? {
        return userModel.findUserAccount(CrowdPluginConfiguration.CROWD_REALM, user.name)
    }

    private fun createUserAccount(user: User): SUser {
        logger.info("User [${user.name} doesn't exists in TeamCity. Creating!")
        val userAccount = userModel.createUserAccount(CrowdPluginConfiguration.CROWD_REALM, user.name)
        userAccount.updateUserAccount(user.name, user.displayName, user.emailAddress)
        return userAccount
    }

    private fun updateUserDetails(teamCityUser: SUser, user: User): SUser {
        val associatedUser = teamCityUser.associatedUser
        if (associatedUser!!.name != user.displayName || associatedUser.email != user.emailAddress) {
            teamCityUser.updateUserAccount(user.name, user.displayName, user.emailAddress)
        }
        return teamCityUser
    }

    private fun userGroupsInCrowd(teamCityUser: SUser): List<String> {
        return pluginCrowdClient.getUserGroups(teamCityUser.username).map { it.name }
    }
}

open class GroupsUpdater(private val userGroupManager: UserGroupManager,
                    private val doNotRemoveInternalGroups: Boolean,
                    private val shouldCreateGroups: Boolean,
                    loggerFactory: LoggerFactory) {

    private val ALL_USERS_GROUP = "All Users"
    private val CREATED_BY_PLUGIN_MESSAGE = "Created by Crowd Plugin"
    private val groupNameConverter = GroupNameConverter(userGroupManager)
    private val logger: Logger = loggerFactory.getServerLogger()


    fun updateGroups(teamCityUser: SUser, userGroupsInCrowd: Collection<String>) {
        val allTeamCityGroupsUserIsAMemberOfAlready = allTeamCityUserGroups(teamCityUser)
        val listOfGroupsUserShouldBeRemovedFrom = groupsToBeRemovedFrom(
                if (doNotRemoveInternalGroups) allTeamCityNonInternalUserGroups(teamCityUser) else allTeamCityGroupsUserIsAMemberOfAlready,
                userGroupsInCrowd)

        for (userGroup in userGroupsInCrowd) {
            if (!allTeamCityGroupsUserIsAMemberOfAlready.contains(userGroup)) {
                var teamCityGroup = userGroupManager.findUserGroupByName(userGroup)
                if (teamCityGroup == null && shouldCreateGroups) {
                    val groupKey = groupNameConverter.convert(userGroup)
                    if (groupKey != null) {
                        teamCityGroup = userGroupManager.createUserGroup(groupKey, userGroup, CREATED_BY_PLUGIN_MESSAGE)
                    }
                }
                if (teamCityGroup != null) {
                    teamCityGroup.addUser(teamCityUser)
                    logger.info("Added user [${teamCityUser.username}] to group [$userGroup]")
                }
            }
        }

        for (groupName in listOfGroupsUserShouldBeRemovedFrom) {
            logger.info("Removing user [${teamCityUser.username}] from group [$groupName]")
            val teamCityGroup = userGroupManager.findUserGroupByName(groupName)
            teamCityGroup?.removeUser(teamCityUser)
        }

        teamCityUser.userGroups
    }

    private fun allTeamCityUserGroups(teamCityUser: SUser): List<String> {
        return teamCityUser.userGroups.map { it.name }
    }

    private fun groupsToBeRemovedFrom(teamCityGroups: Collection<String>, crowdGroups: Collection<String>): List<String> {
        return teamCityGroups.filter {
            !crowdGroups.contains(it) && ALL_USERS_GROUP.toLowerCase() != it.toLowerCase()
        }
    }

    private fun allTeamCityNonInternalUserGroups(teamCityUser: SUser): List<String> {
        return teamCityUser.userGroups.filter { it.description == CREATED_BY_PLUGIN_MESSAGE }.map { it.name }
    }
}