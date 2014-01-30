package teamcity.crowd.plugin;

import com.atlassian.crowd.model.user.User;
import com.google.common.base.Optional;
import jetbrains.buildServer.controllers.interceptors.auth.HttpAuthenticationResult;
import jetbrains.buildServer.controllers.interceptors.auth.impl.BasicProtocolBasedHttpAuthenticationScheme;
import jetbrains.buildServer.serverSide.auth.LoginConfiguration;
import jetbrains.buildServer.serverSide.auth.ServerPrincipal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import teamcity.crowd.plugin.loginmodule.LoggedInUserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class CrowdPluginAuthenticationScheme extends BasicProtocolBasedHttpAuthenticationScheme {
    public static final String REALM = "crowd";

    private final PluginCrowdClient crowdClient;
    private final LoggedInUserService loggedInUserService;

    protected CrowdPluginAuthenticationScheme(@Nullable LoginConfiguration loginConfiguration, PluginCrowdClient crowdClient, LoggedInUserService loggedInUserService) {
        super(loginConfiguration);

        this.crowdClient = crowdClient;
        this.loggedInUserService = loggedInUserService;
    }

    @NotNull
    @Override
    protected HttpAuthenticationResult checkCredentials(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull String s, @NotNull String s2, @NotNull Map<String, String> stringStringMap) throws IOException {
        final Optional<User> possibleLoggedInUser = crowdClient.loginUserWithPassword(s, s2);

        if (possibleLoggedInUser.isPresent()){
            final User user = possibleLoggedInUser.get();
            final ServerPrincipal serverPrincipal = loggedInUserService.updateMembership(user);
            return HttpAuthenticationResult.authenticated(serverPrincipal, true);
        }
        return HttpAuthenticationResult.unauthenticated();
    }

    @NotNull
    @Override
    protected String doGetName() {
        return "Crowd";
    }
}
