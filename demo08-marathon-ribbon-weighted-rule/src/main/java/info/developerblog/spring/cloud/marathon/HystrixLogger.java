package info.developerblog.spring.cloud.marathon;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.stereotype.Component;

@Component
public class HystrixLogger {
    @Autowired
    private CounterService counterService;

    @HystrixCommand(groupKey = "calls", commandKey = "call.zone1")
    public void logZone1() {
        counterService.increment("meter.call.server.zone1");
    }

    @HystrixCommand(groupKey = "calls", commandKey = "call.zone2")
    public void logZone2() {
        counterService.increment("meter.call.server.zone2");
    }
}
