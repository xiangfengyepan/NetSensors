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
import aima.search.framework.SuccessorFunction;

public class StateSuccessorsSA implements SuccessorFunction {
    private final ProblemParameters problem;

    ArrayList<Successor> successors = new ArrayList<>();

    final State[] stateBuffer = new State[8];

    private static final int PROB_CONNECT_CENTER = 80;

    private final int SUCCESSORS_LIMIT = 1000;
    Random random = new Random(123);

    public StateSuccessorsSA(ProblemParameters problem) {
        this.problem = problem;
        for (int i = 0; i < stateBuffer.length; ++i)
            stateBuffer[i] = new State(problem);
    }

    public List<Successor> getSuccessors(Object objectState) {
        State currentState = (State) objectState;
        successors.clear();

        while (successors.size() < SUCCESSORS_LIMIT) {
            exhaustiveConnections(currentState);
        }
        // exaustiveConnections(currentState);
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

    void exhaustiveConnections(State state) {
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
                }
                reuseState(newState);
            }

            for (int i = 0; i < nearestSensors.length; ++i) {
                int dst = nearestSensors[i];

                State newState = newState(state);
                if (newState.connectToSensor(src, dst) == ConnectionResult.Successfull) {
                    addSuccessor(newState, "connectSensors");
                }
                reuseState(newState);

            }
        }
    }

    void exhaustiveConnections(State state, int n) {
        // Crear una copia del estado original para realizar las conexiones
        State newState = newState(state);
    
        for (int i = 0; i < n; i++) {
            // Obtener un sensor de origen aleatorio
            int srcId = random.nextInt(state.sensorsCount());
            Sensor sensorSrc = problem.sensor(srcId);
    
            // Calcular la distancia para el sensor
            int distCenter = sensorSrc.sqDistanceTo(state.problem.center(sensorSrc.nearestCenters()[0]));
            int distSensor = sensorSrc.sqDistanceTo(state.problem.sensor(sensorSrc.nearestSensors()[0]));
            boolean connectToCenter = distCenter <= distSensor || random.nextInt(100) >= PROB_CONNECT_CENTER;
    
            // Intentar conectar el sensor a su destino
            if (connectToCenter) {
                // Conectar al centro aleatorio
                int centerDst = random.nextInt(state.centersCount());
    
                if (newState.connectToCenter(srcId, centerDst) == ConnectionResult.Successfull) {
                    //
                }
            } else {
                // Conectar al sensor aleatorio
                int sensorDst = random.nextInt(state.sensorsCount());
    
                if (newState.connectToSensor(srcId, sensorDst) == ConnectionResult.Successfull) {
                    //
                }
            }
        }
        addSuccessor(newState, "connectSensor");
    
        // Reutilizar el estado al final, luego de aplicar todas las conexiones
        reuseState(newState);
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
