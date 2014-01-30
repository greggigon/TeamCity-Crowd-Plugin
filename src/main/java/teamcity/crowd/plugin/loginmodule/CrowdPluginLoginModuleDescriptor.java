package teamcity.crowd.plugin.loginmodule;

import jetbrains.buildServer.serverSide.auth.LoginConfiguration;
import jetbrains.buildServer.serverSide.auth.LoginModuleDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import teamcity.crowd.plugin.PluginCrowdClient;
import teamcity.crowd.plugin.utils.LoggerFactory;

import javax.security.auth.spi.LoginModule;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CrowdPluginLoginModuleDescriptor implements LoginModuleDescriptor {

    private final PluginCrowdClient pluginCrowdClient;
    private final LoggedInUserService loggedInUserService;
    private LoggerFactory loggerFactory;

    public CrowdPluginLoginModuleDescriptor(LoginConfiguration loginConfiguration, PluginCrowdClient pluginCrowdClient,
                                            LoggedInUserService loggedInUserService, LoggerFactory loggerFactory){
        this.pluginCrowdClient = pluginCrowdClient;
        this.loggedInUserService = loggedInUserService;
        this.loggerFactory = loggerFactory;
        loginConfiguration.registerAuthModuleType(this);
    }

    @Override
    public Class<? extends LoginModule> getLoginModuleClass() {
        return CrowdLoginModule.class;
    }

    @Nullable
    @Override
    public Map<String, ?> getOptions() {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(PluginCrowdClient.class.getName(), pluginCrowdClient);
        options.put(LoggerFactory.class.getName(), loggerFactory);
        options.put(LoggedInUserService.class.getName(), loggedInUserService);
        return options;
    }

    @Nullable
    @Override
    public Map<String, ?> getJAASOptions(@NotNull Map<String, String> stringStringMap) {
        return getOptions();
    }

    @Nullable
    @Override
    public String getTextForLoginPage() {
        return "Login with your Crowd Credentials.";
    }

    @Nullable
    @Override
    public Collection<String> validate() {
        return new ArrayList<>();
    }

    @NotNull
    @Override
    public String getName() {
        return "Crowd";
    }

    @Nullable
    @Override
    public String getDisplayName() {
        return "Crowd Login Module";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Description from CrowdLoginModuleDesc";
    }

    @Override
    public boolean isMultipleInstancesAllowed() {
        return false;
    }

    @NotNull
    @Override
    public Map<String, String> getDefaultProperties() {
        return new HashMap<>();
    }

    @Nullable
    @Override
    public String getEditPropertiesJspFilePath() {
        return null;
    }

    @NotNull
    @Override
    public String describeProperties(@NotNull Map<String, String> stringStringMap) {
        return null;
    }
}
