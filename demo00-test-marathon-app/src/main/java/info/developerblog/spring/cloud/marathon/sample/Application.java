package info.developerblog.spring.cloud.marathon.sample;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequest;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.EnableZuulServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


@Slf4j
@Configuration
@EnableAutoConfiguration
@RestController
@EnableConfigurationProperties
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class Application {
    @Autowired
    private LoadBalancerClient loadBalancer;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private SampleClient sampleClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Environment environment;

    @Value("${spring.application.name:test-marathon-app}")
    private String serviceId;

    private AtomicLong counter = new AtomicLong();

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @RequestMapping("/me")
    public String me() throws UnknownHostException, InterruptedException {
        long multiplicator = counter.incrementAndGet();

        long delay = Long.valueOf(environment.getProperty("TEST_APP_DELAY", "100"));
        Thread.sleep(delay * multiplicator);

        String response = serviceId + " @ " + environment.getProperty("HOST", "unknownhost")
                + ":" + environment.getProperty("PORT0", "9090")
                + " with docker hostname: " + InetAddress.getLocalHost().getHostName();

        counter.decrementAndGet();

        return response;
    }

    @RequestMapping("/services")
    public List<String> services() {
        return discoveryClient.getServices();
    }

    @RequestMapping("/rest")
    public String rest() {
        return this.restTemplate.getForObject("http://"+ serviceId +"/me", String.class);
    }

    @RequestMapping("/")
    public ServiceInstance lb() {
        return loadBalancer.choose(serviceId);
    }

    @RequestMapping("/url")
    public String realUrl() throws IOException {
        return loadBalancer.execute(serviceId, instance ->
                loadBalancer.reconstructURI(
                        instance,
                        new URI("http://"+ serviceId +"/me")
                )
        ).toString();
    }

    @RequestMapping("/choose")
    public String choose() {
        return loadBalancer.choose(serviceId).getUri().toString();
    }

    @RequestMapping("/instances")
    public List<ServiceInstance> instances() {
        return discoveryClient.getInstances(serviceId);
    }

    @RequestMapping("/feign")
    public String feign() {
        return sampleClient.call();
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @FeignClient("test-marathon-app")
    public interface SampleClient {

        @RequestMapping(value = "/me", method = RequestMethod.GET)
        String call();
    }
}
