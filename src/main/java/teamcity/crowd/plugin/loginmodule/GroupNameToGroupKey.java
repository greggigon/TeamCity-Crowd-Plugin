package teamcity.crowd.plugin.loginmodule;

public class GroupNameToGroupKey {

    public String transform(String groupName) {
        String sanitizedGroupName = groupName.toUpperCase().replaceAll(" ", "_").replaceAll("-", "_");
        if (sanitizedGroupName.length() > 16) {
            return sanitizedGroupName.substring(0, 16);
        }
        return sanitizedGroupName;
    }
}
