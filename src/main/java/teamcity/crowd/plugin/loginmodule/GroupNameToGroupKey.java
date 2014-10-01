package teamcity.crowd.plugin.loginmodule;

import com.google.common.base.Optional;
import jetbrains.buildServer.groups.UserGroupManager;

import java.util.Formatter;

public class GroupNameToGroupKey {
    private final UserGroupManager userGroupManager;

    public GroupNameToGroupKey(UserGroupManager userGroupManager) {
        this.userGroupManager = userGroupManager;
    }

    //private final UserGroupManager groupManger;

    public Optional<String> transform(String groupName) {
        String sanitizedGroupName = groupName.toUpperCase().replaceAll(" ", "_").replaceAll("-", "_");
        if (sanitizedGroupName.length() < 16) {
            return Optional.of( sanitizedGroupName );
        }
        sanitizedGroupName = sanitizedGroupName.substring(0, 16);
        if ( userGroupManager.findUserGroupByKey(sanitizedGroupName) == null ) {
            return Optional.of( sanitizedGroupName );
        } else {
            return findAlternative(sanitizedGroupName);
        }
    }

    private Optional<String> findAlternative(String sanitizedGroupName) {
        String prefix = sanitizedGroupName.substring(0,13);
        for ( int i = 0 ; i < 1000 ; i++ ) {
            String newName = prefix + String.format("%03d", i);
            if (userGroupManager.findUserGroupByKey(newName) == null) {
                return Optional.of( newName );
            }
        }
        return Optional.absent();
    }
}
