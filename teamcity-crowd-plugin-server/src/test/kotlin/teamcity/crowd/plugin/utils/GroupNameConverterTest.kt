package teamcity.crowd.plugin.utils

import jetbrains.buildServer.groups.SUserGroup
import jetbrains.buildServer.groups.UserGroupManager
import org.junit.Assert.*
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.mock

class GroupNameConverterTest {
    private val userGroupManager: UserGroupManager = mock(UserGroupManager::class.java)
    private val converter = GroupNameConverter(userGroupManager)

    @Test
    fun shouldTrimKeyToBe16Characters() {
        assertEquals("LONGER_NAME_THAN", converter.convert("longer-name than it should"))
    }

    @Test
    fun shouldReplaceDASHESWithUNDERSCORES() {
        assertEquals("SOME_NAME", converter.convert("some-name"))
    }

    @Test
    fun shouldReplaceSPACESWithUNDERSCORES() {
        assertEquals("SOME_NAME", converter.convert("some name"))
    }

    @Test
    fun shouldUpercaseGroupName() {
        assertEquals("FOOBAR", converter.convert("foobar"))
    }

    @Test
    fun shouldReturn16CharacterName() {
        assertEquals("1234567890ABCDEF", converter.convert("1234567890ABCDEF"))
    }

    @Test
    fun shouldReturnAUniqueKeyIfDefaultAlreadyExistsWhenTruncating() {
        Mockito.`when`(userGroupManager.findUserGroupByKey("1234567890ABCDEF")).thenReturn(mock(SUserGroup::class.java))
        assertEquals("1234567890ABC000", converter.convert("1234567890ABCDEFX"))
    }

    @Test
    fun shouldReturnAUniqueKeyIfFirstAttemptExists() {
        Mockito.`when`(userGroupManager.findUserGroupByKey("1234567890ABCDEF")).thenReturn(mock(SUserGroup::class.java))
        Mockito.`when`(userGroupManager.findUserGroupByKey("1234567890ABC000")).thenReturn(mock(SUserGroup::class.java))

        assertEquals("1234567890ABC001", converter.convert("1234567890ABCDEFX"))
        assertEquals("1234567890ABC001", converter.convert("1234567890ABCDEF"))
        assertEquals("1234567890ABC001", converter.convert("1234567890ABC000"))
    }

    @Test
    fun shouldReturnNullWhen1000GroupsExistsWithTheProposedName() {
        Mockito.`when`(userGroupManager.findUserGroupByKey(ArgumentMatchers.anyString())).thenReturn(mock(SUserGroup::class.java))

        assertNull(converter.convert("1234567890ABCDEFX"))
        assertNull(converter.convert("1234567890ABCDEF"))
        assertNull(converter.convert("1234567890ABC000"))
    }
}