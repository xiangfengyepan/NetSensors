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

        if (dstId(srcSensorId) == dstSensorId)
            return ConnectionResult.AlreadyConnected;

        // Check if it creates a cycle
        int node = dstSensorId;
        while (!isConnectedToDataCenter(node)) {
            node = dstId(node);
            if (node == srcSensorId)
                return ConnectionResult.UnableToConnect;
        }

        // Check MAX_CONNECTIONS
        int currentConnections = receivingSensorCount(dstSensorId);
        if (currentConnections + 1 > Sensor.MAX_CONNECTIONS)
            return ConnectionResult.UnableToConnect;

        // ------- All checks passed -------
        // ------- Updateing State -------

        sendAdditionalVolume(srcSensorId, -sendingVolume(srcSensorId));
        connectToSensorId(srcSensorId, dstSensorId);
        sendAdditionalVolume(srcSensorId, sendingVolume(srcSensorId));

        return ConnectionResult.Successfull;
    }

    public ConnectionResult connectToCenter(int srcSensorId, int dstCenterId) {
        if (isConnectedToDataCenter(srcSensorId) && centerId(srcSensorId) == dstCenterId)
            return ConnectionResult.AlreadyConnected;

        // Check MAX_CONNECTIONS
        int currentConnections = centerReceivingSensorCount(dstCenterId);
        if (currentConnections + 1 > Center.MAX_CONNECTIONS)
            return ConnectionResult.UnableToConnect;

        // ------- All checks passed -------
        // ------- Updateing State -------

        sendAdditionalVolume(srcSensorId, -sendingVolume(srcSensorId));
        connectToCenterId(srcSensorId, dstCenterId);

        return ConnectionResult.Successfull;
    }

    /**
     * Propagates the additional volume until the center
     * 
     * @param node   The node where the data is sended from
     * @param volume The volume of data that we want to send.
     */
    private void sendAdditionalVolume(int sensorId, int volume) {
        while (!isConnectedToDataCenter(sensorId)) {
            sensorId = dstId(sensorId);
            setSendingVolume(sensorId, sendingVolume(sensorId) + volume);
        }
    }

    public static enum ConnectionResult {
        Successfull,
        AlreadyConnected,
        UnableToConnect,
    }
}
