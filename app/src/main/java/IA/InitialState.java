package IA;

import java.util.Random;

import IA.State.State.ConnectionResult;
import IA.State.State;
import IA.State.ProblemParameters.Sensor;

public final class InitialState {
    private static final int PROB_CONNECT_CENTER = 80;
    private static final double SENSORS_DEGREE = 4.;
    private static final Random random = new Random(469874635498745L);

    public static void inilializeConnections(State state) {
        // long seed = random.nextLong();
        // random.setSeed(seed);
        // System.out.println("Seed: " + seed);

        for (int srcId = 0; srcId < state.sensorsCount(); ++srcId) {
            Sensor src = state.problem.sensor(srcId);

            int distCenter = src.sqDistanceTo(state.problem.center(src.nearestCenters()[0]));
            int distSensor = src.sqDistanceTo(state.problem.sensor(src.nearestSensors()[0]));

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
                    // See Function in [desmos.com]: \operatorname{round}\left(n\cdot x^{g}\right)
                    index = (int) Math.round(n * Math.pow(random.nextDouble(), SENSORS_DEGREE));
                } while (state.connectToSensor(srcId,
                        src.nearestSensors()[index]) == ConnectionResult.UnableToConnect);
            }
        }
    }

}
