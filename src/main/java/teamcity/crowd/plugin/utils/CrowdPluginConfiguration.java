package teamcity.crowd.plugin.utils;

import com.atlassian.crowd.service.client.ClientProperties;
import jetbrains.buildServer.web.openapi.PluginException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class CrowdPluginConfiguration {
    public static final String SHOULD_CREATE_GROUPS = "tc.crowd.plugin.createGroups";
    public static final String DO_NOT_REMOVE_INTERNAL_GROUPS = "tc.crowd.plugin.doNotRemoveIntGroups";

    private LoggerFactory loggerFactory;
    private String configDir;
    private String configurationFileName;
    private ClientProperties clientProperties;
    private boolean shouldCreateGroups = false;
    private boolean doNotRemoveInternalGroups = false;

    public CrowdPluginConfiguration(String configDir, String configurationFileName, ClientProperties clientProperties, LoggerFactory loggerFactory) {
        this.configDir = configDir;
        this.configurationFileName = configurationFileName;
        this.clientProperties = clientProperties;
        this.loggerFactory = loggerFactory;

        initPropertiesFromFile();
    }

    public ClientProperties getClientProperties() {
        return clientProperties;
    }

    private void initPropertiesFromFile() {
        final File configurationFile = new File(configDir, configurationFileName);
        final Properties teamCityPluginProperties = new Properties();
        FileReader reader = null;
        try {
            reader = new FileReader(configurationFile);
            teamCityPluginProperties.load(reader);
            shouldCreateGroups = Boolean.parseBoolean(teamCityPluginProperties.getProperty(SHOULD_CREATE_GROUPS, "false"));
            doNotRemoveInternalGroups = Boolean.parseBoolean(teamCityPluginProperties.getProperty(DO_NOT_REMOVE_INTERNAL_GROUPS, "false"));

            clientProperties.updateProperties(teamCityPluginProperties);
        } catch (IOException e) {
            loggerFactory.getServerLogger().error("Seems that configuration file is not valid", e);
            throw new PluginException("Can't initialize the configuration properties for TeamCity Crowd Plugin. Make sure configuration file teamcity-crowd-plugin.properties is in the TeamCity Data Folder/config", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    loggerFactory.getServerLogger().debug("Ignoring stupid exception", e);
                }
            }
        }
    }

    public boolean shouldCreateGroups() {
        return shouldCreateGroups;
    }

    public boolean doNotRemoveInternalGroups() {
        return doNotRemoveInternalGroups;
    }
}
