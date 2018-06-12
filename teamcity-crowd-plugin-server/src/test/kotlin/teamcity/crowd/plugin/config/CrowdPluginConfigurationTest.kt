package teamcity.crowd.plugin.config

import com.atlassian.crowd.service.client.ClientProperties
import com.intellij.openapi.diagnostic.Logger
import jetbrains.buildServer.web.openapi.PluginException
import org.junit.Test
import org.mockito.Mockito.mock

class CrowdPluginConfigurationTest {

    @Test(expected = PluginException::class)
    fun shouldFailToLoadNonExistentFile() {
        val crowdProperties = mock(ClientProperties::class.java)

        CrowdPluginConfiguration("foo", "bar", FakeLogger(), crowdProperties)
    }
}

class FakeLogger : LoggerFactory {
    override fun getServerLogger(): Logger {
        return Logger.getInstance("Test")
    }
}