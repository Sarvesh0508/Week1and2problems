package Problem6;

import java.util.concurrent.*;
import java.util.*;

class TokenBucket {
    private int tokens;
    private final int maxTokens;
    private final double refillRate; // tokens per second
    private long lastRefillTime;

    public TokenBucket(int maxTokens, double refillRate) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.tokens = maxTokens;
        this.lastRefillTime = System.currentTimeMillis();
    }

    // Refill tokens based on time passed
    private void refill() {
        long now = System.currentTimeMillis();
        double seconds = (now - lastRefillTime) / 1000.0;

        int tokensToAdd = (int) (seconds * refillRate);

        if (tokensToAdd > 0) {
            tokens = Math.min(maxTokens, tokens + tokensToAdd);
            lastRefillTime = now;
        }
    }

    // Try to consume a token
    public synchronized boolean allowRequest() {
        refill();

        if (tokens > 0) {
            tokens--;
            return true;
        }
        return false;
    }

    public int getRemainingTokens() {
        return tokens;
    }

    public long getRetryAfterSeconds() {
        if (tokens > 0) return 0;

        return (long) (1 / refillRate); // approximate wait
    }
}

public class RateLimiter {

    private ConcurrentHashMap<String, TokenBucket> clientBuckets;

    private static final int MAX_REQUESTS = 1000;
    private static final double REFILL_RATE = MAX_REQUESTS / 3600.0; // per sec

    public RateLimiter() {
        clientBuckets = new ConcurrentHashMap<>();
    }

    public String checkRateLimit(String clientId) {

        clientBuckets.putIfAbsent(clientId,
                new TokenBucket(MAX_REQUESTS, REFILL_RATE));

        TokenBucket bucket = clientBuckets.get(clientId);

        if (bucket.allowRequest()) {
            return "Allowed (" + bucket.getRemainingTokens() + " requests remaining)";
        } else {
            return "Denied (0 remaining, retry after " +
                    bucket.getRetryAfterSeconds() + "s)";
        }
    }

    public void getRateLimitStatus(String clientId) {
        TokenBucket bucket = clientBuckets.get(clientId);

        if (bucket == null) {
            System.out.println("Client not found");
            return;
        }

        int remaining = bucket.getRemainingTokens();
        int used = MAX_REQUESTS - remaining;

        System.out.println("Used: " + used +
                ", Limit: " + MAX_REQUESTS +
                ", Remaining: " + remaining);
    }

    // Main test
    public static void main(String[] args) {

        RateLimiter limiter = new RateLimiter();
        String client = "abc123";

        // Simulate requests
        for (int i = 0; i < 5; i++) {
            System.out.println(limiter.checkRateLimit(client));
        }

        // Simulate limit exceed
        for (int i = 0; i < 1000; i++) {
            limiter.checkRateLimit(client);
        }

        System.out.println(limiter.checkRateLimit(client)); // should deny

        limiter.getRateLimitStatus(client);
    }
}