package glotov.multithreading.entity;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import glotov.multithreading.exception.CustomException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Ship implements Callable<Void> {
    private static final Logger logger = LogManager.getLogger(Ship.class);

    private final int id;
    private final Port port;
    private final int capacity;
    private int currentLoad;
    private final Random random;

    public Ship(int id, Port port, int capacity, int currentLoad) {
        this.id = id;
        this.port = port;
        this.capacity = capacity;
        this.currentLoad = currentLoad;
        this.random = new Random();
    }

    public int getId() {
        return id;
    }

    // Perform loading of containers
    private void load() throws CustomException {
        int maxLoad = capacity - currentLoad;
        int loadCount = random.nextInt(maxLoad + 1);
        int shipId = getId();
        port.unloadContainers(loadCount, shipId);  // Unload containers from the port
        currentLoad += loadCount;  // Update the current load of the ship
    }

    // Perform unloading of containers
    private void unload() throws CustomException {
        int unloadCount = random.nextInt(currentLoad + 1);
        int shipId = getId();
        port.loadContainers(unloadCount, shipId);  // Load containers onto the port
        currentLoad -= unloadCount;  // Update the current load of the ship
    }

    @Override
    public Void call() throws CustomException {
        try {
            Dock dock = port.acquireDock();  // Acquire a dock from the port
            logger.info("Ship " + id + " docked at the port.");
            // Perform loading/unloading operations based on random activity
            ShipActivity activity = ShipActivity.getRandomActivity();
            logger.info("Get activity for ship " + id + ": " + activity);
            switch (activity) {
                case UNLOAD -> unload();  // Unload containers from the ship
                case LOAD -> load();  // Load containers onto the ship
                case UNLOAD_AND_LOAD -> {
                    unload();  // Unload containers from the ship
                    load();  // Load containers onto the ship
                }
            }
            TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));  // Simulate work on the dock
            port.releaseDock(dock);  // Release the dock back to the port
            logger.info("Ship " + id + "left the port. Current load: " + currentLoad);
        } catch (InterruptedException e) {
            logger.error("Ship " + id + "encountered an InterruptedException: " + e.getMessage());
            throw new CustomException(e);  // Wrap and propagate the exception as CustomException
        }
        return null;
    }

    @Override
    public String toString() {
        return "Ship{" +
                "id=" + id +
                ", capacity=" + capacity +
                ", currentLoad=" + currentLoad +
                '}';
    }
}

