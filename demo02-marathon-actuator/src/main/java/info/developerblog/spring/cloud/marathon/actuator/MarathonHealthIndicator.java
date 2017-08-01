package info.developerblog.spring.cloud.marathon.actuator;

import mesosphere.marathon.client.Marathon;
import mesosphere.marathon.client.model.v2.App;
import mesosphere.marathon.client.model.v2.GetServerInfoResponse;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.cloud.client.discovery.health.DiscoveryHealthIndicator;

import java.util.List;

public class MarathonHealthIndicator implements DiscoveryHealthIndicator {
    Marathon client;

    public MarathonHealthIndicator(Marathon client) {
        this.client = client;
    }

    @Override
    public String getName() {
        return "Marathon Discovery Client";
    }

    @Override
    public Health health() {
        Health.Builder builder = new Health.Builder();

        try {
            GetServerInfoResponse serverInfo = client.getServerInfo();
            builder.up()
                    .withDetail("name", serverInfo.getName())
                    .withDetail("leader", serverInfo.getLeader())
                    .withDetail("http_port", serverInfo.getHttp_config().getHttp_port())
                    .withDetail("https_port", serverInfo.getHttp_config().getHttps_port())
                    .withDetail("hostname", serverInfo.getMarathon_config().getHostname())
                    .withDetail("local_port_min", serverInfo.getMarathon_config().getLocal_port_min())
                    .withDetail("local_port_max", serverInfo.getMarathon_config().getLocal_port_max());
        }
        catch (Exception e) {
            builder.down(e);
        }

        return builder.build();
    }
}
