package info.developerblog.spring.cloud.marathon.discovery.ribbon;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ServerList;
import info.developerblog.spring.cloud.marathon.discovery.MarathonDiscoveryProperties;
import mesosphere.marathon.client.Marathon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MarathonRibbonClientConfiguration {

    private Marathon client;
    private IClientConfig clientConfig;
    private MarathonDiscoveryProperties properties;

    @Autowired
    public MarathonRibbonClientConfiguration(Marathon client,
                                             IClientConfig clientConfig,
                                             MarathonDiscoveryProperties properties) {
        this.client = client;
        this.clientConfig = clientConfig;
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    public ServerList<?> ribbonServerList() {
        MarathonServerList serverList = new MarathonServerList(client, properties);
        serverList.initWithNiwsConfig(clientConfig);
        return serverList;
    }
}
