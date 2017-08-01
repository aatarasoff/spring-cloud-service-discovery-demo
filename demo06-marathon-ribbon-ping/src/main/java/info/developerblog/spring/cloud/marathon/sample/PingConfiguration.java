package info.developerblog.spring.cloud.marathon.sample;

import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.PingUrl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PingConfiguration {
    @Bean
    public IPing ribbonPing() {
        return new PingUrl();
    }
}
