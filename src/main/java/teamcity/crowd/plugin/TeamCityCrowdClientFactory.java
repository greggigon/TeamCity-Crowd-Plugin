package teamcity.crowd.plugin;

import com.atlassian.crowd.service.client.ClientProperties;
import com.atlassian.crowd.service.client.CrowdClient;
import com.atlassian.crowd.service.factory.CrowdClientFactory;

public class TeamCityCrowdClientFactory {
    private CrowdClientFactory crowdClientFactory;
    private ClientProperties clientProperties;

    public TeamCityCrowdClientFactory(CrowdClientFactory crowdClientFactory, ClientProperties clientProperties){
        this.crowdClientFactory = crowdClientFactory;
        this.clientProperties = clientProperties;
    }

    public CrowdClient newInstance(){
        return crowdClientFactory.newInstance(clientProperties);
    }
}
