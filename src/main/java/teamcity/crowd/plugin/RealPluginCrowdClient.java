package teamcity.crowd.plugin;

import com.atlassian.crowd.exception.*;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.service.client.CrowdClient;
import com.google.common.base.Optional;
import teamcity.crowd.plugin.utils.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class RealPluginCrowdClient implements PluginCrowdClient {

    private final static String OPERATION_FAILED_MESSAGE = "Bummer. Something went wrong. Can't talk to Crowd at all.";
    private final static String INVALID_AUTHENTICATION_MESSAGE = "Plugin can't authenticate with Crowd. Configuration details are incorrect.";
    private final static String APPLICATION_PERMISSIONS_MESSAGE = "Crowd client permissions are incorrect. Configuration details are incorrect.";

    private CrowdClient crowdClient;
    private LoggerFactory loggerFactory;

    public RealPluginCrowdClient(CrowdClient crowdClient, LoggerFactory loggerFactory) {
        this.crowdClient = crowdClient;
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Optional<User> loginUserWithPassword(String username, String password) {
        try {
            User user = crowdClient.authenticateUser(username, password);
            return Optional.of(user);
        } catch (UserNotFoundException e) {
            loggerFactory.getServerLogger().warn(format("User with name [%s] doesn't exists.", username), e);
        } catch (InactiveAccountException e) {
            loggerFactory.getServerLogger().info(format("User account [%s] is inactive", username), e);
        } catch (ExpiredCredentialException e) {
            loggerFactory.getServerLogger().info(format("User [%s] credentials expired", username), e);
        } catch (ApplicationPermissionException e) {
            loggerFactory.getServerLogger().error(APPLICATION_PERMISSIONS_MESSAGE, e);
        } catch (InvalidAuthenticationException e) {
            loggerFactory.getServerLogger().error(INVALID_AUTHENTICATION_MESSAGE, e);
        } catch (OperationFailedException e) {
            loggerFactory.getServerLogger().error(OPERATION_FAILED_MESSAGE, e);
        }
        return Optional.absent();
    }

    @Override
    public List<Group> getUserGroups(String username) {
        try {
            return crowdClient.getGroupsForUser(username, 0, Integer.MAX_VALUE);
        } catch (OperationFailedException e) {
            loggerFactory.getServerLogger().error(OPERATION_FAILED_MESSAGE, e);
        } catch (InvalidAuthenticationException e) {
            loggerFactory.getServerLogger().error(INVALID_AUTHENTICATION_MESSAGE, e);
        } catch (ApplicationPermissionException e) {
            loggerFactory.getServerLogger().error(APPLICATION_PERMISSIONS_MESSAGE, e);
        } catch (UserNotFoundException e) {
            loggerFactory.getServerLogger().error(format("User with name [%s] doesn't exists.", username), e);
        }
        return new ArrayList<>();
    }

}
