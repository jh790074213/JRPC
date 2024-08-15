package version4_1.server.ratelimit;

/**
 * @Author JH
 * @Date 2024/8/15 20:15
 * @Version 1.0
 */
public interface RateLimit {
    /**
     * @return 是否获得令牌
     */
    boolean getToken();
}
