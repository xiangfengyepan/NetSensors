package IA.State.ProblemParameters;

import java.util.stream.IntStream;

public final class Sensor extends Node {
    public final static int MAX_CONNECTIONS = 3;

    private final int maxCaptureVolume;
    private int[] nearestSensors;
    private int[] nearestCenters;

    public Sensor(int maxCaptureVolume, int x, int y, int id) {
        super(x, y, MAX_CONNECTIONS, id);
        this.maxCaptureVolume = maxCaptureVolume;
    };

    public int maxCaptureVolume() {
        return maxCaptureVolume;
    }

    @Override
    public int maxReceivingVolume() {
        return maxCaptureVolume * 2;
    }

    public int maxTransmition() {
        return maxCaptureVolume * 3;
    }

    public int[] nearestSensors() {
        return nearestSensors;
    }

    public int[] nearestCenters() {
        return nearestCenters;
    }

    public void updateNearesSensors(Sensor[] allSensors) {
        nearestSensors = IntStream.range(0, allSensors.length)
                .filter(id -> allSensors[id].x != x || allSensors[id].y != y)
                .boxed()
                .sorted((a, b) -> Integer.compare(sqDistanceTo(allSensors[a]), sqDistanceTo(allSensors[b])))
                .mapToInt(id -> id).toArray();
    }

    public void updateNearesCenters(Center[] allCenters) {
        nearestCenters = IntStream.range(0, allCenters.length)
                .boxed()
                .sorted((a, b) -> Integer.compare(sqDistanceTo(allCenters[a]), sqDistanceTo(allCenters[b])))
                .mapToInt(id -> id).toArray();
    }

    @Override
    public String toString() {
        return String.format("sensor(x=%02d, y=%02d, capacity=%02d)", x, y, maxCaptureVolume);
    }
}
