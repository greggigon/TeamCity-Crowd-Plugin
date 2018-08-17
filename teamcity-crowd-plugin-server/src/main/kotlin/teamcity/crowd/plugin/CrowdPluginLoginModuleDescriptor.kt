package teamcity.crowd.plugin

import com.intellij.openapi.diagnostic.Logger
import jetbrains.buildServer.serverSide.auth.LoginConfiguration
import jetbrains.buildServer.serverSide.auth.LoginModuleDescriptor
import teamcity.crowd.plugin.config.CrowdPluginConfiguration
import teamcity.crowd.plugin.utils.TeamCityLoggerFactory
import javax.security.auth.spi.LoginModule

class CrowdPluginLoginModuleDescriptor(loginConfiguration: LoginConfiguration,
                                       private val pluginCrowdClient: PluginCrowdClient,
                                       private val loggedInUserService: LoggedInUserService,
                                       loggerFactory: TeamCityLoggerFactory) : LoginModuleDescriptor {

    private val logger = loggerFactory.getServerLogger()

    init {
        loginConfiguration.registerAuthModuleType(this)
    }

    override fun getName(): String {
        return CrowdPluginConfiguration.CROWD_NAME
    }

    override fun isMultipleInstancesAllowed(): Boolean {
        return false
    }

    override fun getLoginModuleClass(): Class<out LoginModule> {
        return CrowdLoginModule::class.java
    }

    override fun getDisplayName(): String {
        return ModuleDescriptorConstants.DISPLAY_NAME
    }

    override fun getDefaultProperties(): MutableMap<String, String> {
        return mutableMapOf()
    }

    override fun getJAASOptions(properties: MutableMap<String, String>): MutableMap<String, *> {
        val options = mutableMapOf<String, Any>()
        options[ModuleDescriptorConstants.CROWD_CLIENT_OPTION] = pluginCrowdClient
        options[ModuleDescriptorConstants.LOGGER_OPTION] = logger
        options[ModuleDescriptorConstants.LOGGED_IN_USER_SERVICE_OPTION] = loggedInUserService
        options.putAll(properties)
        return options
    }

    override fun getEditPropertiesJspFilePath(): String? {
        return null
    }

    override fun describeProperties(properties: MutableMap<String, String>): String {
        return ""
    }

    override fun validate(properties: MutableMap<String, String>): MutableCollection<String> {
        return mutableListOf()
    }

    override fun getDescription(): String {
        return ModuleDescriptorConstants.DESCRIPTION_TEXT
    }

    override fun getOptions(): MutableMap<String, *> {
        return getJAASOptions(mutableMapOf())
    }

    override fun getTextForLoginPage(): String? {
        return ModuleDescriptorConstants.TEXT_FOR_LOGIN_PAGE
    }
}

object ModuleDescriptorConstants {
    const val TEXT_FOR_LOGIN_PAGE = "Login with your Crowd Credentials"
    const val DESCRIPTION_TEXT = "Crowd Login Module uses Crowd to connect and Authenticate"
    const val DISPLAY_NAME = "Crowd Login Module"

    const val CROWD_CLIENT_OPTION = "CROWD_CLIENT_OPTION"
    const val LOGGER_OPTION = "LOGGER_OPTION"
    const val LOGGED_IN_USER_SERVICE_OPTION = "LOGGED_IN_USER_SERVICE_OPTION"
}