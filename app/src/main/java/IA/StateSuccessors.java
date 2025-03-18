package IA;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

import IA.State.State;
import IA.State.State.ConnectionResult;
import IA.State.ProblemParameters.Sensor;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public final class StateSuccessors implements SuccessorFunction {
    int weightedNumConnections = 3;

    private static final int PROB_CONNECT_CENTER = 10;
    private static final int PROB_CONNECT_NEAREST_CENTER = 50;
    private static final int PROB_CONNECT_NEAREST_SENSOR = 30;

    private final Random random = new Random();
    private final StringBuilder action = new StringBuilder();

    private final int connectToNearestN = 10;

    public List<Successor> getSuccessors(Object objectState) {
        State state = (State) objectState;
        int aproximatedCapacity = state.sensorsCount() * (state.sensorsCount() + state.centersCount());
        ArrayList<Successor> successors = new ArrayList<>(aproximatedCapacity);

        // Exaustive successors
        connect1(state, successors);

        /*
        // Weighted successors
        for (int i = 0; i < maxWeightedStates; ++i) {
            State newState = new State(state);
            StringBuilder action = new StringBuilder();

            int connections = exaustiveNumConnections + 1
                    + random.nextInt(weightedNumConnections - exaustiveNumConnections);
            for (int connected = 0; connected < connections; ++connected)
                makeWeightedConnection(newState, action);

            if (action.length() > 0)
                successors.add(new Successor(action.toString(), state));
        }
        */

        return successors;
    }

    void connect1(State state, ArrayList<Successor> successors) {
        for (int src = 0; src < state.sensorsCount(); ++src) {
            for (int centerDst = 0; centerDst < state.centersCount(); ++centerDst) {
                State newState = new State(state);
                if (newState.connectToCenter(src, centerDst) == ConnectionResult.Successfull) {
                    action.setLength(0);
                    action.append(src).append(" -> center ").append(centerDst);

                    successors.add(new Successor(action.toString(), newState));

                    connect2(newState, successors, src);
                }
            }

            int[] nearestSensors = state.problem().sensor(src).nearestSensors();
            for (int i = 0; i < nearestSensors.length; ++i) {
                int dst = nearestSensors[i];

                State newState = new State(state);
                if (newState.connectToSensor(src, dst) == ConnectionResult.Successfull) {
                    action.setLength(0);
                    action.append(src).append(" -> ").append(dst);

                    successors.add(new Successor(action.toString(), newState));

                    connect2(newState, successors, src);
                }
            }
        }
    }

    void connect2(State state, ArrayList<Successor> successors, int skipSensorId) {
        action.append(" & ");
        int baseLength = action.length();

        int dst = skipSensorId;

        int[] nearestSensors = state.problem().sensor(skipSensorId).nearestSensors();
        for (int i = 0; i < 20; ++i) {
            int src = nearestSensors[i];

            if (src == skipSensorId)
                continue;


            State newState = new State(state);
            if (newState.connectToSensor(src, dst) == ConnectionResult.Successfull) {
                action.setLength(baseLength);
                action.append(src).append(" -> ").append(dst);

                successors.add(new Successor(action.toString(), newState));
            }
        }
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
