package Problem9;

import java.util.*;

class Transaction {
    int id;
    int amount;
    String merchant;
    String account;
    int time; // minutes (for simplicity)

    public Transaction(int id, int amount, String merchant, String account, int time) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.time = time;
    }
}

public class FinancialAnalyzer {

    List<Transaction> transactions;

    public FinancialAnalyzer(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    // 🔹 Classic Two Sum
    public List<int[]> findTwoSum(int target) {
        Map<Integer, Transaction> map = new HashMap<>();
        List<int[]> result = new ArrayList<>();

        for (Transaction t : transactions) {
            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                result.add(new int[]{map.get(complement).id, t.id});
            }

            map.put(t.amount, t);
        }
        return result;
    }

    // 🔹 Two Sum within 1 hour window
    public List<int[]> findTwoSumWithTime(int target) {
        List<int[]> result = new ArrayList<>();

        for (int i = 0; i < transactions.size(); i++) {
            for (int j = i + 1; j < transactions.size(); j++) {

                if (Math.abs(transactions.get(i).time - transactions.get(j).time) <= 60 &&
                        transactions.get(i).amount + transactions.get(j).amount == target) {

                    result.add(new int[]{transactions.get(i).id, transactions.get(j).id});
                }
            }
        }
        return result;
    }

    // 🔹 K-Sum
    public List<List<Integer>> findKSum(int k, int target) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(result, new ArrayList<>(), 0, k, target);
        return result;
    }

    private void backtrack(List<List<Integer>> result, List<Integer> current,
                           int start, int k, int target) {

        if (k == 0 && target == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        if (k == 0 || target < 0) return;

        for (int i = start; i < transactions.size(); i++) {
            current.add(transactions.get(i).id);

            backtrack(result, current, i + 1,
                    k - 1, target - transactions.get(i).amount);

            current.remove(current.size() - 1);
        }
    }

    // 🔹 Duplicate Detection
    public void detectDuplicates() {
        Map<String, List<Transaction>> map = new HashMap<>();

        for (Transaction t : transactions) {
            String key = t.amount + "-" + t.merchant;

            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(t);
        }

        for (String key : map.keySet()) {
            List<Transaction> list = map.get(key);

            if (list.size() > 1) {
                System.out.println("Duplicate Found → " + key);

                for (Transaction t : list) {
                    System.out.println("Account: " + t.account + ", ID: " + t.id);
                }
            }
        }
    }

    // Main Test
    public static void main(String[] args) {

        List<Transaction> data = Arrays.asList(
                new Transaction(1, 500, "Store A", "acc1", 600),
                new Transaction(2, 300, "Store B", "acc2", 615),
                new Transaction(3, 200, "Store C", "acc3", 630),
                new Transaction(4, 500, "Store A", "acc4", 700)
        );

        FinancialAnalyzer fa = new FinancialAnalyzer(data);

        // Two Sum
        System.out.println("Two Sum:");
        for (int[] pair : fa.findTwoSum(500)) {
            System.out.println(pair[0] + " + " + pair[1]);
        }

        // Time Window
        System.out.println("\nTwo Sum within 1 hour:");
        for (int[] pair : fa.findTwoSumWithTime(500)) {
            System.out.println(pair[0] + " + " + pair[1]);
        }

        // K Sum
        System.out.println("\nK Sum (k=3, target=1000):");
        System.out.println(fa.findKSum(3, 1000));

        // Duplicates
        System.out.println("\nDuplicate Detection:");
        fa.detectDuplicates();
    }
}