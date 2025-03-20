package IA;

import java.util.ArrayList;
import java.util.List;

import IA.State.State;
import IA.State.State.ConnectionResult;
import IA.State.ProblemParameters.Node;
import IA.State.ProblemParameters.ProblemParameters;
import IA.State.ProblemParameters.Sensor;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public final class StateSuccessors implements SuccessorFunction {
    private final ProblemParameters problem;

    private final HeuristicCost heuristic = new HeuristicCost();
    private double bestScore;
    private State bestState;
    private String bestAction;

    ArrayList<Successor> successors = new ArrayList<>();

    final State[] stateBuffer = new State[8];
    int usedStateBuffers = 0;

    public StateSuccessors(ProblemParameters problem) {
        this.problem = problem;
        for (int i = 0; i < stateBuffer.length; ++i)
            stateBuffer[i] = new State(problem);
    }

    public List<Successor> getSuccessors(Object objectState) {
        State currentState = (State) objectState;
        successors.clear();

        bestScore = heuristic.getHeuristicValue(currentState);
        bestState = new State(currentState);

        // Exaustive successors
        exaustiveConnections(currentState);

        successors.add(new Successor(bestAction, new State(bestState)));
        return successors;
    }

    State newState(State stateToCopy) {
        State newState = stateBuffer[usedStateBuffers++];
        newState.copy(stateToCopy);
        return newState;
    }

    void reuseState(State stateToReuse) {
        --usedStateBuffers;
        assert stateBuffer[usedStateBuffers] == stateToReuse;
    }

    void addSuccessor(State successor, String action) {
        double score = heuristic.getHeuristicValue(successor);

        if (score < bestScore) {
            bestScore = score;
            bestState.copy(successor);
            bestAction = action;
        }
    }

    void exaustiveConnections(State state) {
        for (int src = 0; src < state.sensorsCount(); ++src) {
            Sensor sensorSrc = problem.sensor(src);
            int[] nearestCenters = sensorSrc.nearestCenters();
            int[] nearestSensors = sensorSrc.nearestSensors();

            for (int i = 0; i < nearestCenters.length; ++i) {
                int centerDst = nearestCenters[i];

                State newState = newState(state);
                if (newState.connectToCenter(src, centerDst) == ConnectionResult.Successfull) {
                    addSuccessor(newState, "connectCenter");
                    connectChain(newState, sensorSrc, problem.center(centerDst), 80 - i * 20, 1);

                    if (!state.sensorById(src).isConnectedToDataCenter()) {
                        Sensor previousDst = state.sensorById(src).dst().sensor();
                        connectReplaceing(newState, sensorSrc, previousDst, 50, 1);
                    }
                }
                reuseState(newState);
            }

            for (int i = 0; i < nearestSensors.length; ++i) {
                int dst = nearestSensors[i];

                State newState = newState(state);
                if (newState.connectToSensor(src, dst) == ConnectionResult.Successfull) {
                    addSuccessor(newState, "connectSensors");
                    connectChain(newState, sensorSrc, problem.sensor(dst), 80 - i, 1);

                    if (!state.sensorById(src).isConnectedToDataCenter()) {
                        Sensor previousDst = state.sensorById(src).dst().sensor();
                        connectReplaceing(newState, sensorSrc, previousDst, 50, 1);
                    }
                }
                reuseState(newState);

            }
        }
    }

    void connectChain(State state, Sensor chainSrc, Node chainDst, int connectToNearestN, int depth) {
        if (depth <= 0)
            return;

        int dst = chainSrc.id;
        int[] nearestSensors = problem.sensor(dst).nearestSensors();

        int nConnections = Math.min(nearestSensors.length, connectToNearestN);
        for (int i = 0; i < nConnections; ++i) {
            int src = nearestSensors[i];
            Sensor sensorSrc = problem.sensor(src);

            // This connection won't introduce immediate losses
            int available = chainSrc.maxReceivingVolume() - state.sensorById(dst).sendingVolume();
            if (available < sensorSrc.maxCaptureVolume())
                continue;

            if (!chainCondition(sensorSrc, chainSrc, chainDst))
                continue;

            State newState = newState(state);
            if (newState.connectToSensor(src, dst) == ConnectionResult.Successfull) {
                addSuccessor(newState, "chain");
                connectChain(newState, sensorSrc, chainDst, connectToNearestN / 2 - i, depth - 1);
            }
            reuseState(newState);
        }
    }

    void connectReplaceing(State state, Sensor previousSensorSrc, Sensor sensorDst,
            int connectToNearestN, int depth) {
        if (depth <= 0)
            return;

        int dst = sensorDst.id;
        int[] nearestSensors = problem.sensor(dst).nearestSensors();

        int nConnections = Math.min(nearestSensors.length, connectToNearestN);
        for (int i = 0; i < nConnections; ++i) {
            int src = nearestSensors[i];
            Sensor sensorSrc = problem.sensor(src);

            // Previously, the connection was close to saturated.
            int pastAvailable = sensorDst.maxReceivingVolume() -
                    state.sensorById(previousSensorSrc.id).sendingVolume();
            if (pastAvailable >= sensorSrc.maxCaptureVolume())
                continue;

            // This connection won't introduce immediate losses
            int available = sensorDst.maxReceivingVolume() - state.sensorById(dst).sendingVolume();
            if (available < sensorSrc.maxCaptureVolume())
                continue;

            State newState = newState(state);
            if (newState.connectToSensor(src, dst) == ConnectionResult.Successfull) {
                addSuccessor(newState, "replace");

                if (!state.sensorById(sensorSrc.id).isConnectedToDataCenter()) {
                    Sensor previousSensorDst = state.sensorById(sensorSrc.id).dst().sensor();
                    connectReplaceing(newState, sensorSrc, previousSensorDst, 40, depth
                            - 1);
                }
            }
            reuseState(newState);
        }
    }

    boolean chainCondition(Sensor newNode, Sensor chainSrc, Node chainDst) {
        if (chainSrc.maxReceivingVolume() < newNode.maxCaptureVolume())
            return false;

        int dDstSrc = chainDst.sqDistanceTo(chainSrc);
        int dDstNew = chainDst.sqDistanceTo(newNode);
        int dSrcNew = chainSrc.sqDistanceTo(newNode);

        return dDstNew > dDstSrc && dDstNew > dSrcNew;
    }
}
