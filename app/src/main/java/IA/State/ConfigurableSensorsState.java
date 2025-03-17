package IA.State;

import java.util.ArrayList;
import java.util.Arrays;

import IA.State.Graph.CenterNode;
import IA.State.Graph.SensorNode;
import IA.State.ProblemParameters.Center;
import IA.State.ProblemParameters.ProblemParameters;
import IA.State.ProblemParameters.Sensor;

public final class ConfigurableSensorsState {

    private final Graph graph;
    private final boolean[] centersModified;

    public ConfigurableSensorsState(ConfigurableSensorsState state) {
        graph = new Graph(state.graph);
        centersModified = state.centersModified.clone();
    }

    public ConfigurableSensorsState(int nCenters, int nSens, int centerSeed, int sensorSeed) {
        graph = new Graph(nCenters, nSens, centerSeed, sensorSeed);
        centersModified = new boolean[nCenters];
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
        updateDerivedData();
        return graph.totalCost();
    }

    public int totalVolume() {
        updateDerivedData();
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

        // Set volume to 0 so that it get's recalculated
        centersModified[src.centerId()] = true;
        centersModified[dst.centerId()] = true;

        src.connectToDst(dst);
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

        // Set volume to 0 so that it get's recalculated
        centersModified[src.centerId()] = true;
        centersModified[dstCenterId] = true;

        src.connectToCenterId(dstCenterId);
        return ConnectionResult.Successfull;
    }

    /**
     * Will recalculate all the derived data of the nodes connected to the given
     * center.
     */
    private void updateDerivedData() {
        boolean needsRecalculate = false;
        for (boolean centerIsModified : centersModified)
            needsRecalculate = needsRecalculate || centerIsModified;
        if (!needsRecalculate)
            return;

        // Get Sensors that need to be recalculated
        ArrayList<SensorNode> sensorsToRecalculate = new ArrayList<>();
        for (int sensorId = 0; sensorId < graph.sensorsCount(); ++sensorId) {
            SensorNode node = graph.sensorById(sensorId);
            if (centersModified[node.centerId()]) {
                sensorsToRecalculate.add(node);
                node.setSendingVolume(0);
            }
        }

        if (sensorsToRecalculate.isEmpty())
            return;

        int[] distanceToCenter = new int[graph.sensorsCount()];

        // Reset sensor data
        for (SensorNode node : sensorsToRecalculate)
            updateSensorConnections(distanceToCenter, node, node.id());

        // Sort sensors from nearest to a dataCenter
        sensorsToRecalculate.sort((a, b) -> Integer.compare(distanceToCenter[a.id()], distanceToCenter[b.id()]));

        // Update Volume & Cost
        for (SensorNode node : sensorsToRecalculate) {
            CenterNode center = node.center();

            int maxSensorVolume = node.sensor().maxCaptureVolume();
            int maxCenterVolume = Center.MAX_DATA_VOLUME - center.volume();

            sendAdditionalVolume(node, Math.min(maxSensorVolume, maxCenterVolume));
        }

        Arrays.fill(centersModified, false);
    }

    /**
     * Computes the `distanceToCenter` and updates the connections to the centers
     * (with connectToDst).
     * 
     * @param distanceToCenter
     * @param node
     * @param cycleStop
     */
    private void updateSensorConnections(int[] distanceToCenter, SensorNode node, int cycleStop) {
        if (distanceToCenter[node.id()] == 0) {
            if (node.isConnectedToDataCenter()) {
                distanceToCenter[node.id()] = node.connectionSqDistance();
            } else {
                if (node.dstId() == cycleStop) {
                    Sensor from = graph.sensorById(cycleStop).sensor();
                    Sensor to = node.sensor();
                    throw new Error("Detected a cycle in the graph. " + from + " -> ... -> " + to + " -> " + from);
                }

                SensorNode dst = node.dst();
                updateSensorConnections(distanceToCenter, dst, cycleStop);
                node.connectToDst(dst);
                distanceToCenter[node.id()] = distanceToCenter[node.dstId()] + node.connectionSqDistance();
            }
        }
    }

    /**
     * @param node   The node where the data is sended from
     * @param volume The volume of data that we want to send.
     * @return The volume of data that has been able to send.
     */
    private int sendAdditionalVolume(SensorNode node, int volume) {
        // Rule that must be true: maxCaptureVolume >= sendingVolume

        int remainingCapacity = node.sensor().maxCaptureVolume() - node.sendingVolume();
        if (remainingCapacity == 0)
            return 0;

        volume = Math.min(remainingCapacity, volume);

        if (!node.isConnectedToDataCenter())
            volume = sendAdditionalVolume(node.dst(), volume);

        node.setSendingVolume(node.sendingVolume() + volume);
        return volume;
    }

    public void print() {
        graph.print();
        updateDerivedData();
        graph.print();
    }

    public static enum ConnectionResult {
        Successfull,
        AlreadyConnected,
        UnableToConnect,
    }
}
