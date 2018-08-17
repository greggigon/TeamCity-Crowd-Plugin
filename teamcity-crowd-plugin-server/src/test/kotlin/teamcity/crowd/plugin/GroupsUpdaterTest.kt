package teamcity.crowd.plugin

import jetbrains.buildServer.groups.SUserGroup
import jetbrains.buildServer.groups.UserGroup
import jetbrains.buildServer.groups.UserGroupManager
import jetbrains.buildServer.users.SUser
import org.junit.Test
import org.mockito.Mockito.*
import teamcity.crowd.plugin.utils.GroupNameConverter

class GroupsUpdaterTest {
    private val userGroupManager = mock(UserGroupManager::class.java)
    private val groupNameConverter = mock(GroupNameConverter::class.java)
    private val updater = GroupsUpdater(userGroupManager, groupNameConverter, true, true, FakeLogger())

    private val user = mock(SUser::class.java)

    init {
        `when`(user.username).thenReturn("the-user")
    }

    @Test
    fun shouldCreateNewGroupAndAddUserToItIfGroupNotInTeamCity() {
        // Given
        val newGroupName = "new_group"
        val existingTcGroupName = "in_teamcity"

        val existingTcGroup = mock(UserGroup::class.java)
        val newTCGroup = mock(SUserGroup::class.java)

        `when`(existingTcGroup.name).thenReturn(existingTcGroupName)
        `when`(newTCGroup.name).thenReturn(newGroupName)

        `when`(user.userGroups).thenReturn(listOf(existingTcGroup))
        `when`(groupNameConverter.convert(newGroupName)).thenReturn(newGroupName)
        `when`(userGroupManager.createUserGroup(newGroupName, newGroupName, GroupsUpdater.CREATED_BY_PLUGIN_MESSAGE)).thenReturn(newTCGroup)

        // When
        updater.updateGroups(user, listOf(newGroupName))

        // Then
        verify(userGroupManager).createUserGroup(newGroupName, newGroupName, GroupsUpdater.CREATED_BY_PLUGIN_MESSAGE)
        verify(newTCGroup).addUser(user)
    }

    @Test
    fun shouldUpdateExistingGroupIfGroupAlreadyInTeamCity() {
        // Given
        val newGroupName = "new_group"
        val existingTcGroupName = "in_teamcity"

        val existingTcGroup = mock(UserGroup::class.java)
        val existingGroupThatUserIsNotYetMemberOf = mock(SUserGroup::class.java)

        `when`(existingTcGroup.name).thenReturn(existingTcGroupName)
        `when`(existingGroupThatUserIsNotYetMemberOf.name).thenReturn(newGroupName)

        `when`(user.userGroups).thenReturn(listOf(existingTcGroup))
        `when`(groupNameConverter.convert(newGroupName)).thenReturn(newGroupName)
        `when`(userGroupManager.findUserGroupByName(newGroupName)).thenReturn(existingGroupThatUserIsNotYetMemberOf)

        // When
        updater.updateGroups(user, listOf(newGroupName))

        // Then
        verify(userGroupManager, never()).createUserGroup(newGroupName, newGroupName, GroupsUpdater.CREATED_BY_PLUGIN_MESSAGE)
        verify(existingGroupThatUserIsNotYetMemberOf).addUser(user)
    }

    @Test
    fun shouldNotCreateGroupWhenGroupNotInTeamCityAndConfigurationForItSetToFalse() {
        // Given
        val updater = GroupsUpdater(userGroupManager, groupNameConverter, true, false, FakeLogger())

        val newGroupName = "new_group"
        val existingTcGroupName = "in_teamcity"

        val existingTcGroup = mock(UserGroup::class.java)
        val existingGroupThatUserIsNotYetMemberOf = mock(SUserGroup::class.java)

        `when`(existingTcGroup.name).thenReturn(existingTcGroupName)
        `when`(existingGroupThatUserIsNotYetMemberOf.name).thenReturn(newGroupName)

        `when`(user.userGroups).thenReturn(emptyList())
        `when`(groupNameConverter.convert(newGroupName)).thenReturn(newGroupName)

        // When
        updater.updateGroups(user, listOf(newGroupName))

        // Then
        verify(userGroupManager, never()).createUserGroup(newGroupName, newGroupName, GroupsUpdater.CREATED_BY_PLUGIN_MESSAGE)
    }

    @Test
    fun shouldRemoveUserFromGroupsInTCWhenUserNoLongerInGroupInCrowd() {
        // Given
        val existingTcGroupName = "in_teamcity"

        val existingTcGroup = mock(UserGroup::class.java)
        val existingGroupThatUserShouldBeRemovedFrom = mock(SUserGroup::class.java)

        `when`(existingTcGroup.name).thenReturn(existingTcGroupName)
        `when`(existingTcGroup.description).thenReturn(GroupsUpdater.CREATED_BY_PLUGIN_MESSAGE)
        `when`(user.userGroups).thenReturn(listOf(existingTcGroup))
        `when`(userGroupManager.findUserGroupByName(existingTcGroupName)).thenReturn(existingGroupThatUserShouldBeRemovedFrom)

        // When
        updater.updateGroups(user, emptyList())

        // Then
        verify(existingGroupThatUserShouldBeRemovedFrom).removeUser(user)
    }

    @Test
    fun shouldRemoveUserFromInternalGroupIfConfigurationIsSetToDoSo() {
        // Given
        val updater = GroupsUpdater(userGroupManager, groupNameConverter, false, false, FakeLogger())
        val existingTcGroupName = "in_teamcity"

        val existingTcGroup = mock(UserGroup::class.java)
        val existingGroupThatUserShouldBeRemovedFrom = mock(SUserGroup::class.java)

        `when`(existingTcGroup.name).thenReturn(existingTcGroupName)
        `when`(existingTcGroup.description).thenReturn("bogus")
        `when`(user.userGroups).thenReturn(listOf(existingTcGroup))
        `when`(userGroupManager.findUserGroupByName(existingTcGroupName)).thenReturn(existingGroupThatUserShouldBeRemovedFrom)

        // When
        updater.updateGroups(user, emptyList())

        // Then
        verify(existingGroupThatUserShouldBeRemovedFrom).removeUser(user)
    }



}