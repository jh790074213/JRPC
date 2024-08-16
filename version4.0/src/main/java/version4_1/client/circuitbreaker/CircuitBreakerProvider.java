package version4_1.client.circuitbreaker;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author JH
 * @Date 2024/8/16 14:58
 * @Version 4.0
 */
@Slf4j
public class CircuitBreakerProvider {
    private Map<String,CircuitBreaker> circuitBreakerMap=new HashMap<>();

    public synchronized CircuitBreaker getCircuitBreaker(String serviceName){
        CircuitBreaker circuitBreaker;
        if(circuitBreakerMap.containsKey(serviceName)){
            circuitBreaker=circuitBreakerMap.get(serviceName);
        }else {
            log.info("serviceName="+serviceName+"创建一个新的熔断器");
            circuitBreaker=new CircuitBreaker(1,0.5,10000);
            circuitBreakerMap.put(serviceName,circuitBreaker);
        }
        return circuitBreaker;
    }
}