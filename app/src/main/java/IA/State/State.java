package IA.State;

import IA.State.ProblemParameters.Center;
import IA.State.ProblemParameters.ProblemParameters;
import IA.State.ProblemParameters.Sensor;

public final class State extends Graph {

    public State(State state) {
        super(state);
    }

    public State(int nCenters, int nSens, int centerSeed, int sensorSeed) {
        super(new ProblemParameters(nCenters, nSens, centerSeed, sensorSeed));
    }

    public State(ProblemParameters problem) {
        super(problem);
    }

    public ConnectionResult connectToSensor(int srcSensorId, int dstSensorId) {
        if (srcSensorId == dstSensorId)
            return ConnectionResult.UnableToConnect;

        SensorNode src = sensorById(srcSensorId);
        SensorNode dst = sensorById(dstSensorId);

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
        int currentConnections = dst.receivingSensorCount();
        if (currentConnections + 1 > Sensor.MAX_CONNECTIONS)
            return ConnectionResult.UnableToConnect;

        // ------- All checks passed -------
        // ------- Updateing State -------

        sendAdditionalVolume(src, -src.sendingVolume());
        src.connectToDst(dst);
        sendAdditionalVolume(src, src.sendingVolume());

        return ConnectionResult.Successfull;
    }

    public ConnectionResult connectToCenter(int srcSensorId, int dstCenterId) {
        SensorNode src = sensorById(srcSensorId);

        if (src.isConnectedToDataCenter() && src.centerId() == dstCenterId)
            return ConnectionResult.AlreadyConnected;

        // Check MAX_CONNECTIONS
        int currentConnections = centerById(dstCenterId).receivingSensorCount();
        if (currentConnections + 1 > Center.MAX_CONNECTIONS)
            return ConnectionResult.UnableToConnect;

        // ------- All checks passed -------
        // ------- Updateing State -------

        sendAdditionalVolume(src, -src.sendingVolume());
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
        while(!node.isConnectedToDataCenter()) {
            node = node.dst();
            node.setSendingVolume(node.sendingVolume() + volume);
        }
    }

    public static enum ConnectionResult {
        Successfull,
        AlreadyConnected,
        UnableToConnect,
    }
}
