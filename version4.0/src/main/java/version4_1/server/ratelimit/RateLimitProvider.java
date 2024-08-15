package version4_1.server.ratelimit;

import version4_1.server.ratelimit.impl.TokenBucketRateLimitImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author JH
 * @Date 2024/8/15 20:23
 * @Version 4.0
 */
public class RateLimitProvider {
    private Map<String,RateLimit> rateLimitMap = new HashMap<>();

    /**
     * 根据服务名获得限流器
     * @param interfaceName 服务名
     * @return 限流器
     */
    public RateLimit getRateLimit(String interfaceName){
        if(!rateLimitMap.containsKey(interfaceName)){
            RateLimit rateLimit=new TokenBucketRateLimitImpl(100,10);
            rateLimitMap.put(interfaceName,rateLimit);
            return rateLimit;
        }
        return rateLimitMap.get(interfaceName);
    }
}
