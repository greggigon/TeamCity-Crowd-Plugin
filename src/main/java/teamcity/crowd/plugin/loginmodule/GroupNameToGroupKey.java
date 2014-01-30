package teamcity.crowd.plugin.loginmodule;

/**
 * Created by Grzegorz (Greg) Gigon
 * Date: 30/01/2014
 */
public class GroupNameToGroupKey {

    public String transform(String groupName) {
        String sanitizedGroupName = groupName.toUpperCase().replaceAll(" ", "_").replaceAll("-", "_");
        if (sanitizedGroupName.length() > 16) {
            return sanitizedGroupName.substring(0, 16);
        }
        return sanitizedGroupName;
    }
}
