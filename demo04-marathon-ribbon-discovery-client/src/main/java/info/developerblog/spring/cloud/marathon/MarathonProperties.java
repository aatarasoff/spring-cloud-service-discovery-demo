package info.developerblog.spring.cloud.marathon;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@ConfigurationProperties("spring.cloud.marathon")
@Data
public class MarathonProperties {
    @NotNull
    private String scheme = "http";

    @NotNull
    private String host = "localhost";

    @NotNull
    private int port = 8080;

    private String endpoint = null;

    private boolean enabled = true;

    private String listOfServers = null;

    private String token = null;

    private String username = null;

    private String password = null;

    public String getEndpoint() {
        if (null != endpoint) {
            return endpoint;
        }

        return this.getScheme() + "://" + this.getHost() + ":" + this.getPort();
    }
}
