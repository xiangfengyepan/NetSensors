package IA.State.ProblemParameters;

import java.util.HashSet;
import java.util.Random;

public final class ProblemParameters {
    private final Sensor[] sensors;
    private final Center[] centers;

    public ProblemParameters(int nCenters, int nSens, int centerSeed, int sensorSeed) {
        sensors = new Sensor[nSens];
        centers = new Center[nCenters];

        HashSet<Long> occupied = new HashSet<>((nCenters + nSens) * 2);

        Random rand = new Random(centerSeed);
        for (int i = 0; i < nCenters; ++i) {
            int cx, cy;
            do {
                cx = rand.nextInt(100);
                cy = rand.nextInt(100);
            } while (!occupied.add((long) cx << 32 | cy));

            centers[i] = new Center(cx, cy);
        }

        rand = new Random(sensorSeed);
        for (int i = 0; i < nSens; ++i) {
            int cx, cy;
            do {
                cx = rand.nextInt(100);
                cy = rand.nextInt(100);
            } while (!occupied.add((long) cx << 32 | cy));

            final int[] capacities = { 1, 2, 5 };
            // int capacity = capacities[rand.nextInt(capacities.length)];
            int capacity = capacities[i % 3];

            sensors[i] = new Sensor(capacity, cx, cy);
        }

        for (Sensor sensor : sensors) {
            sensor.updateNearesCenters(centers);
            sensor.updateNearesSensors(sensors);
        }
    }

    public Sensor sensor(int id) {
        return sensors[id];
    }

    public Center center(int id) {
        return centers[id];
    }

    public int sensorsCount() {
        return sensors.length;
    }

    public int centersCount() {
        return centers.length;
    }

    public void print() {
        for (Sensor sensor : sensors)
            System.out.println(sensor);
        for (Center center : centers)
            System.out.println(center);
    }
}
