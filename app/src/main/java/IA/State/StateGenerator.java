package IA.State;

import java.util.ArrayList;
import java.util.List;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class StateGenerator implements SuccessorFunction {

    public List<Successor> getSuccessors(Object state) {
        ArrayList<Successor> llistaSuccessors = new ArrayList<Successor>();

        State board = (State) state;

        // Usefull variable
        float totalCost = board.getTotalCost();
        Sensors sensors = board.getSensors();
        DataCenters dataCenters = board.getDataCenters();
        Node[] sensorCennectedTo = board.getSensorConnectedTo();
        int[] sensorReceivingVolume = board.getSensorReceivingVolume();

        // TODO add newBoard, this is an example of adding one newBoard
        State newBoard = new State(board);
        Node src = sensors.get(0);
        Node dst = sensors.get(1);
        newBoard.addEdge(src, dst);
        llistaSuccessors.add( new Successor("msg", newBoard));

        return llistaSuccessors;
    }
}
