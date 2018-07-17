package teamcity.crowd.plugin

import jetbrains.buildServer.groups.UserGroupManager
import jetbrains.buildServer.users.SUser
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.mock

class GroupsUpdaterTest {
    private val userGroupManager = mock(UserGroupManager::class.java)
    private val user = mock(SUser::class.java)

    @Test
    fun shouldLeaveAllExistingGroupsWhenUpdating(){



    }

}