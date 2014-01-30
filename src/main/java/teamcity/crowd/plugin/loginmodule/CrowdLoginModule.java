package teamcity.crowd.plugin.loginmodule;

import com.atlassian.crowd.model.user.User;
import com.google.common.base.Optional;
import teamcity.crowd.plugin.PluginCrowdClient;
import teamcity.crowd.plugin.utils.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.util.Map;

public class CrowdLoginModule implements LoginModule {

    private CallbackHandler myCallbackHandler;
    private NameCallback myNameCallback;
    private PasswordCallback myPasswordCallback;
    private Callback[] myCallbacks;
    private Subject mySubject;

    private PluginCrowdClient pluginCrowdClient;
    private LoggerFactory loggerFactory;
    private LoggedInUserService loggedInUserService;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        myCallbackHandler = callbackHandler;
        myNameCallback = new NameCallback("login:");
        myPasswordCallback = new PasswordCallback("password:", false);
        myCallbacks = new Callback[]{myNameCallback, myPasswordCallback};
        mySubject = subject;
        pluginCrowdClient = (PluginCrowdClient) options.get(PluginCrowdClient.class.getName());
        loggedInUserService = (LoggedInUserService) options.get(LoggedInUserService.class.getName());
        loggerFactory = (LoggerFactory) options.get(LoggerFactory.class.getName());
    }

    @Override
    public boolean login() throws LoginException {
        try {
            myCallbackHandler.handle(myCallbacks);
        } catch (Exception e) {
            throw new LoginException(e.toString());
        }
        final String username = myNameCallback.getName();
        final String password = new String(myPasswordCallback.getPassword());

        String message = String.format("Attempting to log in with user [%s]", username);
        loggerFactory.getServerLogger().debug(message);

        Optional<User> possiblyLoggedInUser = pluginCrowdClient.loginUserWithPassword(username, password);
        if (possiblyLoggedInUser.isPresent()) {
            mySubject.getPrincipals().add(loggedInUserService.updateMembership(possiblyLoggedInUser.get()));
            return true;
        }
        throw new FailedLoginException("Invalid username or password");
    }

    @Override
    public boolean commit() throws LoginException {
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        return true;
    }
}
