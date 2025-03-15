package IA.State;

import java.util.ArrayList;
import java.util.List;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class StateSuccessor implements SuccessorFunction {
    private final int NUM_OPS = 3;
    private boolean[] vops;

    public StateSuccessor(boolean[] ops) {
        vops = new boolean[NUM_OPS];
        for (int i = 0; i < NUM_OPS; i++)
            vops[i] = ops[i];
    }

    public List<Successor> getSuccessors(Object state) {
        ArrayList<Successor> llistaSuccessors = new ArrayList<Successor>();
        State board = (State) state;

        // Usefull variable
        float totalCost = board.getTotalCost();
        Sensors sensors = board.getSensors();
        DataCenters dataCenters = board.getDataCenters();
        Node[] sensorCennectedTo = board.getSensorConnectedTo();
        int[] sensorReceivingVolume = board.getSensorReceivingVolume();

        ArrayList<Node> allNodes = new ArrayList<Node>() {
            {
                addAll(sensors);
                addAll(dataCenters);
            }
        };

        if (vops[0]) // Operador add edge
        {
            // for (int srcIndex = 0; srcIndex < sensors.size(); srcIndex++) {
            //     for (Node dst : allNodes) {
            //         if (dst != sensorCennectedTo[srcIndex]) {
            //             State newBoard = new State(board);
            //             // TODO check if can connect
            //             newBoard.connectSensor(srcIndex, dst);
            //             llistaSuccessors
            //                     .add(new Successor(sensors.get(srcIndex).toString() + "->" + dst.toString(), newBoard));
            //         }
            //     }
            // }

        } 
        if (vops[1]) // Operator delete edge
        {
            // for (int srcIndex = 0; srcIndex < sensors.size(); srcIndex++) {
            //     if (sensorCennectedTo[srcIndex] != null) {
            //         State newBoard = new State(board);
            //         newBoard.connectSensor(srcIndex, null);
            //         llistaSuccessors
            //                 .add(new Successor(sensors.get(srcIndex).toString() + "->" + "null", newBoard));
            //     }
            // }
        } 
        if (vops[2]) {
            // TODO
        }

        return llistaSuccessors;
    }
}
