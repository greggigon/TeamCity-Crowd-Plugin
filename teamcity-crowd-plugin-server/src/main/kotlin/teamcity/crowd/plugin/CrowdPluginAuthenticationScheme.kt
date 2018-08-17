package teamcity.crowd.plugin

import jetbrains.buildServer.controllers.interceptors.auth.HttpAuthenticationResult
import jetbrains.buildServer.controllers.interceptors.auth.HttpAuthenticationSchemeAdapter
import org.apache.http.HttpHeaders
import teamcity.crowd.plugin.config.CrowdPluginConfiguration
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CrowdPluginAuthenticationScheme(
        private val pluginCrowdClient: PluginCrowdClient,
        private val loggedInUserService: LoggedInUserService) : HttpAuthenticationSchemeAdapter() {

    private val base64Decoder = Base64.getDecoder()

    override fun doGetName(): String {
        return CrowdPluginConfiguration.CROWD_NAME
    }

    override fun processAuthenticationRequest(request: HttpServletRequest, response: HttpServletResponse, properties: MutableMap<String, String>): HttpAuthenticationResult {
        val authorizationPair = extractAuthorization(request.getHeader(HttpHeaders.AUTHORIZATION))
        if (authorizationPair != null && isBasicAuthorization(authorizationPair.first)) {
            val userPasswordPair = extractUserPassword(authorizationPair.second)
            val user = pluginCrowdClient.loginUserWithPassword(userPasswordPair.first, userPasswordPair.second)

            if (user != null) {
                val serverPrincipal = loggedInUserService.updateMembership(user)
                return HttpAuthenticationResult.authenticated(serverPrincipal, true)
            }
        }
        return HttpAuthenticationResult.unauthenticated()
    }

    override fun getDisplayName(): String {
        return "Crowd backed Basic HTTP"
    }

    override fun getDescription(): String {
        return "Allows basic HTTP authentication via Crowd users and groups"
    }

    private fun extractAuthorization(authorizationString: String?): Pair<String, String>? {
        if (authorizationString == null) return null
        val split = authorizationString.split(" ")
        if (split.count() != 2) {
            return null
        }
        return Pair(split[0], split[1])
    }

    private fun isBasicAuthorization(string: String): Boolean = "basic" == string.toLowerCase()

    private fun extractUserPassword(string: String): Pair<String, String> {
        val decoded = String(base64Decoder.decode(string))
        val split = decoded.split(":")
        return Pair(split[0], split[1])
    }
}
