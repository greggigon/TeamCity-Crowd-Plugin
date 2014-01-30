package teamcity.crowd.plugin;

import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.user.User;
import com.google.common.base.Optional;

import java.util.List;

public interface PluginCrowdClient {

    Optional<User> loginUserWithPassword(String username, String password);

    List<Group> getUserGroups(String username);
}
