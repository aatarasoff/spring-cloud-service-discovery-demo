package info.developerblog.spring.cloud.marathon.discovery;

import lombok.extern.slf4j.Slf4j;
import mesosphere.marathon.client.Marathon;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.Collections;
import java.util.List;

@Slf4j
public class MarathonDiscoveryClient implements DiscoveryClient {

    private static final String SPRING_CLOUD_MARATHON_DISCOVERY_CLIENT_DESCRIPTION = "Spring Cloud Marathon Discovery Client";

    private final Marathon client;
    private final MarathonDiscoveryProperties properties;

    public MarathonDiscoveryClient(Marathon client, MarathonDiscoveryProperties properties) {
        this.client = client;
        this.properties = properties;
    }

    @Override
    public String description() {
        return SPRING_CLOUD_MARATHON_DISCOVERY_CLIENT_DESCRIPTION;
    }

    @Override
    public ServiceInstance getLocalServiceInstance() {
        return null;
    }

    @Override
    public List <ServiceInstance> getInstances(String serviceId) {
        return Collections.emptyList();
    }


    @Override
    public List<String> getServices() {
        return Collections.emptyList();
    }
}
