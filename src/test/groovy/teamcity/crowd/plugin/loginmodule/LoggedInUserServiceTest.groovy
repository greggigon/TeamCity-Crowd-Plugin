package teamcity.crowd.plugin.loginmodule
import com.atlassian.crowd.integration.rest.entity.GroupEntity
import com.atlassian.crowd.model.group.Group
import com.atlassian.crowd.model.group.GroupType
import com.atlassian.crowd.model.user.User
import jetbrains.buildServer.groups.SUserGroup
import jetbrains.buildServer.groups.UserGroupManager
import jetbrains.buildServer.users.SUser
import jetbrains.buildServer.users.UserModel
import org.junit.Before
import org.junit.Test
import teamcity.crowd.plugin.CrowdPluginAuthenticationScheme
import teamcity.crowd.plugin.PluginCrowdClient
import teamcity.crowd.plugin.mocks.MockLogger
import teamcity.crowd.plugin.utils.CrowdPluginConfiguration

import static org.mockito.Mockito.*

class LoggedInUserServiceTest {
    private final def user = [getName: { 'gregster' }, getDisplayName: { 'gregster' }, getEmailAddress: { 'gregster@greggigon.com' }] as User
    private final def provider = new MockLogger()
    private final def crowdPluginConfiguration = mock(CrowdPluginConfiguration)

    @Before
    void setUp(){
        when(crowdPluginConfiguration.shouldCreateGroups()).thenReturn(false)
    }

    @Test
    void 'should Create Groups when creating user if configuration is set to Create Groups (true)'() {
        def groupManager = mock(UserGroupManager)
        def pluginCrowdClient = mock(PluginCrowdClient)
        def userModel = mock(UserModel)
        def teamCityUser = mock(SUser)
        def configuration = mock(CrowdPluginConfiguration)
        def createdGroup = mock(SUserGroup)

        def gregsGroup = new GroupEntity('group1', '', GroupType.GROUP, true)


        when(configuration.shouldCreateGroups()).thenReturn(true)
        when(teamCityUser.getAssociatedUser()).thenReturn([getName:{'gregster'}, getEmail: {'gregster@greggigon.com'}] as jetbrains.buildServer.users.User)
        when(teamCityUser.getUsername()).thenReturn('gregster')
        when(userModel.findUserAccount(CrowdPluginAuthenticationScheme.REALM, user.getName())).thenReturn(teamCityUser)

        when(pluginCrowdClient.getUserGroups('gregster')).thenReturn([gregsGroup] as List<Group>)
        when(groupManager.createUserGroup('GROUP1', 'group1', 'Created by Crowd Plugin')).thenReturn(createdGroup)

        def loggedInUserService = new LoggedInUserService(groupManager, pluginCrowdClient, userModel, provider, configuration)
        loggedInUserService.updateMembership(user)

        verify(groupManager).createUserGroup('GROUP1', 'group1', 'Created by Crowd Plugin')
        verify(createdGroup).addUser(teamCityUser)
    }

    @Test
    void 'should Create Groups with unique key adding a group with by default would match an existing key'() {
        def groupManager = mock(UserGroupManager)
        def pluginCrowdClient = mock(PluginCrowdClient)
        def userModel = mock(UserModel)
        def teamCityUser = mock(SUser)
        def configuration = mock(CrowdPluginConfiguration)
        def createdGroup = mock(SUserGroup)
        def createdOtherGroup = mock(SUserGroup)

        def gregsGroup = new GroupEntity('1234567890abcdef', '', GroupType.GROUP, true)
        def gregsOtherGroup = new GroupEntity('1234567890abcdef_Another', '', GroupType.GROUP, true)


        when(configuration.shouldCreateGroups()).thenReturn(true)
        when(teamCityUser.getAssociatedUser()).thenReturn([getName:{'gregster'}, getEmail: {'gregster@greggigon.com'}] as jetbrains.buildServer.users.User)
        when(teamCityUser.getUsername()).thenReturn('gregster')
        when(userModel.findUserAccount(CrowdPluginAuthenticationScheme.REALM, user.getName())).thenReturn(teamCityUser)

        when(pluginCrowdClient.getUserGroups('gregster')).thenReturn([gregsGroup, gregsOtherGroup] as List<Group>)
        when(groupManager.createUserGroup('1234567890ABCDEF', '1234567890abcdef', 'Created by Crowd Plugin')).thenReturn(createdGroup)
        when(groupManager.createUserGroup('1234567890ABC000', '1234567890abcdef_Another', 'Created by Crowd Plugin')).thenReturn(createdOtherGroup)
        when(groupManager.findUserGroupByKey('1234567890ABCDEF')).thenReturn(null).thenReturn(createdGroup)

        def loggedInUserService = new LoggedInUserService(groupManager, pluginCrowdClient, userModel, provider, configuration)
        loggedInUserService.updateMembership(user)

        verify(groupManager).createUserGroup('1234567890ABCDEF', '1234567890abcdef', 'Created by Crowd Plugin')
        verify(groupManager).createUserGroup('1234567890ABC000', '1234567890abcdef_Another', 'Created by Crowd Plugin')
    }

    @Test
    void 'will not create group when there are 1000 groups with the same 13 char prefix'() {
        def groupManager = mock(UserGroupManager)
        def pluginCrowdClient = mock(PluginCrowdClient)
        def userModel = mock(UserModel)
        def teamCityUser = mock(SUser)
        def configuration = mock(CrowdPluginConfiguration)
        def createdGroup = mock(SUserGroup)

        def gregsGroup = new GroupEntity('1234567890ABCDEFX', '', GroupType.GROUP, true)


        when(configuration.shouldCreateGroups()).thenReturn(true)
        when(teamCityUser.getAssociatedUser()).thenReturn([getName:{'gregster'}, getEmail: {'gregster@greggigon.com'}] as jetbrains.buildServer.users.User)
        when(teamCityUser.getUsername()).thenReturn('gregster')
        when(userModel.findUserAccount(CrowdPluginAuthenticationScheme.REALM, user.getName())).thenReturn(teamCityUser)

        when(pluginCrowdClient.getUserGroups('gregster')).thenReturn([gregsGroup] as List<Group>)
        when(groupManager.findUserGroupByKey('1234567890ABCDEF')).thenReturn(mock(SUserGroup))
        for (int i = 0; i < 1000; i++) {
            when(groupManager.findUserGroupByKey('1234567890ABC' + String.format("%03d", i))).thenReturn(mock(SUserGroup))
        }

        def loggedInUserService = new LoggedInUserService(groupManager, pluginCrowdClient, userModel, provider, configuration)
        loggedInUserService.updateMembership(user)
    }

    @Test
    void 'should NOT update user details on successful login if details are the same'() {
        def groupManager = mock(UserGroupManager)
        def pluginCrowdClient = mock(PluginCrowdClient)
        def userModel = mock(UserModel)
        def teamCityUser = mock(SUser)

        when(teamCityUser.getAssociatedUser()).thenReturn([getName: { 'gregster' }, getEmail: { 'gregster@greggigon.com' }] as jetbrains.buildServer.users.User)
        when(userModel.findUserAccount(CrowdPluginAuthenticationScheme.REALM, user.getName())).thenReturn(teamCityUser)
        when(pluginCrowdClient.getUserGroups(user.getName())).thenReturn([] as List<Group>)

        def loggedInUserService = new LoggedInUserService(groupManager, pluginCrowdClient, userModel, provider, crowdPluginConfiguration)

        loggedInUserService.updateMembership(user)

        verify(teamCityUser, never()).updateUserAccount('gregster', 'gregster', 'gregster@greggigon.com')
    }

    @Test
    void 'should update user details on successful login'() {
        def groupManager = mock(UserGroupManager)
        def pluginCrowdClient = mock(PluginCrowdClient)
        def userModel = mock(UserModel)
        def teamCityUser = mock(SUser)

        when(teamCityUser.getAssociatedUser()).thenReturn([getName: { 'not gregster' }, getEmail: { 'not-gregster@greggigon.com' }] as jetbrains.buildServer.users.User)
        when(userModel.findUserAccount(CrowdPluginAuthenticationScheme.REALM, user.getName())).thenReturn(teamCityUser)
        when(pluginCrowdClient.getUserGroups(user.getName())).thenReturn([] as List<Group>)

        def loggedInUserService = new LoggedInUserService(groupManager, pluginCrowdClient, userModel, provider, crowdPluginConfiguration)

        loggedInUserService.updateMembership(user)

        verify(teamCityUser).updateUserAccount('gregster', 'gregster', 'gregster@greggigon.com')
    }

    @Test
    void 'should add user to new Group and remove from existing'() {
        def groupManager = mock(UserGroupManager)
        def pluginCrowdClient = mock(PluginCrowdClient)
        def userModel = mock(UserModel)
        def teamCityUser = mock(SUser)
        def allUsersGroup = mock(SUserGroup)

        when(teamCityUser.getAssociatedUser()).thenReturn([getName:{'gregster'}, getEmail: {'gregster@greggigon.com'}] as jetbrains.buildServer.users.User)
        when(teamCityUser.getUsername()).thenReturn('gregster')
        when(allUsersGroup.getName()).thenReturn(LoggedInUserService.ALL_USERS_GROUP)

        when(teamCityUser.getUserGroups()).thenReturn([allUsersGroup] as List<SUserGroup>)
        when(userModel.findUserAccount(CrowdPluginAuthenticationScheme.REALM, user.getName())).thenReturn(teamCityUser)
        when(pluginCrowdClient.getUserGroups('gregster')).thenReturn([] as List<Group>)

        new LoggedInUserService(groupManager, pluginCrowdClient, userModel, provider, crowdPluginConfiguration).updateMembership(user)

        verify(allUsersGroup, never()).removeUser(teamCityUser)
    }

    @Test
    void 'should remove user from a group he is no longer a member of'() {
        def groupManager = mock(UserGroupManager)
        def pluginCrowdClient = mock(PluginCrowdClient)
        def userModel = mock(UserModel)
        def teamCityUser = mock(SUser)
        def group = mock(SUserGroup)

        when(teamCityUser.getAssociatedUser()).thenReturn([getName: { 'gregster' }, getEmail: { 'gregster@greggigon.com' }] as jetbrains.buildServer.users.User)
        when(teamCityUser.getUsername()).thenReturn('gregster')
        when(group.getName()).thenReturn('groupName')
        when(teamCityUser.getUserGroups()).thenReturn([group] as List<SUserGroup>)
        when(userModel.findUserAccount(CrowdPluginAuthenticationScheme.REALM, user.getName())).thenReturn(teamCityUser)
        when(pluginCrowdClient.getUserGroups('gregster')).thenReturn([] as List<Group>)
        when(groupManager.findUserGroupByName('groupName')).thenReturn(group)

        new LoggedInUserService(groupManager, pluginCrowdClient, userModel, provider, crowdPluginConfiguration).updateMembership(user)

        verify(group).removeUser(teamCityUser)
    }

    @Test
    void 'should not add user to group if it is not in TeamCity'() {
        def groupManager = mock(UserGroupManager)
        def pluginCrowdClient = mock(PluginCrowdClient)
        def userModel = mock(UserModel)
        def teamCityUser = mock(SUser)

        when(teamCityUser.getAssociatedUser()).thenReturn([getName: { 'gregster' }, getEmail: { 'gregster@greggigon.com' }] as jetbrains.buildServer.users.User)
        when(teamCityUser.getUsername()).thenReturn('gregster')
        when(userModel.findUserAccount(CrowdPluginAuthenticationScheme.REALM, user.getName())).thenReturn(teamCityUser)
        when(pluginCrowdClient.getUserGroups('gregster')).thenReturn([[getName: { 'groupName' }] as Group] as List<Group>)
        when(groupManager.findUserGroupByName('groupName')).thenReturn(null)

        def loggedInUserService = new LoggedInUserService(groupManager, pluginCrowdClient, userModel, provider, crowdPluginConfiguration)

        def principle = loggedInUserService.updateMembership(user)
        assert principle
    }

    @Test
    void 'should update user groups memberships'() {
        def groupManager = mock(UserGroupManager)
        def pluginCrowdClient = mock(PluginCrowdClient)
        def userModel = mock(UserModel)
        def teamCityUser = mock(SUser)
        def group = mock(SUserGroup)

        when(teamCityUser.getAssociatedUser()).thenReturn([getName: { 'gregsterg' }, getEmail: { 'gregster@greggigon.com' }] as jetbrains.buildServer.users.User)
        when(teamCityUser.getUsername()).thenReturn('gregster')
        when(userModel.findUserAccount(CrowdPluginAuthenticationScheme.REALM, user.getName())).thenReturn(teamCityUser)
        when(pluginCrowdClient.getUserGroups('gregster')).thenReturn([[getName: { 'groupName' }] as Group] as List<Group>)
        when(groupManager.findUserGroupByName('groupName')).thenReturn(group)

        def loggedInUserService = new LoggedInUserService(groupManager, pluginCrowdClient, userModel, provider, crowdPluginConfiguration)

        loggedInUserService.updateMembership(user)

        verify(group).addUser(teamCityUser)
    }

    @Test
    void 'should create new user if it does not exists on login'() {
        def groupManager = mock(UserGroupManager)
        def pluginCrowdClient = mock(PluginCrowdClient)
        def userModel = mock(UserModel)
        def createdUser = mock(SUser)

        when(userModel.findUserAccount(CrowdPluginAuthenticationScheme.REALM, user.getName())).thenReturn(null)
        when(userModel.createUserAccount(CrowdPluginAuthenticationScheme.REALM, 'gregster')).thenReturn(createdUser)

        when(pluginCrowdClient.getUserGroups(user.getName())).thenReturn([] as List<Group>)

        def loggedInUserService = new LoggedInUserService(groupManager, pluginCrowdClient, userModel, provider, crowdPluginConfiguration)

        def principle = loggedInUserService.updateMembership(user)

        assert principle.name == user.name

        verify(userModel).createUserAccount(CrowdPluginAuthenticationScheme.REALM, 'gregster')
        verify(createdUser).updateUserAccount('gregster', 'gregster', 'gregster@greggigon.com')
    }

    @Test
    void 'should return server principle when logged in user'() {
        def groupManager = mock(UserGroupManager)
        def pluginCrowdClient = mock(PluginCrowdClient)
        def userModel = mock(UserModel)
        def teamCityUser = mock(SUser)


        when(teamCityUser.getAssociatedUser()).thenReturn([getName:{'gregster'}, getEmail: {'gregster@greggigon.com'}] as jetbrains.buildServer.users.User)
        when(userModel.findUserAccount(CrowdPluginAuthenticationScheme.REALM, user.getName())).thenReturn(teamCityUser)
        when(pluginCrowdClient.getUserGroups(user.getName())).thenReturn([] as List<Group>)

        def loggedInUserService = new LoggedInUserService(groupManager, pluginCrowdClient, userModel, provider, crowdPluginConfiguration)

        def principle = loggedInUserService.updateMembership(user)

        assert principle.name == user.name
    }
}
