package info.developerblog.spring.cloud.marathon;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import com.netflix.loadbalancer.ServerListFilter;
import com.netflix.loadbalancer.ServerListUpdater;
import com.netflix.loadbalancer.ZoneAwareLoadBalancer;
import info.developerblog.spring.cloud.marathon.discovery.MarathonDiscoveryProperties;
import info.developerblog.spring.cloud.marathon.discovery.ribbon.MarathonRibbonClientConfiguration;
import lombok.extern.slf4j.Slf4j;
import mesosphere.marathon.client.Marathon;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class RibbonCustomConfiguration extends MarathonRibbonClientConfiguration {
    public RibbonCustomConfiguration(Marathon client, IClientConfig clientConfig, MarathonDiscoveryProperties properties) {
        super(client, clientConfig, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public ILoadBalancer ribbonLoadBalancer(IClientConfig config,
                                            ServerList<Server> serverList, ServerListFilter<Server> serverListFilter,
                                            IRule rule, IPing ping, ServerListUpdater serverListUpdater) {
        return new BalancerWithParallelStrategy(config, rule, ping, serverList,
                serverListFilter, serverListUpdater);
    }

    public static class BalancerWithParallelStrategy extends ZoneAwareLoadBalancer {
        public BalancerWithParallelStrategy(IClientConfig clientConfig, IRule rule,
                                            IPing ping, ServerList<Server> serverList, ServerListFilter<Server> filter,
                                            ServerListUpdater serverListUpdater) {
            super(clientConfig, rule, ping, serverList, filter, serverListUpdater);
            pingStrategy = (realPing, servers) -> {
                log.debug("parallel execution");
                List<Boolean> collected = Arrays.stream(servers)
                        .parallel()
                        .map(realPing::isAlive)
                        .collect(Collectors.toList());
                return ArrayUtils.toPrimitive(collected.toArray(new Boolean[collected.size()]));
            };
        }
    }
}
