package IA;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import IA.State.State;
import IA.State.State.ConnectionResult;
import IA.State.ProblemParameters.Node;
import IA.State.ProblemParameters.ProblemParameters;
import IA.State.ProblemParameters.Sensor;
import aima.search.framework.Successor;

public class StateSuccessorsSA {
    private final ProblemParameters problem;

    ArrayList<Successor> successors = new ArrayList<>();

    final State[] stateBuffer = new State[8];

    private static final int PROB_CONNECT_CENTER = 80;
    
    private final int SUCCESSORS_LIMIT = 2000;
    Random random = new Random(123);

    public StateSuccessorsSA(ProblemParameters problem) {
        this.problem = problem;
        for (int i = 0; i < stateBuffer.length; ++i)
            stateBuffer[i] = new State(problem);
    }

    public List<Successor> getSuccessors(Object objectState) {
        State currentState = (State) objectState;
        successors.clear();

        // Exaustive successors
        for (int i = 0; i < SUCCESSORS_LIMIT; ++i) {
            exaustiveConnections(currentState);
        }
        return successors;
    }

    State newState(State stateToCopy) {
        return new State(stateToCopy);
    }

    void reuseState(State stateToReuse) {
        // --usedStateBuffers;
        // assert stateBuffer[usedStateBuffers] == stateToReuse;
    }

    void addSuccessor(State successor, String action) {
        successors.add(new Successor(action, successor));
    }

    void exaustiveConnections(State state) {
        int srcId = random.nextInt(state.sensorsCount());
        Sensor sensorSrc = problem.sensor(srcId);

        int distCenter = sensorSrc.sqDistanceTo(state.problem.center(sensorSrc.nearestCenters()[0]));
        int distSensor = sensorSrc.sqDistanceTo(state.problem.sensor(sensorSrc.nearestSensors()[0]));
        boolean connectToCenter = distCenter <= distSensor || random.nextInt(100) >= PROB_CONNECT_CENTER;

        if (connectToCenter) {
            int centerDst = random.nextInt(state.centersCount());

            State newState = newState(state);
            if (newState.connectToCenter(srcId, centerDst) == ConnectionResult.Successfull) {
                addSuccessor(newState, "connectCenter");
            }
            reuseState(newState);
        } else {
            int sensorDst = random.nextInt(state.sensorsCount());

            State newState = newState(state);
            if (newState.connectToSensor(srcId, sensorDst) == ConnectionResult.Successfull) {
                addSuccessor(newState, "connectCenter");
            }
            reuseState(newState);
        }
    }

    void chainConnections(State state) {
        // first connection
        int srcId = random.nextInt(state.sensorsCount());
        Sensor sensorSrc = problem.sensor(srcId);

        int srcId2 = random.nextInt(state.sensorsCount());
        Sensor sensorSrc2 = problem.sensor(srcId2);

        int distCenter = sensorSrc.sqDistanceTo(state.problem.center(sensorSrc.nearestCenters()[0]));
        int distSensor = sensorSrc.sqDistanceTo(state.problem.sensor(sensorSrc.nearestSensors()[0]));
        boolean connectToCenter = distCenter <= distSensor || random.nextInt(100) >= PROB_CONNECT_CENTER;

        if (connectToCenter) {
            int centerDst = random.nextInt(state.centersCount());
            Node centerNode = problem.center(centerDst);

            if (chainCondition(sensorSrc2, sensorSrc, centerNode)) {
                State newState = newState(state);
                if (newState.connectToCenter(srcId, centerDst) == ConnectionResult.Successfull
                        && newState.connectToSensor(srcId2, srcId) == ConnectionResult.Successfull) {
                    addSuccessor(newState, "chainCenter");

                }
                reuseState(newState);
            }

        } else {
            int sensorDst = random.nextInt(state.sensorsCount());
            Node sensorNode = problem.sensor(sensorDst);

            if (chainCondition(sensorSrc2, sensorSrc, sensorNode)) {

                State newState = newState(state);
                if (newState.connectToSensor(srcId, sensorDst) == ConnectionResult.Successfull
                        && newState.connectToSensor(srcId2, srcId) == ConnectionResult.Successfull) {
                    addSuccessor(newState, "chainSensor");
                }
                reuseState(newState);
            }
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
