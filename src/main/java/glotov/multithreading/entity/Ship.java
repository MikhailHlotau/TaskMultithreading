package glotov.multithreading.entity;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import glotov.multithreading.exception.CustomException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Ship implements Runnable {
    private static final Logger logger = LogManager.getLogger(Ship.class);

    private final UUID id;
    private final Port port;
    private final int capacity;
    private int currentDownload;
    private final Random random;

    public Ship(UUID id, Port port, int capacity, int currentDownload) {
        this.id = id;
        this.port = port;
        this.capacity = capacity;
        this.currentDownload = currentDownload;
        this.random = new Random();
    }

    // Perform loading of containers
    private void load() throws CustomException {
        int maxLoad = capacity - currentDownload;
        int loadCount = random.nextInt(maxLoad + 1);
        port.unloadContainers(loadCount, id);  // Unload containers from the port
        currentDownload += loadCount;  // Update the current load of the ship
    }

    // Perform unloading of containers
    private void unload() throws CustomException {
        int unloadCount = random.nextInt(currentDownload + 1);
        port.loadContainers(unloadCount, id);  // Load containers onto the port
        currentDownload -= unloadCount;  // Update the current load of the ship
    }

    @Override
    public void run() {
        try {
            port.acquireDock(id); // Acquire a dock from the port
            Dock dock = port.getAssignedDock(id);
            if (dock != null) {
                int dockId = dock.getId(); // Getting the assigned dock ID
                logger.info("Ship " + id + " assigned to Dock " + dockId);
            }

            // Perform loading/unloading operations depending on random activity
            ShipActivity activity = ShipActivity.getRandomActivity();
            switch (activity) {
                case UNLOAD -> unload();  // Unload containers from the ship
                case LOAD -> load();  // Load containers onto the ship
                case UNLOAD_AND_LOAD -> {
                    unload();  // Unload containers from the ship
                    load();  // Load containers onto the ship
                }
            }
            logger.info("Get activity for ship " + id + ": " + activity);

            TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));  // Simulate work on the dock

            port.releaseDock(id); // Release the dock and free it up for other ships
            logger.info("Ship " + id + " left the port. Current load: " + currentDownload);

        } catch (CustomException | InterruptedException e) {
            System.err.println("Exception occurred for Ship " + id + ": " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public String toString() {
        return "Ship{" +
                "id=" + id +
                ", capacity=" + capacity +
                ", currentLoad=" + currentDownload +
                '}';
    }
}


