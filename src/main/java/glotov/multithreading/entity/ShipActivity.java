package glotov.multithreading.entity;

import java.util.Random;

public enum ShipActivity {
    UNLOAD,
    LOAD,
    UNLOAD_AND_LOAD;
    private static final Random random = new Random();

    public static ShipActivity getRandomActivity() {
        int index = random.nextInt(values().length);
        return values()[index];
    }
}
