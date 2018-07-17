package teamcity.crowd.plugin

import com.atlassian.crowd.exception.*
import com.atlassian.crowd.integration.rest.entity.GroupEntity
import com.atlassian.crowd.model.group.GroupType
import com.atlassian.crowd.model.user.User
import com.atlassian.crowd.service.client.CrowdClient
import com.intellij.openapi.diagnostic.Logger
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import teamcity.crowd.plugin.utils.LoggerFactory

class TeamCityPluginCrowdClientTest {
    private val username = "username"
    private val password = "pass"

    private val loggerFactory = mock(LoggerFactory::class.java)
    private val logger = mock(Logger::class.java)

    @Test
    fun shouldAutheticateUser() {
        // Given
        val crowdClient = mock(CrowdClient::class.java)
        val user = mock(User::class.java)
        val loggerFactory = FakeLogger()

        val tcCrowdClient = TeamCityPluginCrowdClient(crowdClient, loggerFactory)

        Mockito.`when`(tcCrowdClient.loginUserWithPassword(username, password)).thenReturn(user)

        // When
        val theUser = tcCrowdClient.loginUserWithPassword(username, password)

        // Assert
        assertEquals(user, theUser)
    }

    @Test
    fun shouldLogWarningAndReturnNullIfUserNotFound() {
        // Given
        val crowdClient = mock(CrowdClient::class.java)

        Mockito.`when`(loggerFactory.getServerLogger()).thenReturn(logger)

        val tcCrowdClient = TeamCityPluginCrowdClient(crowdClient, loggerFactory)

        val userNotFoundException = UserNotFoundException("User not found")
        Mockito.`when`(crowdClient.authenticateUser(username, password)).thenThrow(userNotFoundException)

        // When
        val theUser = tcCrowdClient.loginUserWithPassword(username, password)

        // Then
        assertNull(theUser)
        verify(logger).warn("User with name [username] doesn't exists.", userNotFoundException)
    }

    @Test
    fun shouldLogInfoWhenUserAccountIsInactiveAndReturnNull() {
        // Given
        val crowdClient = mock(CrowdClient::class.java)

        Mockito.`when`(loggerFactory.getServerLogger()).thenReturn(logger)

        val tcCrowdClient = TeamCityPluginCrowdClient(crowdClient, loggerFactory)

        val inactiveAccountException = InactiveAccountException("Account inactive")
        Mockito.`when`(crowdClient.authenticateUser(username, password)).thenThrow(inactiveAccountException)

        // When
        val theUser = tcCrowdClient.loginUserWithPassword(username, password)

        // Then
        assertNull(theUser)
        verify(logger).info("User account [username] is inactive", inactiveAccountException)
    }

    @Test
    fun shouldLogInfoWhenUserCredentialsExpiredAndReturnNull() {
        // Given
        val crowdClient = mock(CrowdClient::class.java)

        Mockito.`when`(loggerFactory.getServerLogger()).thenReturn(logger)

        val tcCrowdClient = TeamCityPluginCrowdClient(crowdClient, loggerFactory)

        val expiredCredentialException = ExpiredCredentialException("Creds expired")
        Mockito.`when`(crowdClient.authenticateUser(username, password)).thenThrow(expiredCredentialException)

        // When
        val theUser = tcCrowdClient.loginUserWithPassword(username, password)

        // Then
        assertNull(theUser)
        verify(logger).info("User [username] credentials expired", expiredCredentialException)
    }

    @Test
    fun shouldLogErrorWhenCrowdClientApplicationPermissionsAreWrongAndReturnNull() {
        // Given
        val crowdClient = mock(CrowdClient::class.java)

        Mockito.`when`(loggerFactory.getServerLogger()).thenReturn(logger)

        val tcCrowdClient = TeamCityPluginCrowdClient(crowdClient, loggerFactory)

        val appPermissionsException = ApplicationPermissionException("App has no permissions")
        Mockito.`when`(crowdClient.authenticateUser(username, password)).thenThrow(appPermissionsException)

        // When
        val theUser = tcCrowdClient.loginUserWithPassword(username, password)

        // Then
        assertNull(theUser)
        verify(logger).error(PluginCrowdClient.APPLICATION_PERMISSIONS_MESSAGE, appPermissionsException)
    }

    @Test
    fun shouldLogErrorWhenCrowdClientAuthenticationFailsAndReturnNull() {
        // Given
        val crowdClient = mock(CrowdClient::class.java)

        Mockito.`when`(loggerFactory.getServerLogger()).thenReturn(logger)

        val tcCrowdClient = TeamCityPluginCrowdClient(crowdClient, loggerFactory)

        val invalidAuthException = InvalidAuthenticationException("Application authentication issue")
        Mockito.`when`(crowdClient.authenticateUser(username, password)).thenThrow(invalidAuthException)

        // When
        val theUser = tcCrowdClient.loginUserWithPassword(username, password)

        // Then
        assertNull(theUser)
        verify(logger).error(PluginCrowdClient.INVALID_AUTHENTICATION_MESSAGE, invalidAuthException)
    }

    @Test
    fun shouldLogErrorWhenCrowdOperationFailsAndReturnNull() {
        // Given
        val crowdClient = mock(CrowdClient::class.java)

        Mockito.`when`(loggerFactory.getServerLogger()).thenReturn(logger)

        val tcCrowdClient = TeamCityPluginCrowdClient(crowdClient, loggerFactory)

        val operationFailed = OperationFailedException("Bonkers!")
        Mockito.`when`(crowdClient.authenticateUser(username, password)).thenThrow(operationFailed)

        // When
        val theUser = tcCrowdClient.loginUserWithPassword(username, password)

        // Then
        assertNull(theUser)
        verify(logger).error(PluginCrowdClient.OPERATION_FAILED_MESSAGE, operationFailed)
    }

    @Test
    fun shouldLogErrorWhenFailsWithUnknownReasonAndReturnNull() {
        // Given
        val crowdClient = mock(CrowdClient::class.java)

        Mockito.`when`(loggerFactory.getServerLogger()).thenReturn(logger)

        val tcCrowdClient = TeamCityPluginCrowdClient(crowdClient, loggerFactory)

        val unknownException = RuntimeException("Bonkers!")
        Mockito.`when`(crowdClient.authenticateUser(username, password)).thenThrow(unknownException)

        // When
        val theUser = tcCrowdClient.loginUserWithPassword(username, password)

        // Then
        assertNull(theUser)
        verify(logger).error(PluginCrowdClient.UNKNOWN_ERROR_MESSAGE, unknownException)
    }

    @Test
    fun shouldReturnUsersGroups() {
        // Given
        val crowdClient = mock(CrowdClient::class.java)

        val tcCrowdClient = TeamCityPluginCrowdClient(crowdClient, FakeLogger())
        val theGroup = GroupEntity(username, "", GroupType.GROUP, true)
        Mockito.`when`(crowdClient.getGroupsForUser(username, 0, Int.MAX_VALUE)).thenReturn(listOf(theGroup))

        // When
        val userGroups = tcCrowdClient.getUserGroups(username)

        // Then
        assertEquals(1, userGroups.size)
        assertEquals(theGroup, userGroups.first())
    }

    @Test
    fun shouldLogWarningAndReturnEmptyListWhenRetrievingGroupsForUserThatIsNotInCrowd() {
        // Given
        val crowdClient = mock(CrowdClient::class.java)
        Mockito.`when`(loggerFactory.getServerLogger()).thenReturn(logger)
        val tcCrowdClient = TeamCityPluginCrowdClient(crowdClient, loggerFactory)

        val userNotFoundException = UserNotFoundException("User not in crowd")
        Mockito.`when`(crowdClient.getGroupsForUser(username, 0, Int.MAX_VALUE)).thenThrow(userNotFoundException)

        // When
        val userGroups = tcCrowdClient.getUserGroups(username)

        // Then
        assertTrue(userGroups.isEmpty())
        verify(logger).warn("User with name [$username] doesn't exists.", userNotFoundException)
    }


    @Test
    fun shouldLogErrorAndReturnEmptyListWhenOperationInCrowdFailed() {
        // Given
        val crowdClient = mock(CrowdClient::class.java)
        Mockito.`when`(loggerFactory.getServerLogger()).thenReturn(logger)
        val tcCrowdClient = TeamCityPluginCrowdClient(crowdClient, loggerFactory)

        val operationFailedException = OperationFailedException("FooBar")
        Mockito.`when`(crowdClient.getGroupsForUser(username, 0, Int.MAX_VALUE)).thenThrow(operationFailedException)

        // When
        val userGroups = tcCrowdClient.getUserGroups(username)

        // Then
        assertTrue(userGroups.isEmpty())
        verify(logger).error(PluginCrowdClient.OPERATION_FAILED_MESSAGE, operationFailedException)
    }

    @Test
    fun shouldLogErrorAndReturnEmptyListWhenCrowdApplicationAuthenticationFailed() {
        // Given
        val crowdClient = mock(CrowdClient::class.java)
        Mockito.`when`(loggerFactory.getServerLogger()).thenReturn(logger)
        val tcCrowdClient = TeamCityPluginCrowdClient(crowdClient, loggerFactory)

        val invalidAuthException = InvalidAuthenticationException("Bonkers")
        Mockito.`when`(crowdClient.getGroupsForUser(username, 0, Int.MAX_VALUE)).thenThrow(invalidAuthException)

        // When
        val userGroups = tcCrowdClient.getUserGroups(username)

        // Then
        assertTrue(userGroups.isEmpty())
        verify(logger).error(PluginCrowdClient.INVALID_AUTHENTICATION_MESSAGE, invalidAuthException)
    }

    @Test
    fun shouldLogErrorAndReturnEmptyListWhenCrowdApplicationHasInvalidPermissions() {
        // Given
        val crowdClient = mock(CrowdClient::class.java)
        Mockito.`when`(loggerFactory.getServerLogger()).thenReturn(logger)
        val tcCrowdClient = TeamCityPluginCrowdClient(crowdClient, loggerFactory)

        val appPermissionsException = ApplicationPermissionException("Permissions are wrond")
        Mockito.`when`(crowdClient.getGroupsForUser(username, 0, Int.MAX_VALUE)).thenThrow(appPermissionsException)

        // When
        val userGroups = tcCrowdClient.getUserGroups(username)

        // Then
        assertTrue(userGroups.isEmpty())
        verify(logger).error(PluginCrowdClient.APPLICATION_PERMISSIONS_MESSAGE, appPermissionsException)
    }

    @Test
    fun shouldLogErrorAndReturnEmptyListWhenUnknownErrorHappens() {
        // Given
        val crowdClient = mock(CrowdClient::class.java)
        Mockito.`when`(loggerFactory.getServerLogger()).thenReturn(logger)
        val tcCrowdClient = TeamCityPluginCrowdClient(crowdClient, loggerFactory)

        val unknownError = RuntimeException("Permissions are wrond")
        Mockito.`when`(crowdClient.getGroupsForUser(username, 0, Int.MAX_VALUE)).thenThrow(unknownError)

        // When
        val userGroups = tcCrowdClient.getUserGroups(username)

        // Then
        assertTrue(userGroups.isEmpty())
        verify(logger).error(PluginCrowdClient.UNKNOWN_ERROR_MESSAGE, unknownError)
    }


}

