package teamcity.crowd.plugin.mocks;

import jetbrains.buildServer.users.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MockUserModel implements UserModel {
    @Override
    public SUser createUserAccount(String s, String s2) throws DuplicateUserAccountException, MaxNumberOfUserAccountsReachedException, EmptyUsernameException {
        return null;
    }

    @Nullable
    @Override
    public SUser findUserById(long l) {
        return null;
    }

    @Nullable
    @Override
    public SUser findUserAccount(@Nullable String s, @NotNull String s2) {
        return null;
    }

    @Nullable
    @Override
    public SUser findUserAccount(@Nullable String s, @NotNull String s2, @NotNull String s3) {
        return null;
    }

    @Override
    public UserSet<SUser> findUsersByPropertyValue(PropertyKey propertyKey, String s, boolean b) {
        return null;
    }

    @Nullable
    @Override
    public SUser findUserByUsername(@NotNull String s, @NotNull AuthPropertyKey authPropertyKey) throws InvalidUsernameException {
        return null;
    }

    @Override
    public UserSet<SUser> getAllUsers() {
        return null;
    }

    @Override
    public int getNumberOfRegisteredUsers() {
        return 0;
    }

    @Override
    public boolean hasAdministratorAccount() {
        return false;
    }

    @Override
    public void removeUserAccount(long l) {

    }

    @Override
    public void addListener(UserModelListener userModelListener) {

    }

    @Override
    public void removeListener(UserModelListener userModelListener) {

    }

    @Override
    public boolean isGuestUser(@NotNull User user) {
        return false;
    }

    @NotNull
    @Override
    public SUser getGuestUser() {
        return null;
    }

    @Override
    public boolean isSuperUser(@NotNull User user) {
        return false;
    }

    @NotNull
    @Override
    public SUser getSuperUser() {
        return null;
    }
}
