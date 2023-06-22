package glotov.multithreading.main;

import glotov.multithreading.entity.Port;
import glotov.multithreading.entity.Ship;
import glotov.multithreading.exception.CustomException;
import glotov.multithreading.factory.ShipFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        int portCapacity = 5000;
        int dockCount = 3;
        int initialContainers = 3000;
        int shipCount = 10;

        Port port = new Port(portCapacity,dockCount, initialContainers);
        try (ExecutorService executor = Executors.newFixedThreadPool(dockCount)) {

            for (int i = 0; i < shipCount; i++) {
                Ship ship = ShipFactory.createRandomShip(port);
                executor.submit(ship);
            }

            executor.shutdown();
        } catch (CustomException e) {
            throw new RuntimeException(e);
        }
    }
}
