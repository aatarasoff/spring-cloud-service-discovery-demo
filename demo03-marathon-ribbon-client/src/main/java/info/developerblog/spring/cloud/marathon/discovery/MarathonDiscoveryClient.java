package info.developerblog.spring.cloud.marathon.discovery;

import info.developerblog.spring.cloud.marathon.utils.ServiceIdConverter;
import lombok.extern.slf4j.Slf4j;
import mesosphere.marathon.client.Marathon;
import mesosphere.marathon.client.MarathonException;
import mesosphere.marathon.client.model.v2.App;
import mesosphere.marathon.client.model.v2.HealthCheckResult;
import mesosphere.marathon.client.model.v2.HealthCheckResults;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        try {
            return client.getAppTasks(ServiceIdConverter.convertToMarathonId(serviceId))
                    .getTasks()
                    .stream()
                    .filter(task -> null == task.getHealthCheckResults() ||
                            task.getHealthCheckResults().stream().allMatch(HealthCheckResults::getAlive))
                    .map(task -> new DefaultServiceInstance(
                            ServiceIdConverter.convertToServiceId(task.getAppId()),
                            task.getHost(),
                            task.getPorts().stream().findFirst().orElse(0),
                            false))
                    .collect(Collectors.toList());
        } catch (MarathonException e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }


    @Override
    public List<String> getServices() {
        try {
            return client.getApps()
                    .getApps()
                    .stream()
                    .map(App::getId)
                    .map(ServiceIdConverter::convertToServiceId)
                    .collect(Collectors.toList());
        } catch (MarathonException e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
