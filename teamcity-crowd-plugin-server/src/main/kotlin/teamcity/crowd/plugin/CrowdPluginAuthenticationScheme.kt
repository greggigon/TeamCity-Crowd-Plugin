package teamcity.crowd.plugin

import jetbrains.buildServer.controllers.interceptors.auth.HttpAuthenticationResult
import jetbrains.buildServer.controllers.interceptors.auth.HttpAuthenticationSchemeAdapter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CrowdPluginAuthenticationScheme : HttpAuthenticationSchemeAdapter() {
    override fun doGetName(): String {
        return "Crowd"
    }

    override fun processAuthenticationRequest(request: HttpServletRequest, response: HttpServletResponse, properties: MutableMap<String, String>): HttpAuthenticationResult {

        return super.processAuthenticationRequest(request, response, properties)
    }
}
