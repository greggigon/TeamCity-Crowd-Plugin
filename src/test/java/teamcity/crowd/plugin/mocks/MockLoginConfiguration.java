package teamcity.crowd.plugin.mocks;

import jetbrains.buildServer.serverSide.auth.AuthModule;
import jetbrains.buildServer.serverSide.auth.AuthModuleType;
import jetbrains.buildServer.serverSide.auth.LoginConfiguration;
import jetbrains.buildServer.serverSide.auth.LoginModuleDescriptor;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.security.auth.login.Configuration;
import java.util.Collection;
import java.util.List;

public class MockLoginConfiguration implements LoginConfiguration {
    @Override
    public void registerLoginModule(@NotNull LoginModuleDescriptor loginModuleDescriptor) {

    }

    @Override
    public void registerAuthModuleType(@NotNull AuthModuleType authModuleType) {

    }

    @Nullable
    @Override
    public String getTextForLoginPage() {
        return null;
    }

    @Override
    public boolean isDefaultLoginConfigured() {
        return false;
    }

    @Override
    public boolean isOnlyDefaultLoginConfigured() {
        return false;
    }

    @Override
    public <T extends AuthModuleType> boolean isAuthModuleConfigured(@NotNull Class<T> tClass) {
        return false;
    }

    @Override
    public boolean isAtLeastOneAuthModuleConfigured(@NotNull Collection<Class<? extends AuthModuleType>> classes) {
        return false;
    }

    @NotNull
    @Override
    public LoginModuleDescriptor getSelectedLoginModuleDescriptor() {
        return null;
    }

    @NotNull
    @Override
    public List<AuthModule<LoginModuleDescriptor>> getConfiguredLoginModules() {
        return null;
    }

    @Override
    public boolean isGuestLoginAllowed() {
        return false;
    }

    @Override
    public boolean isRootLoginAllowed() {
        return false;
    }

    @Override
    public String getGuestUsername() {
        return null;
    }

    @Override
    public boolean isFreeRegistrationAllowed() {
        return false;
    }

    @Override
    public boolean isUsersCanChangeOwnPasswords() {
        return false;
    }

    @Override
    public Collection<LoginModuleDescriptor> getRegisteredLoginModules() {
        return null;
    }

    @NotNull
    @Override
    public Collection<AuthModuleType> getRegisteredAuthModuleTypes() {
        return null;
    }

    @Nullable
    @Override
    public AuthModuleType findAuthModuleTypeByName(@NotNull String s) {
        return null;
    }

    @Override
    public Configuration createJAASConfiguration() {
        return null;
    }

    @NotNull
    @Override
    public Configuration createJAASConfiguration(@NotNull AuthModule<LoginModuleDescriptor> loginModuleDescriptorAuthModule) {
        return null;
    }

    @NotNull
    @Override
    public <T extends AuthModuleType> List<AuthModule<T>> getConfiguredAuthModules(@Nullable Class<T> tClass) {
        return null;
    }

    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public void writeTo(Element element) {

    }
}
