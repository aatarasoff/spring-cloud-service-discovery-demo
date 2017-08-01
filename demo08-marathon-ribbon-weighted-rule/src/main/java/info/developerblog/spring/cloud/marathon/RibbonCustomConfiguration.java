package info.developerblog.spring.cloud.marathon;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RoundRobinRule;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import com.netflix.loadbalancer.ServerListFilter;
import com.netflix.loadbalancer.ServerListUpdater;
import com.netflix.loadbalancer.ZoneAwareLoadBalancer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RibbonCustomConfiguration {
    private IClientConfig clientConfig;
    private CounterService counterService;

    @Autowired
    public RibbonCustomConfiguration(IClientConfig clientConfig,
                                     CounterService counterService) {
        this.clientConfig = clientConfig;
        this.counterService = counterService;
    }

    @Bean
    public IRule ribbonRule() {
        RoundRobinRule rule = new RoundRobinRule();
        rule.initWithNiwsConfig(clientConfig);
        return rule;
    }

    @Bean
    public ILoadBalancer ribbonLoadBalancer(IClientConfig config,
                                            ServerList<Server> serverList, ServerListFilter<Server> serverListFilter,
                                            IRule rule, IPing ping, ServerListUpdater serverListUpdater) {
        return new BalancerStatsProxy(config, rule, ping, serverList,
                serverListFilter, serverListUpdater, counterService);
    }

    public static class BalancerStatsProxy extends ZoneAwareLoadBalancer {
        CounterService counterService;

        public BalancerStatsProxy(IClientConfig clientConfig, IRule rule,
                                  IPing ping, ServerList<Server> serverList, ServerListFilter<Server> filter,
                                  ServerListUpdater serverListUpdater, CounterService counterService) {
            super(clientConfig, rule, ping, serverList, filter, serverListUpdater);
            this.counterService = counterService;
        }

        @Override
        public Server chooseServer(Object key) {
            Server choosed = super.chooseServer(key);
            counterService.increment("meter.call.server." + choosed.getHost());
            return choosed;
        }
    }
}
