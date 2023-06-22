package glotov.multithreading.factory;

public enum ShipCapacity {
    SMALL(500),
    MEDIUM(1000),
    LARGE(1500);

    private final int capacity;

    ShipCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return "ShipCapacity{" +
                "capacity=" + capacity +
                '}';
    }
}
