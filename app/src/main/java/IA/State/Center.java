package IA.State;

public final class Center extends Node {
    public final static int MAX_CONNECTIONS = 25;
    public final static int MAX_Mbps = 150;

    public Center(int cx, int cy) {
        super(cx, cy, MAX_CONNECTIONS);
    }

    // TODO check if ok
    public int getRealReciveVolumne(int sensorReceivingVolume) {
        return Math.min(MAX_Mbps, sensorReceivingVolume);
    }

    @Override
    public boolean isCenter() {
        return true;
    }

    @Override
    public String toString() {
        return "center(x=" + getCx() + ", y=" + getCy() + ")";
    }
}
