package version4_1.server.ratelimit.impl;

import version4_1.server.ratelimit.RateLimit;

/**
 * @Author JH
 * @Date 2024/8/15 20:16
 * @Version 4.0
 */
public class TokenBucketRateLimitImpl implements RateLimit {
    // 产生令牌的速率 1块/ms
    private static int RATE;
    // 桶容量
    private static int CAPACITY;
    // 当前桶内令牌数
    private volatile int curCapacity;
    // 时间戳
    private volatile long timeStamp = System.currentTimeMillis();

    public TokenBucketRateLimitImpl(int rate, int capacity) {
        RATE = rate;
        CAPACITY = capacity;
        curCapacity = capacity;
    }

    @Override
    public synchronized boolean getToken() {
        System.out.println("令牌容量：" + curCapacity);
        // 如果当前桶还有剩余，就直接返回
        if (curCapacity > 0) {
            curCapacity--;
            return true;
        }
        // 如果桶无剩余，
        long current = System.currentTimeMillis();
        // 如果距离上一次的请求的时间大于RATE的时间
        if (current - timeStamp >= RATE) {
            // 计算这段时间间隔中生成的令牌，如果>2,桶容量加上（计算的令牌-1）
            // 如果==1，就不做操作（因为这一次操作要消耗一个令牌）
            if ((current - timeStamp) / RATE >= 2) {
                curCapacity += (int) (current - timeStamp) / RATE - 1;
            }
            // 保持桶内令牌容量<=10
            if (curCapacity > CAPACITY) curCapacity = CAPACITY;
            // 刷新时间戳为本次请求
            timeStamp = current;
            return true;
        }
        // 获得不到，返回false
        return false;
    }
}
