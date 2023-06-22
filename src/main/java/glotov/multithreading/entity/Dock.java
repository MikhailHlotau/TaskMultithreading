package glotov.multithreading.entity;

class Dock {
    private int id;
    private boolean available;

    public Dock(int id) {
        this.id = id;
        this.available = true;
    }

    public int getId() {
        return id;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}