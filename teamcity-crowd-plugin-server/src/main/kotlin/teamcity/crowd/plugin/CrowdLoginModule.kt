package teamcity.crowd.plugin

import com.intellij.openapi.diagnostic.Logger
import javax.security.auth.Subject
import javax.security.auth.callback.Callback
import javax.security.auth.callback.CallbackHandler
import javax.security.auth.callback.NameCallback
import javax.security.auth.callback.PasswordCallback
import javax.security.auth.login.FailedLoginException
import javax.security.auth.login.LoginException
import javax.security.auth.spi.LoginModule


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

        pluginCrowdClient = options!![ModuleDescriptorConstants.CROWD_CLIENT_OPTION] as PluginCrowdClient
        loggedInUserService = options[ModuleDescriptorConstants.LOGGED_IN_USER_SERVICE_OPTION] as LoggedInUserService
        logger = options[ModuleDescriptorConstants.LOGGER_OPTION] as Logger
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
            mySubject.principals.add(loggedInUserService.updateMembership(user))
            return true
        }
        throw FailedLoginException("Invalid username or password")
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