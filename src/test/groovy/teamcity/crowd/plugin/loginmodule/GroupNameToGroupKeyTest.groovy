package teamcity.crowd.plugin.loginmodule

import com.google.common.base.Optional
import jetbrains.buildServer.groups.SUserGroup
import jetbrains.buildServer.groups.UserGroupManager
import org.junit.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class GroupNameToGroupKeyTest {
    private def UserGroupManager userGroupManager = mock(UserGroupManager)
    private def groupToKey = new GroupNameToGroupKey(userGroupManager)

    @Test
    void 'should trim key to be 16 characters'(){
        assert groupToKey.transform('longer-name than it should').get() == 'LONGER_NAME_THAN'
    }

    @Test
    void 'should replace - wih _'(){
        assert groupToKey.transform('some-name').get() == 'SOME_NAME'
    }

    @Test
    void 'should replace spaces in group names into _'(){
        assert groupToKey.transform('some name').get() == 'SOME_NAME'
    }

    @Test
    void 'should upcase the group name into key'(){
         assert groupToKey.transform('foobar').get() == 'FOOBAR'
    }

    @Test
    void 'should return 16 char name'(){
        assert groupToKey.transform('1234567890ABCDEF').get() == '1234567890ABCDEF'
    }

    @Test
    void 'should return a unique key if default exists when truncating'(){
        when(userGroupManager.findUserGroupByKey('1234567890ABCDEF')).thenReturn(mock(SUserGroup))
        assert groupToKey.transform('1234567890ABCDEFX').get() == '1234567890ABC000'
    }

    @Test
    void 'should return a new unique key if first attempt exists'(){
        when(userGroupManager.findUserGroupByKey('1234567890ABCDEF')).thenReturn(mock(SUserGroup))
        when(userGroupManager.findUserGroupByKey('1234567890ABC000')).thenReturn(mock(SUserGroup))

        assert groupToKey.transform('1234567890ABCDEFX').get() == '1234567890ABC001'
        assert groupToKey.transform('1234567890ABCDEF').get() == '1234567890ABC001'
        assert groupToKey.transform('1234567890ABC000').get() == '1234567890ABC001'
    }

    @Test
    void 'should return absent when 1000 groups exist with the same prefix'() {
        when(userGroupManager.findUserGroupByKey('1234567890ABCDEF')).thenReturn(mock(SUserGroup))
        for (int i = 0; i < 1000; i++) {
            when(userGroupManager.findUserGroupByKey('1234567890ABC' + String.format("%03d", i))).thenReturn(mock(SUserGroup))
        }

        assert groupToKey.transform('1234567890ABCDEFX') == Optional.absent()
        assert groupToKey.transform('1234567890ABCDEF') == Optional.absent()
        assert groupToKey.transform('1234567890ABC000') == Optional.absent()
    }
}