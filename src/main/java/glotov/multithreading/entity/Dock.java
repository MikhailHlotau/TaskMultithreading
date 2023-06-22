package glotov.multithreading.entity;

public class Dock {
    private boolean available;

    public Dock() {
        this.available = true;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}