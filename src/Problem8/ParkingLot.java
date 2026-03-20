package Problem8;

import java.util.*;

class ParkingSpot {
    String licensePlate;
    long entryTime;
    String status; // EMPTY, OCCUPIED, DELETED

    public ParkingSpot() {
        this.status = "EMPTY";
    }
}

public class ParkingLot {

    private ParkingSpot[] table;
    private int capacity;
    private int size = 0;
    private int totalProbes = 0;
    private int totalOperations = 0;

    public ParkingLot(int capacity) {
        this.capacity = capacity;
        table = new ParkingSpot[capacity];

        for (int i = 0; i < capacity; i++) {
            table[i] = new ParkingSpot();
        }
    }

    // Hash function
    private int hash(String plate) {
        return Math.abs(plate.hashCode()) % capacity;
    }

    // Park vehicle
    public void parkVehicle(String plate) {
        int index = hash(plate);
        int probes = 0;

        while (table[index].status.equals("OCCUPIED")) {
            index = (index + 1) % capacity;
            probes++;
        }

        table[index].licensePlate = plate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].status = "OCCUPIED";

        size++;
        totalProbes += probes;
        totalOperations++;

        System.out.println("Parked " + plate +
                " at spot #" + index +
                " (" + probes + " probes)");
    }

    // Exit vehicle
    public void exitVehicle(String plate) {
        int index = hash(plate);

        while (!table[index].status.equals("EMPTY")) {
            if (plate.equals(table[index].licensePlate)) {

                long exitTime = System.currentTimeMillis();
                long durationMs = exitTime - table[index].entryTime;

                double hours = durationMs / (1000.0 * 60 * 60);
                double fee = hours * 5; // $5/hour

                table[index].status = "DELETED";
                size--;

                System.out.printf("Vehicle %s exited from spot #%d\n", plate, index);
                System.out.printf("Duration: %.2f hours, Fee: $%.2f\n", hours, fee);
                return;
            }

            index = (index + 1) % capacity;
        }

        System.out.println("Vehicle not found!");
    }

    // Find nearest free spot (from index 0)
    public int findNearestAvailable() {
        for (int i = 0; i < capacity; i++) {
            if (!table[i].status.equals("OCCUPIED")) {
                return i;
            }
        }
        return -1;
    }

    // Statistics
    public void getStatistics() {
        double occupancy = (size * 100.0) / capacity;
        double avgProbes = totalOperations == 0 ? 0 :
                (totalProbes * 1.0) / totalOperations;

        System.out.printf("Occupancy: %.2f%%\n", occupancy);
        System.out.printf("Avg Probes: %.2f\n", avgProbes);
        System.out.println("Peak Hour: 2-3 PM (simulated)");
    }

    // Main test
    public static void main(String[] args) throws InterruptedException {

        ParkingLot lot = new ParkingLot(10);

        lot.parkVehicle("ABC-1234");
        Thread.sleep(1000);

        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");

        lot.exitVehicle("ABC-1234");

        System.out.println("Nearest free spot: " + lot.findNearestAvailable());

        lot.getStatistics();
    }
}