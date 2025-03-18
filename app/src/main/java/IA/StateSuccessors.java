package IA;

import java.util.ArrayList;
import java.util.List;

import IA.State.State;
import IA.State.State.ConnectionResult;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public final class StateSuccessors implements SuccessorFunction {
    private final StringBuilder action = new StringBuilder();

    private final HeuristicCost heuristic = new HeuristicCost();
    double bestScore;

    ArrayList<Successor> successors = new ArrayList<>();
    State newState;
    State firstConnectionState;

    public List<Successor> getSuccessors(Object objectState) {
        State currentState = (State) objectState;
        successors.clear();

        newState = new State(currentState);
        firstConnectionState = new State(currentState);

        bestScore = heuristic.getHeuristicValue(currentState);

        // Exaustive successors
        exaustiveConnections(currentState, successors);

        return successors;
    }

    void resetNewState(State state) {
        // //// Normal slow code ////
        // newState = new State(state);

        //// Questionable but significantly optimized version (3 times faster) ////
        newState.copy(state);
    }

    void addState() {
        // //// Normal slow code ////
        // successors.add(new Successor(action.toString(), newState));

        //// Questionable but significantly optimized version (3 times faster) ////
        double score = heuristic.getHeuristicValue(newState);
        if (score < bestScore) {
            bestScore = score;
            successors.add(new Successor(action.toString(), newState));
            newState = new State(newState);
        }
    }

    void exaustiveConnections(State state, ArrayList<Successor> successors) {
        for (int src = 0; src < state.sensorsCount(); ++src) {
            for (int centerDst = 0; centerDst < state.centersCount(); ++centerDst) {

                resetNewState(state);
                if (newState.connectToCenter(src, centerDst) == ConnectionResult.Successfull) {
                    action.setLength(0);
                    action.append(src).append(" -> center ").append(centerDst);

                    addState();

                    firstConnectionState.copy(newState);
                    connectDouble(successors, src, 50);
                }
            }

            int[] nearestSensors = state.problem().sensor(src).nearestSensors();
            for (int i = 0; i < nearestSensors.length; ++i) {
                int dst = nearestSensors[i];

                resetNewState(state);
                if (newState.connectToSensor(src, dst) == ConnectionResult.Successfull) {
                    action.setLength(0);
                    action.append(src).append(" -> ").append(dst);

                    addState();

                    firstConnectionState.copy(newState);
                    connectDouble(successors, src, 30);
                }
            }
        }
    }

    void connectDouble(ArrayList<Successor> successors, int dst, int connectToNearestN) {
        action.append(" & ");
        int baseLength = action.length();

        int[] nearestSensors = firstConnectionState.problem().sensor(dst).nearestSensors();
        int nConnections = Math.min(nearestSensors.length, connectToNearestN);
        for (int i = 0; i < nConnections; ++i) {
            int src = nearestSensors[i];

            resetNewState(firstConnectionState);
            if (newState.connectToSensor(src, dst) == ConnectionResult.Successfull) {
                action.delete(baseLength, action.length());
                action.append(src).append(" -> ").append(dst);

                addState();
            }
        }
    }
}
