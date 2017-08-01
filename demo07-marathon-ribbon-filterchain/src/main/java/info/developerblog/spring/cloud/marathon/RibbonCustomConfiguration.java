package info.developerblog.spring.cloud.marathon;

import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractServerListFilter;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerListFilter;
import info.developerblog.spring.cloud.marathon.discovery.MarathonDiscoveryProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class RibbonCustomConfiguration {
    private IClientConfig clientConfig;
    private MarathonDiscoveryProperties properties;

    @Autowired
    public RibbonCustomConfiguration(IClientConfig clientConfig,
                                     MarathonDiscoveryProperties properties) {
        this.clientConfig = clientConfig;
        this.properties = properties;
    }

    @Bean
    public ServerListFilter<Server> ribbonServerListFilter() {
        MarathonZoneExclusiveFilter filter = new MarathonZoneExclusiveFilter(properties.getZone());
        filter.initWithNiwsConfig(clientConfig);
        return filter;
    }

    public static class MarathonZoneExclusiveFilter extends AbstractServerListFilter<Server> {
        private boolean enabled = false;
        private String localZone;

        MarathonZoneExclusiveFilter(String localZone) {
            this.localZone = localZone;
        }

        @Override
        public List<Server> getFilteredListOfServers(List<Server> servers) {
            log.debug("filter lists by MarathonZoneExclusiveFilter");
            if (enabled) {
                log.debug("MarathonZoneExclusiveFilter is enabled");
                return servers.stream()
                        .filter(server -> server.getHost().contains(localZone))
                        .collect(Collectors.toList());
            }

            return servers;
        }

        void initWithNiwsConfig(IClientConfig clientConfig) {
            enabled = clientConfig.getPropertyAsBoolean(
                    CommonClientConfigKey.EnableZoneExclusivity,
                    false
            );
        }
    }
}
