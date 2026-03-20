package Problem5;

import java.util.*;
import java.util.concurrent.*;

class PageViewEvent {
    String url;
    String userId;
    String source;

    public PageViewEvent(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

public class RealTimeAnalytics {

    // page → total visits
    private ConcurrentHashMap<String, Integer> pageViews;

    // page → unique users
    private ConcurrentHashMap<String, Set<String>> uniqueVisitors;

    // source → count
    private ConcurrentHashMap<String, Integer> trafficSources;

    public RealTimeAnalytics() {
        pageViews = new ConcurrentHashMap<>();
        uniqueVisitors = new ConcurrentHashMap<>();
        trafficSources = new ConcurrentHashMap<>();
    }

    // Process event
    public void processEvent(PageViewEvent event) {

        // Update page views
        pageViews.put(event.url,
                pageViews.getOrDefault(event.url, 0) + 1);

        // Update unique visitors
        uniqueVisitors.putIfAbsent(event.url, ConcurrentHashMap.newKeySet());
        uniqueVisitors.get(event.url).add(event.userId);

        // Update traffic source
        trafficSources.put(event.source,
                trafficSources.getOrDefault(event.source, 0) + 1);
    }

    // Get Top 10 Pages
    public List<Map.Entry<String, Integer>> getTopPages() {
        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>(Map.Entry.comparingByValue());

        for (Map.Entry<String, Integer> entry : pageViews.entrySet()) {
            pq.offer(entry);
            if (pq.size() > 10) {
                pq.poll();
            }
        }

        List<Map.Entry<String, Integer>> result = new ArrayList<>(pq);
        result.sort((a, b) -> b.getValue() - a.getValue());
        return result;
    }

    // Dashboard Output
    public void getDashboard() {
        System.out.println("\n===== DASHBOARD =====");

        // Top Pages
        System.out.println("\nTop Pages:");
        int rank = 1;
        for (Map.Entry<String, Integer> entry : getTopPages()) {
            String url = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.get(url).size();

            System.out.println(rank++ + ". " + url +
                    " - " + views + " views (" + unique + " unique)");
        }

        // Traffic Sources
        System.out.println("\nTraffic Sources:");
        int total = trafficSources.values().stream().mapToInt(i -> i).sum();

        for (Map.Entry<String, Integer> entry : trafficSources.entrySet()) {
            double percent = (entry.getValue() * 100.0) / total;
            System.out.printf("%s: %.1f%%\n", entry.getKey(), percent);
        }
    }

    // Simulate real-time dashboard every 5 sec
    public void startDashboard() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            getDashboard();
        }, 0, 5, TimeUnit.SECONDS);
    }

    // Main test
    public static void main(String[] args) throws InterruptedException {

        RealTimeAnalytics analytics = new RealTimeAnalytics();

        analytics.startDashboard();

        // Simulate events
        String[] urls = {"/article/breaking-news", "/sports/championship", "/tech/ai"};
        String[] sources = {"google", "facebook", "direct"};

        Random rand = new Random();

        for (int i = 0; i < 50; i++) {
            String url = urls[rand.nextInt(urls.length)];
            String user = "user_" + rand.nextInt(20);
            String source = sources[rand.nextInt(sources.length)];

            analytics.processEvent(new PageViewEvent(url, user, source));

            Thread.sleep(200);
        }
    }
}