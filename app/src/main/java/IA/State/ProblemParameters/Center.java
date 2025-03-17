package IA.State.ProblemParameters;

public final class Center extends Node {
    public final static int MAX_CONNECTIONS = 25;
    public final static int MAX_DATA_VOLUME = 150;

    public Center(int cx, int cy) {
        super(cx, cy, MAX_CONNECTIONS);
    }

    @Override
    public String toString() {
        return String.format("center(x=%02d, y=%02d)", getCx(), getCy());
    }
}
