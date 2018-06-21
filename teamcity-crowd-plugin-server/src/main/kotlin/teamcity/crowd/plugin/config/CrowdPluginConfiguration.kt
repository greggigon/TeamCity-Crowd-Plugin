package teamcity.crowd.plugin.config

import com.atlassian.crowd.service.client.ClientProperties
import jetbrains.buildServer.web.openapi.PluginException
import teamcity.crowd.plugin.utils.LoggerFactory
import java.io.File
import java.io.FileReader
import java.util.*

/**
 * This class is used to bootstrap the REST Crowd Client
 */
class CrowdPluginConfiguration(configDirectory: String, configFileName: String, loggerFactory: LoggerFactory, val clientProperties: ClientProperties) {
    val shouldCreateGroups: Boolean
    val doNotRemoveInternalGroups: Boolean

    companion object {
        const val SHOULD_CREATE_GROUPS = "tc.crowd.plugin.createGroups"
        const val DO_NOT_REMOVE_INTERNAL_GROUPS = "tc.crowd.plugin.doNotRemoveIntGroups"

        const val CONFIG_ERROR_MESSAGE = "Can't initialize the configuration properties for TeamCity Crowd Plugin. " +
                "Make sure configuration file teamcity-crowd-plugin.properties is in the TeamCity Data Folder/config"
    }

    init {
        val configurationFile = File(configDirectory, configFileName)
        val log = loggerFactory.getServerLogger()

        if (!configurationFile.exists() && !configurationFile.isFile) {
            log.error("Path to configuration file doesn't exists [${configurationFile.absolutePath}]")
            throw PluginException(CONFIG_ERROR_MESSAGE)
        }

        val pluginProperties = Properties()
        val reader = FileReader(configurationFile)
        pluginProperties.load(reader)

        clientProperties.updateProperties(pluginProperties)

        shouldCreateGroups = pluginProperties.getProperty(SHOULD_CREATE_GROUPS, "false").toBoolean()
        doNotRemoveInternalGroups = pluginProperties.getProperty(DO_NOT_REMOVE_INTERNAL_GROUPS, "false").toBoolean()
    }


}