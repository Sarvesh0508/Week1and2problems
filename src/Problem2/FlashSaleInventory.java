package Problem2;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FlashSaleInventory {

    // productId -> stock
    private ConcurrentHashMap<String, AtomicInteger> stockMap;

    // productId -> waiting list (FIFO)
    private ConcurrentHashMap<String, Queue<Integer>> waitingListMap;

    public FlashSaleInventory() {
        stockMap = new ConcurrentHashMap<>();
        waitingListMap = new ConcurrentHashMap<>();
    }

    // Add product
    public void addProduct(String productId, int stock) {
        stockMap.put(productId, new AtomicInteger(stock));
        waitingListMap.put(productId, new LinkedList<>());
    }

    // Check stock
    public int checkStock(String productId) {
        if (!stockMap.containsKey(productId)) return 0;
        return stockMap.get(productId).get();
    }

    // Purchase item
    public String purchaseItem(String productId, int userId) {
        AtomicInteger stock = stockMap.get(productId);

        if (stock == null) {
            return "Product not found";
        }

        // Atomic operation
        while (true) {
            int currentStock = stock.get();

            if (currentStock > 0) {
                // Try to decrement safely
                if (stock.compareAndSet(currentStock, currentStock - 1)) {
                    return "Success, " + (currentStock - 1) + " units remaining";
                }
            } else {
                // Add to waiting list
                Queue<Integer> queue = waitingListMap.get(productId);
                synchronized (queue) {
                    queue.add(userId);
                    return "Out of stock. Added to waiting list, position #" + queue.size();
                }
            }
        }
    }

    // Get waiting list
    public Queue<Integer> getWaitingList(String productId) {
        return waitingListMap.get(productId);
    }

    // Main test
    public static void main(String[] args) {
        FlashSaleInventory system = new FlashSaleInventory();

        system.addProduct("IPHONE15_256GB", 3);

        // Check stock
        System.out.println("Stock: " + system.checkStock("IPHONE15_256GB"));

        // Simulate purchases
        System.out.println(system.purchaseItem("IPHONE15_256GB", 101));
        System.out.println(system.purchaseItem("IPHONE15_256GB", 102));
        System.out.println(system.purchaseItem("IPHONE15_256GB", 103));

        // Now stock is 0 → waiting list starts
        System.out.println(system.purchaseItem("IPHONE15_256GB", 104));
        System.out.println(system.purchaseItem("IPHONE15_256GB", 105));

        // Show waiting list
        System.out.println("Waiting List: " + system.getWaitingList("IPHONE15_256GB"));
    }
}