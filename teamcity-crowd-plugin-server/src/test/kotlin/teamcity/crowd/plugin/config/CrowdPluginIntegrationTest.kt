package teamcity.crowd.plugin.config

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration(locations = [
    "classpath:test-build-server-plugin-teamcity-crowd-plugin.xml"
])
class CrowdPluginIntegrationTest {
    companion object {
        init {
            System.setProperty("crowd.properties", "src/main/resources/valid-crowd.properties")
        }
    }

    @Autowired
    private val pluginConfiguration: CrowdPluginConfiguration? = null

    @Test
    fun shouldReadConfiguration() {
        assertNotNull(pluginConfiguration)
    }

    @Test
    fun shouldReadValidConfiguration(){
        assertTrue(pluginConfiguration!!.shouldCreateGroups)
        assertTrue(pluginConfiguration!!.doNotRemoveInternalGroups)
    }
}