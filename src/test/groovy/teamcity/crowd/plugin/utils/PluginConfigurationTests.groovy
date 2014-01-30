package teamcity.crowd.plugin.utils

import com.atlassian.crowd.service.client.ClientProperties
import jetbrains.buildServer.web.openapi.PluginException
import org.junit.Test
import teamcity.crowd.plugin.mocks.MockLogger

class PluginConfigurationTests {

    @Test
    void 'should Complain A Lot When Client Configuration file Is Not There'() {
        try {
            new CrowdPluginConfiguration(new File(".").absolutePath, "some.properties", [:] as ClientProperties, new MockLogger())
            fail("Should drop dead with Plugin exception")
        } catch (PluginException e) {
            assert true
        }
    }
}
