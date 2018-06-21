package teamcity.crowd.plugin.utils

import com.intellij.openapi.diagnostic.Logger
import jetbrains.buildServer.log.Loggers

interface LoggerFactory {
    fun getServerLogger(): Logger
}

/**
 * Used to log messages into teamcity-server.log file.
 * More information in @see https://confluence.jetbrains.com/display/TCD10/Plugin+Development+FAQ
 */
class TeamCityLoggerFactory : LoggerFactory {

    override fun getServerLogger(): Logger {
        return Loggers.SERVER
    }

}