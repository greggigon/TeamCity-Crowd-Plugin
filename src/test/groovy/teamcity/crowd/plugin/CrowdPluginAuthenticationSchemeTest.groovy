package teamcity.crowd.plugin
import com.atlassian.crowd.model.user.User
import com.google.common.base.Optional
import jetbrains.buildServer.controllers.interceptors.auth.HttpAuthenticationResult
import jetbrains.buildServer.serverSide.auth.LoginConfiguration
import jetbrains.buildServer.serverSide.auth.ServerPrincipal
import org.junit.Test
import teamcity.crowd.plugin.CrowdPluginAuthenticationScheme
import teamcity.crowd.plugin.PluginCrowdClient
import teamcity.crowd.plugin.loginmodule.LoggedInUserService

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.mockito.Matchers.anyObject
import static org.mockito.Mockito.*

class CrowdPluginAuthenticationSchemeTest {

    @Test
    void "should not authenticate when crowd doesn't validate user and password"(){
        def loginConfiguration = mock(LoginConfiguration)
        def crowdClient = mock(PluginCrowdClient)
        def loggedInUserHandler = mock(LoggedInUserService)

        when(crowdClient.loginUserWithPassword('foo', 'bar')).thenReturn(Optional.absent())

        def scheme = new CrowdPluginAuthenticationScheme(loginConfiguration, crowdClient, loggedInUserHandler)

        def result = scheme.checkCredentials([:] as HttpServletRequest, [:] as HttpServletResponse, 'foo', 'bar', [:])

        assert result.type == HttpAuthenticationResult.Type.UNAUTHENTICATED

        verify(loggedInUserHandler, never()).updateMembership(anyObject())
    }

    @Test
    void "should authenticate when crowd client validates user and password"(){
        def loginConfiguration = mock(LoginConfiguration)
        def crowdClient = mock(PluginCrowdClient)
        def loggedInUserHandler = mock(LoggedInUserService)
        def user = [:] as User
        def serverPrincipal = new ServerPrincipal(CrowdPluginAuthenticationScheme.REALM, 'foo')

        when(crowdClient.loginUserWithPassword('foo', 'bar')).thenReturn(Optional.of(user))
        when(loggedInUserHandler.updateMembership(user)).thenReturn(serverPrincipal)

        def scheme = new CrowdPluginAuthenticationScheme(loginConfiguration, crowdClient, loggedInUserHandler)

        def result = scheme.checkCredentials([:] as HttpServletRequest, [:] as HttpServletResponse, 'foo', 'bar', [:])

        assert result.type == HttpAuthenticationResult.Type.AUTHENTICATED
        assert result.principal == serverPrincipal
    }
}
