package Problem1;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UsernameChecker {

    // Store username -> userId
    private ConcurrentHashMap<String, Integer> userMap;

    // Store username -> attempt count
    private ConcurrentHashMap<String, Integer> attemptMap;

    public UsernameChecker() {
        userMap = new ConcurrentHashMap<>();
        attemptMap = new ConcurrentHashMap<>();
    }

    // Add existing users (simulate DB)
    public void addUser(String username, int userId) {
        userMap.put(username, userId);
    }

    // Check availability
    public boolean checkAvailability(String username) {
        // Increase attempt count
        attemptMap.put(username, attemptMap.getOrDefault(username, 0) + 1);

        return !userMap.containsKey(username);
    }

    // Suggest alternatives
    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            String newName = username + i;
            if (!userMap.containsKey(newName)) {
                suggestions.add(newName);
            }
        }

        // Try replacing underscore
        String modified = username.replace("_", ".");
        if (!userMap.containsKey(modified)) {
            suggestions.add(modified);
        }

        return suggestions;
    }

    // Get most attempted username
    public String getMostAttempted() {
        String maxUser = "";
        int maxCount = 0;

        for (Map.Entry<String, Integer> entry : attemptMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                maxUser = entry.getKey();
            }
        }

        return maxUser + " (" + maxCount + " attempts)";
    }

    // Main method to test
    public static void main(String[] args) {
        UsernameChecker system = new UsernameChecker();

        // Existing users
        system.addUser("john_doe", 1);
        system.addUser("admin", 2);

        // Test availability
        System.out.println(system.checkAvailability("john_doe")); // false
        System.out.println(system.checkAvailability("jane_smith")); // true

        // Suggestions
        System.out.println(system.suggestAlternatives("john_doe"));

        // Simulate attempts
        system.checkAvailability("admin");
        system.checkAvailability("admin");
        system.checkAvailability("admin");

        // Most attempted
        System.out.println(system.getMostAttempted());
    }
}