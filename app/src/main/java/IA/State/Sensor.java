package IA.State;

public final class Sensor extends Node {
    public final static int MAX_CONNECTIONS = 3;
    int capacity;

    public Sensor(int capacity, int cx, int cy) {
        super(cx, cy, MAX_CONNECTIONS);
        this.capacity = capacity;
    };

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public boolean isCenter() {
        return false;
    }

    @Override
    public String toString() {
        return "sensor(x=" + getCx() + ", y=" + getCy() + ", capacity=" + capacity + ")";
    }
}
