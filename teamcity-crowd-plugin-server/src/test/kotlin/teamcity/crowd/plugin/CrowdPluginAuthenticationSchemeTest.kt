package teamcity.crowd.plugin

import com.atlassian.crowd.model.user.User
import jetbrains.buildServer.controllers.interceptors.auth.HttpAuthenticationResult
import jetbrains.buildServer.serverSide.auth.ServerPrincipal
import org.apache.http.HttpHeaders
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito.*
import teamcity.crowd.plugin.config.CrowdPluginConfiguration
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CrowdPluginAuthenticationSchemeTest {
    private val pluginCrowdClient = mock(PluginCrowdClient::class.java)
    private val loggedInUserService = mock(LoggedInUserService::class.java)
    private val httpResponse = mock(HttpServletResponse::class.java)

    private val crowdPluginAuthenticationScheme = CrowdPluginAuthenticationScheme(pluginCrowdClient, loggedInUserService)


    @Test
    fun shouldNotAuthenticateIfHttpHeadersAreNotPresent() {
        val request = mock(HttpServletRequest::class.java)

        val result = crowdPluginAuthenticationScheme.processAuthenticationRequest(request, httpResponse, mutableMapOf())

        Assert.assertEquals(HttpAuthenticationResult.Type.UNAUTHENTICATED, result.type)
    }

    @Test
    fun shouldAuthenticateRequestWithBasicAuth() {
        val user = "foo"
        val password = "bar"
        val encodedUserPassword = "Basic ${String(Base64.getEncoder()!!.encode("$user:$password".toByteArray()))}"
        val expectedPrincipal = ServerPrincipal(CrowdPluginConfiguration.CROWD_REALM, user)

        val request = mock(HttpServletRequest::class.java)
        val mockedUser = mock(User::class.java)

        `when`(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(encodedUserPassword)
        `when`(pluginCrowdClient.loginUserWithPassword(user, password)).thenReturn(mockedUser)
        `when`(loggedInUserService.updateMembership(mockedUser)).thenReturn(expectedPrincipal)

        val result = crowdPluginAuthenticationScheme.processAuthenticationRequest(request, httpResponse, mutableMapOf())

        Assert.assertEquals(HttpAuthenticationResult.Type.AUTHENTICATED, result.type)
        Assert.assertEquals(expectedPrincipal, result.principal)

        verify(loggedInUserService).updateMembership(mockedUser)
    }


}