package teamcity.crowd.plugin.mocks;

import jetbrains.buildServer.groups.*;
import jetbrains.buildServer.users.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class MockUserGroupManager implements UserGroupManager {
    @NotNull
    @Override
    public SUserGroup createUserGroup(@NotNull String s, @NotNull String s2, @NotNull String s3) throws UserGroupException {
        return null;
    }

    @Override
    public void deleteUserGroup(@NotNull SUserGroup sUserGroup) {

    }

    @Override
    public void renameUserGroup(@NotNull String s, @NotNull String s2) throws UserGroupException {

    }

    @NotNull
    @Override
    public Collection<SUserGroup> getUserGroups() {
        return null;
    }

    @NotNull
    @Override
    public Collection<SUserGroup> getRootUserGroups() {
        return null;
    }

    @NotNull
    @Override
    public List<UserGroup> getHostGroupsOf(@NotNull User user) {
        return null;
    }

    @NotNull
    @Override
    public List<UserGroup> getAllHostGroupsOf(@NotNull User user) {
        return null;
    }

    @Nullable
    @Override
    public SUserGroup findUserGroupByKey(@NotNull String s) {
        return null;
    }

    @Nullable
    @Override
    public SUserGroup findUserGroupByName(@NotNull String s) {
        return null;
    }

    @Override
    public void addUserGroupListener(@NotNull UserGroupListener userGroupListener) {

    }

    @Override
    public void removeUserGroupListener(@NotNull UserGroupListener userGroupListener) {

    }

    @NotNull
    @Override
    public SUserGroup getAllUsersGroup() {
        return null;
    }

    @Override
    public boolean isAllUsersGroup(@NotNull SUserGroup sUserGroup) {
        return false;
    }
}
