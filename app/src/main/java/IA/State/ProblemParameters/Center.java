package IA.State.ProblemParameters;

public final class Center extends Node {
    public final static int MAX_CONNECTIONS = 25;
    public final static int MAX_DATA_VOLUME = 150;

    public Center(int cx, int cy, int id) {
        super(cx, cy, MAX_CONNECTIONS, id);
    }

    @Override
    public String toString() {
        return String.format("center(x=%02d, y=%02d)", x, y);
    }

    @Override
    public int maxReceivingVolume() {
        return MAX_DATA_VOLUME;
    }
}
