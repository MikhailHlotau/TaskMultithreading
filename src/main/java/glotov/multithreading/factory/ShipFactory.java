package glotov.multithreading.factory;

import glotov.multithreading.entity.Port;
import glotov.multithreading.entity.Ship;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import glotov.multithreading.exception.CustomException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShipFactory {
    private static final Logger logger = LogManager.getLogger(ShipFactory.class);
    private static final Random random = new Random();
    private static final Set<UUID> usedIds = new HashSet<UUID>();

    public static Ship createShip(UUID id, Port port) {
        int capacity = getRandomCapacity();
        int currentLoad = random.nextInt(capacity + 1);
        logger.info("Creating ship: id = " + id + ", capacity = " + capacity + ", currentLoad = " + currentLoad);
        return new Ship(id, port, capacity, currentLoad);
    }

    public static Ship createRandomShip(Port port) throws CustomException {
        UUID id = generateUniqueId();
        return createShip(id, port);
    }

    private static UUID generateUniqueId() {
        UUID id;
        do {
            id = UUID.randomUUID();
        } while (usedIds.contains(id));
        usedIds.add(id);
        return id;
    }

    private static int getRandomCapacity() {
        int index = random.nextInt(ShipCapacity.values().length);
        return ShipCapacity.values()[index].getCapacity();
    }
}