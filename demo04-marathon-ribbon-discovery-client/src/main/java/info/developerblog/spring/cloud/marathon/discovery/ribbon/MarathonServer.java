package info.developerblog.spring.cloud.marathon.discovery.ribbon;

import com.netflix.loadbalancer.Server;
import mesosphere.marathon.client.model.v2.HealthCheckResults;

import java.util.Collection;

public class MarathonServer extends Server {

    private Collection<HealthCheckResults> healthChecks;

    public MarathonServer(String host, int port, Collection<HealthCheckResults> healthChecks) {
        super(host, port);
        this.healthChecks = healthChecks;
    }

    public boolean isHealthChecksPassing() {
        return healthChecks.stream()
                .allMatch(HealthCheckResults::getAlive);
    }

}
