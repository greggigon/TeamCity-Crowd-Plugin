package teamcity.crowd.plugin

import com.intellij.openapi.diagnostic.Logger
import javax.security.auth.spi.LoginModule
import javax.security.auth.login.LoginException
import teamcity.crowd.plugin.utils.LoggerFactory
import javax.security.auth.callback.PasswordCallback
import javax.security.auth.callback.NameCallback
import javax.security.auth.callback.CallbackHandler
import javax.security.auth.Subject
import javax.security.auth.callback.Callback


class CrowdLoginModule : LoginModule {
    private lateinit var myCallbackHandler: CallbackHandler
    private lateinit var myNameCallback: NameCallback
    private lateinit var myPasswordCallback: PasswordCallback
    private lateinit var myCallbacks: Array<Callback>
    private lateinit var mySubject: Subject
    private lateinit var pluginCrowdClient: PluginCrowdClient
    private lateinit var loggedInUserService: LoggedInUserService
    private lateinit var logger: Logger

    override fun initialize(subject: Subject?, callbackHandler: CallbackHandler?, sharedState: MutableMap<String, *>?, options: MutableMap<String, *>?) {
        myCallbackHandler = callbackHandler!!
        myNameCallback = NameCallback("login:")
        myPasswordCallback = PasswordCallback("password:", false)
        myCallbacks = arrayOf(myNameCallback, myPasswordCallback)
        mySubject = subject!!
        pluginCrowdClient = options!![PluginCrowdClient::javaClass.name] as PluginCrowdClient
        loggedInUserService = options[LoggedInUserService::javaClass.name] as LoggedInUserService
        logger = (options[LoggerFactory::javaClass.name] as LoggerFactory).getServerLogger()
    }

    override fun login(): Boolean {
        try {
            myCallbackHandler.handle(myCallbacks)
        } catch (e: Exception) {
            throw LoginException(e.toString())
        }
        val username = myNameCallback.name
        val password = String(myPasswordCallback.password)

        logger.debug("Attempting to log in with user [$username]")

        val user = pluginCrowdClient.loginUserWithPassword(username, password)
        if (user != null) {
            // mySubject.principals.add(loggedInUserService)
        }
//        if (possiblyLoggedInUser.isPresent()) {
//            mySubject.getPrincipals().add(loggedInUserService.updateMembership(possiblyLoggedInUser.get()));
//            return true;
//        }
//        throw new FailedLoginException ("Invalid username or password");
        return false
    }

    override fun commit(): Boolean {
        return true
    }

    override fun logout(): Boolean {
        return true
    }

    override fun abort(): Boolean {
        return true
    }

}