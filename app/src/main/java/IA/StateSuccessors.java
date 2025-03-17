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
    int exaustiveNumConnections = 1;
    int weightedNumConnections = 3;
    int maxWeightedStates = 1000;

    private static final int PROB_CONNECT_CENTER = 10;
    private static final int PROB_CONNECT_NEAREST_CENTER = 50;
    private static final int PROB_CONNECT_NEAREST_SENSOR = 30;

    private final Random random = new Random();

    public List<Successor> getSuccessors(Object objectState) {
        State state = (State) objectState;
        ArrayList<Successor> successors = new ArrayList<>(maxWeightedStates);

        // Exaustive successors
        doExausitveConnections(state, exaustiveNumConnections, successors, "");

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
        return successors;
    }

    void doExausitveConnections(State state, int n, ArrayList<Successor> successors, String action) {
        if (n <= 0)
            return;

        tryAllConnections(state, (newAction, newState) -> {
            String allActions = action + newAction;
            successors.add(new Successor(allActions, newState));
            doExausitveConnections(newState, n - 1, successors, allActions);
        });
    }

    void tryAllConnections(State state, BiConsumer<String, State> callback) {
        for (int src = 0; src < state.sensorsCount(); ++src) {
            for (int centerDst = 0; centerDst < state.centersCount(); ++centerDst) {
                State newState = new State(state);
                if (newState.connectToCenter(src, centerDst) == ConnectionResult.Successfull) {
                    StringBuilder action = new StringBuilder("ConnectToCenter(");
                    action.append(state.problem().sensor(src)).append(", ");
                    action.append(state.problem().center(centerDst)).append(")");

                    callback.accept(action.toString(), newState);
                }
            }

            for (int dst = 0; dst < state.sensorsCount(); ++dst) {
                State newState = new State(state);
                if (newState.connectToSensor(src, dst) == ConnectionResult.Successfull) {
                    StringBuilder action = new StringBuilder("ConnectToSensor(");
                    action.append(state.problem().sensor(src)).append(", ");
                    action.append(state.problem().sensor(dst)).append(")");

                    callback.accept(action.toString(), newState);
                }
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
