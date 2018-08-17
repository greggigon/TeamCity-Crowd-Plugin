package teamcity.crowd.plugin.config

import com.atlassian.crowd.service.client.ClientProperties
import com.intellij.openapi.diagnostic.Logger
import jetbrains.buildServer.web.openapi.PluginException
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import teamcity.crowd.plugin.FakeLogger
import teamcity.crowd.plugin.utils.LoggerFactory

class CrowdPluginConfigurationTest {

    @Test(expected = PluginException::class)
    fun shouldFailToLoadNonExistentFile() {
        val crowdProperties = mock(ClientProperties::class.java)

        CrowdPluginConfiguration("foo", "bar", FakeLogger(), crowdProperties)
    }

    @Test
    fun shouldLoadValidProperties() {
        val crowdProperties = mock(ClientProperties::class.java)

        val crowdPluginConfiguration = CrowdPluginConfiguration("src/test/resources", "valid-crowd.properties", FakeLogger(), crowdProperties)

        verify(crowdProperties).updateProperties(ArgumentMatchers.any())

        assertTrue("Loaded property should be true", crowdPluginConfiguration.shouldCreateGroups)
        assertTrue("Loaded property should be true", crowdPluginConfiguration.doNotRemoveInternalGroups)

        assertNotNull(crowdPluginConfiguration.clientProperties)
    }

}

