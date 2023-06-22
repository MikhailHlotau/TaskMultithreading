package glotov.multithreading.entity;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import glotov.multithreading.exception.CustomException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Port {
    private static final Logger logger = LogManager.getLogger(Port.class);
    private final int maxContainers;
    private final Semaphore docks;
    private final AtomicInteger availableContainers;
    private static volatile Port port;
    private final List<Dock> dockList;
    private final Map<UUID, Dock> dockAssignment;

    private static final Lock lock = new ReentrantLock(true);

    private Port(int maxContainers, int dockCount, int initialContainers) {
        this.maxContainers = maxContainers;
        this.docks = new Semaphore(dockCount, true);
        this.availableContainers = new AtomicInteger(initialContainers);
        this.dockList = new ArrayList<>();
        this.dockAssignment = new HashMap<>();
        for (int i = 1; i <= dockCount; i++) {
            dockList.add(new Dock(i));
        }
    }

    public static Port getInstance(int maxContainers, int dockCount, int initialContainers) {
        if (port == null) {
            synchronized (lock) {
                if (port == null) {
                    port = new Port(maxContainers, dockCount, initialContainers);
                    logger.info("Port created");
                }
            }
        } else {
            logger.info("You cannot create another port");
        }
        return port;
    }

    public void loadContainers(int count, UUID id) {
        while (true) {
            int currentCount = availableContainers.get();
            if (currentCount == maxContainers) {
                logger.info("No space available in the port. Ship " + id + " waiting to unload.");
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    logger.error("InterruptedException occurred while waiting to unload containers: " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            } else {
                int newCount = availableContainers.addAndGet(count);
                logger.info("Unloaded from ship " + id + ": " + count + " containers. Available containers: " + newCount);
                break;
            }
        }
    }

    public void unloadContainers(int count, UUID id) {
        while (true) {
            int currentCount = availableContainers.get();
            if (currentCount < count) {
                logger.info("Not enough containers in the port. Ship " + id + " waiting to unload.");
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    logger.error("InterruptedException occurred while waiting to unload containers: " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            } else {
                int newCount = availableContainers.addAndGet(-count);
                logger.info("Loaded onto ship " + id + ": " + count + " containers. Available containers: " + newCount);
                break;
            }
        }
    }

    public void acquireDock(UUID shipId) throws CustomException {
        try {
            docks.acquire();
            Dock dock = null;
            synchronized (dockList) {
                for (Dock d : dockList) {
                    if (d.isAvailable()) {
                        dock = d;
                        dock.setAvailable(false);
                        break;
                    }
                }
            }
            if (dock != null) {
                int dockId = dock.getId();
                dockAssignment.put(shipId, dock); // Keep ship and dock consistent
                System.out.println("Ship " + shipId + " docked at the port. Dock ID: " + dockId);
            }
        } catch (InterruptedException e) {
            System.err.println("InterruptedException occurred while acquiring a dock: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }


    public void releaseDock(UUID shipId) {
        Dock dock = dockAssignment.get(shipId);
        if (dock != null) {
            int dockId = dock.getId();
            dock.setAvailable(true);
            dockAssignment.remove(shipId); // Removing ship and dock mapping
            System.out.println("Ship " + shipId + " released the dock. Dock ID: " + dockId);
        }
        docks.release();
    }

    public Dock getAssignedDock(UUID shipId) {
        return dockAssignment.get(shipId);
    }
}
