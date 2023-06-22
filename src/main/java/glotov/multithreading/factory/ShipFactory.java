package glotov.multithreading.factory;

import glotov.multithreading.entity.Port;
import glotov.multithreading.entity.Ship;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import glotov.multithreading.exception.CustomException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShipFactory {
    private static final Logger logger = LogManager.getLogger(ShipFactory.class);
    private static final Random random = new Random();
    private static final Set<Integer> usedIds = new HashSet<>();

    public static Ship createShip(int id, Port port) throws CustomException {
        int capacity = getRandomCapacity();
        int currentLoad = random.nextInt(capacity + 1);
        logger.info("Creating ship with ID: " + id);
        try {
            return new Ship(id, port, capacity, currentLoad);
        } catch (Exception e) {
            logger.error("Exception occurred while creating ship: " + e.getMessage());
            throw new CustomException("Failed to create ship.", e);
        }
    }

    public static Ship createRandomShip(Port port) throws CustomException {
        int id = generateUniqueId();
        return createShip(id, port);
    }

    private static int generateUniqueId() {
        int id = random.nextInt(10000);
        while (usedIds.contains(id)) {
            id = random.nextInt(10000);
        }
        usedIds.add(id);
        return id;
    }

    private static int getRandomCapacity() {
        int index = random.nextInt(ShipCapacity.values().length);
        return ShipCapacity.values()[index].getCapacity();
    }
}