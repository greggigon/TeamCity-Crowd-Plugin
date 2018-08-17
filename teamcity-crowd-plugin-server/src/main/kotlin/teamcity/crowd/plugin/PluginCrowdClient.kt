package teamcity.crowd.plugin

import com.atlassian.crowd.exception.*
import com.atlassian.crowd.model.group.Group
import com.atlassian.crowd.model.user.User
import com.atlassian.crowd.service.client.CrowdClient
import com.intellij.openapi.diagnostic.Logger
import teamcity.crowd.plugin.utils.LoggerFactory
import com.atlassian.crowd.exception.ApplicationPermissionException
import com.atlassian.crowd.exception.InvalidAuthenticationException
import com.atlassian.crowd.service.client.ClientProperties
import com.atlassian.crowd.service.factory.CrowdClientFactory

interface PluginCrowdClient {
    fun loginUserWithPassword(username: String, password: String): User?

    fun getUserGroups(username: String): Collection<Group>

    companion object {
        const val APPLICATION_PERMISSIONS_MESSAGE = "Crowd client permissions are incorrect. Configuration details are incorrect."
        const val INVALID_AUTHENTICATION_MESSAGE = "Plugin can't authenticate with Crowd. Configuration details are incorrect."
        const val OPERATION_FAILED_MESSAGE = "Bummer. Something went wrong. Can't talk to Crowd at all."
        const val UNKNOWN_ERROR_MESSAGE = "Bummer. Failed with unknown reasons!"
    }
}

class TeamCityPluginCrowdClientFactory(
        private val crowdClientFactory: CrowdClientFactory,
        private val clientProperties: ClientProperties){

    fun newInstance(): CrowdClient = crowdClientFactory.newInstance(clientProperties)
}


class TeamCityPluginCrowdClient(private val crowdClient: CrowdClient, loggerFactory: LoggerFactory) : PluginCrowdClient {
    private val logger: Logger = loggerFactory.getServerLogger()

    override fun loginUserWithPassword(username: String, password: String): User? {
        try {
            return crowdClient.authenticateUser(username, password)
        } catch (e: UserNotFoundException) {
            logger.warn("User with name [$username] doesn't exists.", e)
        } catch (e: InactiveAccountException) {
            logger.info("User account [$username] is inactive", e)
        } catch (e: ExpiredCredentialException) {
            logger.info("User [$username] credentials expired", e)
        } catch (e: ApplicationPermissionException) {
            logger.error(PluginCrowdClient.APPLICATION_PERMISSIONS_MESSAGE, e)
        } catch (e: InvalidAuthenticationException) {
            logger.error(PluginCrowdClient.INVALID_AUTHENTICATION_MESSAGE, e)
        } catch (e: OperationFailedException) {
            logger.error(PluginCrowdClient.OPERATION_FAILED_MESSAGE, e)
        } catch (e: Exception) {
            logger.error(PluginCrowdClient.UNKNOWN_ERROR_MESSAGE, e)
        }
        return null
    }

    override fun getUserGroups(username: String): Collection<Group> {
        try {
            return crowdClient.getGroupsForUser(username, 0, Integer.MAX_VALUE)
        } catch (e: UserNotFoundException) {
            logger.warn("User with name [$username] doesn't exists.", e)
        } catch (e: OperationFailedException) {
            logger.error(PluginCrowdClient.OPERATION_FAILED_MESSAGE, e)
        } catch (e: InvalidAuthenticationException) {
            logger.error(PluginCrowdClient.INVALID_AUTHENTICATION_MESSAGE, e)
        } catch (e: ApplicationPermissionException) {
            logger.error(PluginCrowdClient.APPLICATION_PERMISSIONS_MESSAGE, e)
        } catch (e: RuntimeException) {
            logger.error(PluginCrowdClient.UNKNOWN_ERROR_MESSAGE, e)
        }
        return emptyList()
    }

}