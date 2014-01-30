package teamcity.crowd.plugin.loginmodule

import jetbrains.buildServer.serverSide.auth.LoginConfiguration
import org.junit.Test
import teamcity.crowd.plugin.PluginCrowdClient
import teamcity.crowd.plugin.mocks.MockLogger
import teamcity.crowd.plugin.utils.LoggerFactory

import static org.mockito.Mockito.mock

class CrowdPluginLoginModuleDescriptorTest {

    @Test
    void "should set crowd integration client when module initialize"(){
        def loginConfiguration = mock(LoginConfiguration)
        def integrationClient = mock(PluginCrowdClient)
        def handler = mock(LoggedInUserService)

        def descriptor = new CrowdPluginLoginModuleDescriptor(loginConfiguration, integrationClient, handler, new MockLogger())

        assert descriptor.options.size() == 3
        assert descriptor.options[PluginCrowdClient.class.name]
        assert descriptor.options[LoggerFactory.class.name]
        assert descriptor.options[LoggedInUserService.class.name]
    }
}
