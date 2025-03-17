package IA;

import java.util.Random;

import IA.State.State.ConnectionResult;
import IA.State.State;
import IA.State.ProblemParameters.Sensor;

public final class InitialState {
    private static final int PROB_CONNECT_CENTER = 50;
    private static final int PROB_CONNECT_NEAREST_CENTER = 80;
    private static final int PROB_CONNECT_NEAREST_SENSOR = 40;

    public static void inilializeConnections(State state) {
        Random random = new Random(4897496845l);

        for (int srcId = 0; srcId < state.sensorsCount(); ++srcId) {
            Sensor src = state.problem().sensor(srcId);

            for (int dstId : src.nearestCenters()) {
                if (random.nextInt(100) >= PROB_CONNECT_NEAREST_CENTER) {
                    if (state.connectToCenter(srcId, dstId) != ConnectionResult.UnableToConnect)
                        break;
                }
            }
            state.totalCost();
            if (random.nextInt(100) >= PROB_CONNECT_CENTER)
                continue;

            for (int dstId : src.nearestSensors()) {
                if (random.nextInt(100) >= PROB_CONNECT_NEAREST_SENSOR) {
                    if (state.connectToSensor(srcId, dstId) != ConnectionResult.UnableToConnect)
                        break;
                }
            }
            state.totalCost();
        }
    }

}
