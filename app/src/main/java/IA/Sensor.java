package IA;

public class Sensor {
    private int maxConnections = 3;

    private float capacity;
    private int cx;
    private int cy;

    public Sensor(float capacity, int cx, int cy) {
        this.capacity = capacity;
        this.cx = cx;
        this.cy = cy;
    };

    public float getCapacity() {
        return capacity;
    }

    public int getCx() {
        return cx;
    }

    public int getCy() {
        return cy;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setCx(int cx) {
        this.cx = cx;
    }

    public void setCy(int cy) {
        this.cy = cy;
    }

    public int getMaxConnections() {
        return maxConnections;
    }
}
