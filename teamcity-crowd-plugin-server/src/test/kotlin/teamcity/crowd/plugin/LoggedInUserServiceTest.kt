package teamcity.crowd.plugin

import com.atlassian.crowd.model.user.User
import jetbrains.buildServer.groups.UserGroup
import jetbrains.buildServer.groups.UserGroupManager
import jetbrains.buildServer.users.SUser
import jetbrains.buildServer.users.UserModel
import org.junit.Test
import org.mockito.Mockito.*
import teamcity.crowd.plugin.config.CrowdPluginConfiguration

class LoggedInUserServiceTest {
    private val userGroupManager = mock(UserGroupManager::class.java)
    private val pluginCrowdClient = mock(PluginCrowdClient::class.java)
    private val userModel = mock(UserModel::class.java)
    private val loggerFactory = FakeLogger()
    private val crowdUser = mock(User::class.java)
    private val teamCityAccount = mock(SUser::class.java)
    private val theUser = mock(jetbrains.buildServer.users.User::class.java)
    private val userUpdater = mock(GroupsUpdater::class.java)

    private val username = "the-username"


    init {
        `when`(crowdUser.name).thenReturn(username)
        `when`(crowdUser.displayName).thenReturn("the dooood")
        `when`(crowdUser.emailAddress).thenReturn("dude@dude.com")

        `when`(teamCityAccount.username).thenReturn(username)
        `when`(teamCityAccount.associatedUser).thenReturn(theUser)
    }


    @Test
    fun shouldCreateNewUserIfUserNotInTeamCityYet() {
        // Given
        val userService = LoggedInUserService(pluginCrowdClient, userModel, userUpdater, loggerFactory)
        `when`(userGroupManager.findUserGroupByKey(crowdUser.name)).thenReturn(null)
        `when`(userModel.createUserAccount(CrowdPluginConfiguration.CROWD_REALM, crowdUser.name)).thenReturn(teamCityAccount)

        // When
        userService.updateMemebership(crowdUser)

        // Then
        verify(userModel).createUserAccount(CrowdPluginConfiguration.CROWD_REALM, crowdUser.name)
        verify(teamCityAccount).updateUserAccount(crowdUser.name, crowdUser.displayName, crowdUser.emailAddress)
    }

    @Test
    fun shouldUpdateUserDetailsIfAccountExistsInTeamCity() {
        // Given
        val userService = LoggedInUserService(pluginCrowdClient, userModel, userUpdater, loggerFactory)
        `when`(userModel.findUserAccount(CrowdPluginConfiguration.CROWD_REALM, crowdUser.name)).thenReturn(teamCityAccount)

        // When
        userService.updateMemebership(crowdUser)

        // Then
        verify(userModel, never()).createUserAccount(anyString(), anyString())
        verify(teamCityAccount).updateUserAccount(crowdUser.name, crowdUser.displayName, crowdUser.emailAddress)
    }

}
