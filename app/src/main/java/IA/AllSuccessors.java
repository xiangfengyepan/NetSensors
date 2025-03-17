package IA;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import IA.State.State;
import IA.State.State.ConnectionResult;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public final class AllSuccessors implements SuccessorFunction {
    final int NUM_CONNECTIONS_PER_OPERATION = 2;

    public List<Successor> getSuccessors(Object objectState) {
        State state = (State) objectState;
        ArrayList<Successor> successors = new ArrayList<>();

        doNConnections(state, NUM_CONNECTIONS_PER_OPERATION, successors);

        return successors;
    }

    void doNConnections(State state, int n, ArrayList<Successor> successors) {
        if (n <= 0)
            return;

        tryAllConnections(state, newState -> {
            successors.add(new Successor(null, newState));
            doNConnections(newState, n - 1, successors);
        });
    }

    void tryAllConnections(State state, Consumer<State> callback) {
        for (int src = 0; src < state.sensorsCount(); ++src) {
            for (int dst = 0; dst < state.sensorsCount(); ++dst) {
                State newState = new State(state);
                if (newState.connectToSensor(src, dst) == ConnectionResult.Successfull)
                    callback.accept(newState);
            }
            for (int centerDst = 0; centerDst < state.centersCount(); ++centerDst) {
                State newState = new State(state);
                if (newState.connectToCenter(src, centerDst) == ConnectionResult.Successfull)
                    callback.accept(newState);
            }
        }
    }
}
