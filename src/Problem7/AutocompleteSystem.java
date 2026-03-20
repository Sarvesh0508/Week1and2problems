package Problem7;

import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEnd = false;
    int frequency = 0;
}

public class AutocompleteSystem {

    private TrieNode root;

    public AutocompleteSystem() {
        root = new TrieNode();
    }

    // Insert or update query
    public void insert(String query) {
        TrieNode node = root;

        for (char c : query.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }

        node.isEnd = true;
        node.frequency++;
    }

    // DFS to collect results
    private void dfs(TrieNode node, String prefix,
                     PriorityQueue<String[]> pq) {

        if (node.isEnd) {
            pq.offer(new String[]{prefix, String.valueOf(node.frequency)});

            if (pq.size() > 10) {
                pq.poll(); // remove lowest
            }
        }

        for (char c : node.children.keySet()) {
            dfs(node.children.get(c), prefix + c, pq);
        }
    }

    // Search suggestions
    public List<String> search(String prefix) {
        TrieNode node = root;

        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c)) {
                return new ArrayList<>();
            }
            node = node.children.get(c);
        }

        // Min heap (frequency)
        PriorityQueue<String[]> pq = new PriorityQueue<>(
                Comparator.comparingInt(a -> Integer.parseInt(a[1]))
        );

        dfs(node, prefix, pq);

        List<String> result = new ArrayList<>();
        while (!pq.isEmpty()) {
            String[] item = pq.poll();
            result.add(item[0] + " (" + item[1] + ")");
        }

        Collections.reverse(result); // highest first
        return result;
    }

    // Update frequency
    public void updateFrequency(String query) {
        insert(query);
    }

    // Main test
    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        // Insert queries
        system.insert("java tutorial");
        system.insert("javascript");
        system.insert("java download");
        system.insert("java tutorial");
        system.insert("java tutorial");
        system.insert("java 21 features");

        // Search
        System.out.println("Suggestions for 'jav':");
        List<String> results = system.search("jav");

        for (String r : results) {
            System.out.println(r);
        }

        // Update frequency
        system.updateFrequency("java 21 features");
        system.updateFrequency("java 21 features");

        System.out.println("\nAfter updating frequency:");
        System.out.println(system.search("jav"));
    }
}