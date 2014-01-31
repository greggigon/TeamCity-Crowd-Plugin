package teamcity.crowd.plugin.loginmodule

import org.junit.Test

class GroupNameToGroupKeyTest {
    private def groupToKey = new GroupNameToGroupKey()

    @Test
    void 'should trim key to be 16 characters'(){
        assert groupToKey.transform('longer-name than it should') == 'LONGER_NAME_THAN'
    }

    @Test
    void 'should replace - wih _'(){
        assert groupToKey.transform('some-name') == 'SOME_NAME'
    }

    @Test
    void 'should replace spaces in group names into _'(){
        assert groupToKey.transform('some name') == 'SOME_NAME'
    }

    @Test
    void 'should upcase the group name into key'(){
         assert groupToKey.transform('foobar') == 'FOOBAR'
    }
}
