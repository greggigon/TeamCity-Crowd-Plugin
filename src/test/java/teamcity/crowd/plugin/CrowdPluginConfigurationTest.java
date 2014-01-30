package teamcity.crowd.plugin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import teamcity.crowd.plugin.utils.CrowdPluginConfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:build-server-plugin-TeamCityCrowdPlugin.xml",
        "classpath:test-build-server-plugin-TeamCityCrowdPlugin.xml"
})
public class CrowdPluginConfigurationTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private CrowdPluginConfiguration crowdPluginConfiguration;

    @Test
    public void shouldReturnTrueForGroupsCreation(){
        assertThat(crowdPluginConfiguration.shouldCreateGroups(), is(true));
    }

    @Test
    public void shouldModifyCrowdClientProperties(){
        assertThat(crowdPluginConfiguration.getClientProperties().getApplicationName(), is("teamcity2"));
    }

}
