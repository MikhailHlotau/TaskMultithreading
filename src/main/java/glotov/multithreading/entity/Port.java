package glotov.multithreading.entity;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import glotov.multithreading.exception.CustomException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Port {
    private static final Logger logger = LogManager.getLogger(Port.class);
    private final int maxContainers;
    private final Semaphore docks;
    private final AtomicInteger availableContainers;

    public Port(int maxContainers, int dockCount, int initialContainers) {
        this.maxContainers = maxContainers;
        this.docks = new Semaphore(dockCount, true);
        this.availableContainers = new AtomicInteger(initialContainers);
    }

    public void loadContainers(int count, int id) throws CustomException {
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

    public void unloadContainers(int count, int id) throws CustomException {
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
                int unloadedCount = availableContainers.addAndGet(-count);
                logger.info("Loaded to ship " + id + ": " + count + " containers. Available containers: " + unloadedCount);
                break;
            }
        }
    }

    public Dock acquireDock() throws CustomException {
        try {
            // Acquire a dock from the semaphore
            docks.acquire();
        } catch (InterruptedException e) {
            // InterruptedException occurred while acquiring the dock, log, interrupt the thread, and throw CustomException
            logger.error("InterruptedException occurred while acquiring dock: " + e.getMessage());
            Thread.currentThread().interrupt();
            throw new CustomException(e);
        }
        return new Dock();
    }

    public void releaseDock(Dock dock) {
        // Release the dock back to the semaphore
        docks.release();
    }
}