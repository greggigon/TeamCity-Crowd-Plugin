package teamcity.crowd.plugin

import com.intellij.openapi.diagnostic.Logger
import jetbrains.buildServer.serverSide.ServerPaths
import teamcity.crowd.plugin.utils.LoggerFactory

class FakeServerPaths : ServerPaths("", "src/test/resources", "", "")

class FakeLogger : LoggerFactory {
    override fun getServerLogger(): Logger {
        return Logger.getInstance("Test")
    }
}