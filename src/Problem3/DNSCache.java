package Problem3;

import java.util.*;

class DNSEntry {
    String ipAddress;
    long expiryTime;

    public DNSEntry(String ipAddress, long ttlSeconds) {
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

public class DNSCache {

    private final int MAX_SIZE = 5;

    private LinkedHashMap<String, DNSEntry> cache;
    private int hits = 0;
    private int misses = 0;

    public DNSCache() {
        cache = new LinkedHashMap<>(16, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > MAX_SIZE; // LRU eviction
            }
        };

        // Background cleanup thread
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                    cleanupExpiredEntries();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Resolve domain
    public String resolve(String domain) {
        long startTime = System.nanoTime();

        if (cache.containsKey(domain)) {
            DNSEntry entry = cache.get(domain);

            if (!entry.isExpired()) {
                hits++;
                System.out.println("Cache HIT");
                return entry.ipAddress;
            } else {
                cache.remove(domain);
                System.out.println("Cache EXPIRED");
            }
        }

        // Cache miss
        misses++;
        System.out.println("Cache MISS");

        String ip = queryUpstreamDNS(domain);

        // Add to cache with TTL = 5 seconds (for demo)
        cache.put(domain, new DNSEntry(ip, 5));

        return ip;
    }

    // Simulated upstream DNS
    private String queryUpstreamDNS(String domain) {
        // Fake IP generator
        return "172.217." + new Random().nextInt(255) + "." + new Random().nextInt(255);
    }

    // Cleanup expired entries
    private void cleanupExpiredEntries() {
        Iterator<Map.Entry<String, DNSEntry>> iterator = cache.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, DNSEntry> entry = iterator.next();
            if (entry.getValue().isExpired()) {
                iterator.remove();
            }
        }
    }

    // Stats
    public void getCacheStats() {
        int total = hits + misses;
        double hitRate = total == 0 ? 0 : (hits * 100.0) / total;

        System.out.println("Hit Rate: " + hitRate + "%");
        System.out.println("Hits: " + hits + ", Misses: " + misses);
    }

    // Main test
    public static void main(String[] args) throws InterruptedException {
        DNSCache dns = new DNSCache();

        System.out.println(dns.resolve("google.com"));
        Thread.sleep(1000);

        System.out.println(dns.resolve("google.com")); // HIT

        Thread.sleep(6000); // Expire TTL

        System.out.println(dns.resolve("google.com")); // MISS again

        dns.getCacheStats();
    }
}