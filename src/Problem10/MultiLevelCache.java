package Problem10;

import java.util.*;

class Video {
    String id;
    String data;

    public Video(String id) {
        this.id = id;
        this.data = "VideoData_" + id;
    }
}

// LRU Cache using LinkedHashMap
class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private int capacity;

    public LRUCache(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}

public class MultiLevelCache {

    private LRUCache<String, Video> L1;
    private LRUCache<String, Video> L2;
    private Map<String, Video> L3;

    private int hitsL1 = 0, hitsL2 = 0, hitsL3 = 0;
    private int totalRequests = 0;

    public MultiLevelCache() {
        L1 = new LRUCache<>(10000);
        L2 = new LRUCache<>(100000);
        L3 = new HashMap<>();
    }

    // Simulated DB load
    private Video fetchFromDB(String videoId) {
        try { Thread.sleep(50); } catch (Exception e) {}
        return new Video(videoId);
    }

    public Video getVideo(String videoId) {
        totalRequests++;

        // 🔹 L1 Check
        if (L1.containsKey(videoId)) {
            hitsL1++;
            System.out.println("L1 HIT (0.5ms)");
            return L1.get(videoId);
        }

        System.out.println("L1 MISS");

        // 🔹 L2 Check
        if (L2.containsKey(videoId)) {
            hitsL2++;
            System.out.println("L2 HIT (5ms) → Promoting to L1");

            Video v = L2.get(videoId);
            L1.put(videoId, v);
            return v;
        }

        System.out.println("L2 MISS");

        // 🔹 L3 (Database)
        hitsL3++;
        System.out.println("L3 HIT (150ms)");

        Video v = L3.getOrDefault(videoId, fetchFromDB(videoId));

        // Add to L3 if not present
        L3.put(videoId, v);

        // Add to L2
        L2.put(videoId, v);

        return v;
    }

    // Statistics
    public void getStatistics() {
        System.out.println("\n===== CACHE STATS =====");

        double l1Rate = (hitsL1 * 100.0) / totalRequests;
        double l2Rate = (hitsL2 * 100.0) / totalRequests;
        double l3Rate = (hitsL3 * 100.0) / totalRequests;

        System.out.printf("L1 Hit Rate: %.2f%%\n", l1Rate);
        System.out.printf("L2 Hit Rate: %.2f%%\n", l2Rate);
        System.out.printf("L3 Hit Rate: %.2f%%\n", l3Rate);

        double overall = ((hitsL1 + hitsL2) * 100.0) / totalRequests;
        System.out.printf("Overall Cache Hit: %.2f%%\n", overall);
    }

    // Main Test
    public static void main(String[] args) {

        MultiLevelCache cache = new MultiLevelCache();

        // First request
        cache.getVideo("video_123");

        // Second request (should hit L1)
        cache.getVideo("video_123");

        // New video
        cache.getVideo("video_999");

        // Again
        cache.getVideo("video_999");

        cache.getStatistics();
    }
}