package IA.State;

import IA.State.Graph.SensorNode;
import IA.State.ProblemParameters.Center;
import IA.State.ProblemParameters.ProblemParameters;
import IA.State.ProblemParameters.Sensor;

public final class State {

    private final Graph graph;

    public State(State state) {
        graph = new Graph(state.graph);
    }

    public State(int nCenters, int nSens, int centerSeed, int sensorSeed) {
        graph = new Graph(nCenters, nSens, centerSeed, sensorSeed);
    }

    public ProblemParameters problem() {
        return graph.problem();
    }

    public int centersCount() {
        return graph.centersCount();
    }

    public int sensorsCount() {
        return graph.sensorsCount();
    }

    public int totalCost() {
        return graph.totalCost();
    }

    public int totalVolume() {
        return graph.totalVolume();
    }

    public ConnectionResult connectToSensor(int srcSensorId, int dstSensorId) {
        if (srcSensorId == dstSensorId)
            return ConnectionResult.UnableToConnect;

        SensorNode src = graph.sensorById(srcSensorId);
        SensorNode dst = graph.sensorById(dstSensorId);

        if (src.dstId() == dst.id())
            return ConnectionResult.AlreadyConnected;

        // Check if it creates a cycle
        SensorNode node = dst;
        while (!node.isConnectedToDataCenter()) {
            node = node.dst();
            if (node.id() == srcSensorId)
                return ConnectionResult.UnableToConnect;
        }

        // Check MAX_CONNECTIONS
        int currentConnections = 0;
        for (int id = 0; id < sensorsCount(); ++id) {
            if (graph.sensorById(id).dstId() == dstSensorId)
                ++currentConnections;
        }
        if (currentConnections + 1 > Sensor.MAX_CONNECTIONS)
            return ConnectionResult.UnableToConnect;

        // ------- All checks passed -------
        // ------- Updateing State -------

        if (!src.isConnectedToDataCenter())
            sendAdditionalVolume(src.dst(), -src.sendingVolume());
        src.connectToDst(dst);
        sendAdditionalVolume(src.dst(), src.sendingVolume());

        return ConnectionResult.Successfull;
    }

    public ConnectionResult connectToCenter(int srcSensorId, int dstCenterId) {
        SensorNode src = graph.sensorById(srcSensorId);

        if (src.isConnectedToDataCenter() && src.centerId() == dstCenterId)
            return ConnectionResult.AlreadyConnected;

        // Check MAX_CONNECTIONS
        int currentConnections = 0;
        for (int id = 0; id < sensorsCount(); ++id) {
            SensorNode sensor = graph.sensorById(id);
            if (sensor.isConnectedToDataCenter() && sensor.centerId() == dstCenterId)
                ++currentConnections;
        }
        if (currentConnections + 1 > Center.MAX_CONNECTIONS)
            return ConnectionResult.UnableToConnect;

        // ------- All checks passed -------
        // ------- Updateing State -------

        if (!src.isConnectedToDataCenter())
            sendAdditionalVolume(src.dst(), -src.sendingVolume());
        src.connectToCenterId(dstCenterId);

        return ConnectionResult.Successfull;
    }

    /**
     * Propagates the additional volume until the center
     * 
     * @param node   The node where the data is sended from
     * @param volume The volume of data that we want to send.
     */
    private void sendAdditionalVolume(SensorNode node, int volume) {
        node.setSendingVolume(node.sendingVolume() + volume);

        if (!node.isConnectedToDataCenter())
            sendAdditionalVolume(node.dst(), volume);
    }

    public void print() {
        graph.print();
    }

    public static enum ConnectionResult {
        Successfull,
        AlreadyConnected,
        UnableToConnect,
    }
}
