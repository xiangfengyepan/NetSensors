package IA;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import IA.State.State;
import IA.State.State.ConnectionResult;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public final class AllSuccessors implements SuccessorFunction {
    final int NUM_CONNECTIONS_PER_OPERATION = 1;

    public List<Successor> getSuccessors(Object objectState) {
        State state = (State) objectState;
        ArrayList<Successor> successors = new ArrayList<>();

        doNConnections(state, NUM_CONNECTIONS_PER_OPERATION, successors, "");

        return successors;
    }

    void doNConnections(State state, int n, ArrayList<Successor> successors, String action) {
        if (n <= 0)
            return;

        tryAllConnections(state, (newAction, newState) -> {
            String allActions = action + newAction;
            successors.add(new Successor(allActions, newState));
            doNConnections(newState, n - 1, successors, allActions);
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
}
