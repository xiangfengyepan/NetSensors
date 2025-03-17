package IA;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import IA.State.State;
import IA.State.State.ConnectionResult;
import IA.State.ProblemParameters.Sensor;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class WeightedSuccessors implements SuccessorFunction {
    private static final int NUM_CONNECTIONS_PER_OPERATION = 4;
    private static final int MAX_SUCCESSORS = 80000;

    final int PROB_CONNECT_CENTER = 10;
    final int PROB_CONNECT_NEAREST_CENTER = 50;
    final int PROB_CONNECT_NEAREST_SENSOR = 30;

    final Random random = new Random();

    public List<Successor> getSuccessors(Object objectState) {
        State state = (State) objectState;
        ArrayList<Successor> successors = new ArrayList<>();

        while (successors.size() < MAX_SUCCESSORS) {
            State newState = new State(state);
            StringBuilder action = new StringBuilder();

            int connections = random.nextInt(NUM_CONNECTIONS_PER_OPERATION);
            for (int i = 0; i < connections; ++i)
                makeWeightedConnection(newState, action);

            successors.add(new Successor(action.toString(), state));
        }

        return successors;
    }

    void makeWeightedConnection(State state, StringBuilder action) {
        int srcId = random.nextInt(state.sensorsCount());
        Sensor src = state.problem().sensor(srcId);

        if (random.nextInt(100) >= PROB_CONNECT_CENTER) {
            for (int dstId : src.nearestCenters()) {
                if (random.nextInt(100) >= PROB_CONNECT_NEAREST_CENTER) {
                    if (state.connectToCenter(srcId, dstId) == ConnectionResult.Successfull) {
                        action.append("ConnectToCenter(").append(src).append(", ");
                        action.append(state.problem().center(dstId)).append(")");
                        break;
                    }
                }
            }
        } else {
            for (int dstId : src.nearestSensors()) {
                if (random.nextInt(100) >= PROB_CONNECT_NEAREST_SENSOR) {
                    if (state.connectToSensor(srcId, dstId) == ConnectionResult.Successfull) {
                        action.append("ConnectToSensor(").append(src).append(", ");
                        action.append(state.problem().sensor(dstId)).append(")");
                        break;
                    }

                }
            }
        }
    }
}
