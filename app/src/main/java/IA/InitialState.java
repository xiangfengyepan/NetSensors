package IA;

import java.util.Random;

import IA.State.State.ConnectionResult;
import IA.State.State;
import IA.State.ProblemParameters.Sensor;

public final class InitialState {
    private static final int PROB_CONNECT_CENTER = 80;
    private static final double SENSORS_DEGREE = 4.; // See Function: \operatorname{round}\left(n\cdot x^{g}\right)
    private static final Random random = new Random();

    public static void inilializeConnections(State state) {
        long seed = random.nextLong();
        // 9876134l  (eventually (~30min) 36.90660)
        //
        //Seed: 3512287543392061130
        //2784 ms
        //Cost: 178392
        //Volume: 263
        //Best Score (To minimize): 37.28656538134476
        //
        // Seed: -1115645851298615522
        // 3172 ms
        // Cost: 176776
        // Volume: 263
        // Best Score (To minimize): 36.948797490092616

        random.setSeed(seed);
        System.out.println("Seed: " + seed);

        for (int srcId = 0; srcId < state.sensorsCount(); ++srcId) {
            Sensor src = state.problem().sensor(srcId);

            int distCenter = src.sqDistanceTo(state.problem().center(src.nearestCenters()[0]));
            int distSensor = src.sqDistanceTo(state.problem().sensor(src.nearestSensors()[0]));

            boolean connectToCenter = distCenter <= distSensor || random.nextInt(100) >= PROB_CONNECT_CENTER;

            if (connectToCenter) {
                for (int dstId : src.nearestCenters()) {
                    if (state.connectToCenter(srcId, dstId) != ConnectionResult.UnableToConnect)
                        break;
                }
            } else {
                int index = 0;
                int n = src.nearestSensors().length / 2;

                do {
                    index = (int) Math.round(n * Math.pow(random.nextDouble(), SENSORS_DEGREE));
                } while (state.connectToSensor(srcId, src.nearestSensors()[index]) == ConnectionResult.UnableToConnect);
            }
        }
    }

}
