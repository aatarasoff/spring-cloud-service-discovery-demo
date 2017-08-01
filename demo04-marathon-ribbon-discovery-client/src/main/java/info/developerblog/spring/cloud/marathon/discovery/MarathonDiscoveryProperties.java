package info.developerblog.spring.cloud.marathon.discovery;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.cloud.marathon.discovery")
@Data
public class MarathonDiscoveryProperties {
    private boolean enabled = true;

    private String zone;
}
