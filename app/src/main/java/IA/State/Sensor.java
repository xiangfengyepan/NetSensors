package IA.State;

public final class Sensor extends Node {
    public final static int MAX_CONNECTIONS = 3;
    int capacity;

    public Sensor(int capacity, int cx, int cy) {
        super(cx, cy, MAX_CONNECTIONS);
        this.capacity = capacity;
    };

    @Override
    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacitya) {
        this.capacity = capacitya;
    }

    // TODO check if ok
    public int getRealSendingVolumne(Node dst, int sensorReceivingVolume)
    {
        int realSendingVolumne = Math.min(this.capacity * 3, sensorReceivingVolume + this.capacity);
        int maxReciveVolumne = dst.getCapacity() * 2;
        if (dst.isCenter())
            maxReciveVolumne = ((Center) dst).getRealReciveVolumne(sensorReceivingVolume);

        System.out.println(sensorReceivingVolume);
        return Math.min(realSendingVolumne, maxReciveVolumne);
    }

    // TODO check if ok
    public int getMaxTransmition()
    {
        return this.capacity * 3;
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
