package IA.State;

import IA.State.ProblemParameters.Center;
import IA.State.ProblemParameters.Node;
import IA.State.ProblemParameters.ProblemParameters;
import IA.State.ProblemParameters.Sensor;

public class Graph {
    public final ProblemParameters problem;

    private final short[] sensorsDst;
    private final short[] sensorsSendingVolume;

    private final byte[] receivingSensorCount;

    private final short[] centersVolume;
    int totalCost;

    public Graph(Graph state) {
        problem = state.problem;

        sensorsDst = state.sensorsDst.clone();
        sensorsSendingVolume = state.sensorsSendingVolume.clone();
        centersVolume = state.centersVolume.clone();
        receivingSensorCount = state.receivingSensorCount.clone();
        totalCost = state.totalCost;
    }

    public Graph(ProblemParameters problem) {
        this.problem = problem;

        sensorsDst = new short[problem.sensorsCount()];
        sensorsSendingVolume = new short[problem.sensorsCount()];
        receivingSensorCount = new byte[problem.centersCount() + problem.sensorsCount()];
        totalCost = 0;
        centersVolume = new short[problem.centersCount()];

        // Initialize with non zero values.
        // This initialization does not check all the problem restrictions.
        // For correct initializations, use a InitialState class.
        for (int i = 0; i < problem.sensorsCount(); ++i) {
            int center = problem.sensor(i).nearestCenters()[0];

            sensorsDst[i] = (short) (-center - 1);
            sensorsSendingVolume[i] = (short) problem.sensor(i).maxCaptureVolume();

            int sqDist = problem.sensor(i).sqDistanceTo(problem.center(center));
            totalCost += sqDist * sensorsSendingVolume[i];
            centersVolume[center] += sensorsSendingVolume[i];

            receivingSensorCount[center] += 1;
        }
    }

    public void copy(Graph graph) {
        assert problem == graph.problem;
        System.arraycopy(graph.sensorsDst, 0, sensorsDst, 0, sensorsDst.length);
        System.arraycopy(graph.sensorsSendingVolume, 0, sensorsSendingVolume, 0, sensorsSendingVolume.length);
        System.arraycopy(graph.receivingSensorCount, 0, receivingSensorCount, 0, receivingSensorCount.length);
        System.arraycopy(graph.centersVolume, 0, centersVolume, 0, centersVolume.length);
        totalCost = graph.totalCost;
    }

    public int sensorsCount() {
        return sensorsDst.length;
    }

    public int centersCount() {
        return problem.centersCount();
    }

    public int totalCost() {
        return totalCost;
    }

    public int totalVolume() {
        int totalVolume = 0;
        for (int volume : centersVolume)
            totalVolume += Math.min(volume, Center.MAX_DATA_VOLUME);
        return totalVolume;
    }

    // public SensorNode sensorById(int nodeId) {
    // return new SensorNode(nodeId);
    // }

    // public CenterNode centerById(int centerId) {
    // return new CenterNode(centerId);
    // }

    // public final class SensorNode {
    // private final int id;

    // private SensorNode(int id) {
    // assert 0 <= id && id < sensorsDst.length;
    // this.id = id;
    // }

    // public int id() {
    // return id;
    // }

    public int connectionSqDistance(int sensorId) {
        if (isConnectedToDataCenter(sensorId))
            return sensor(sensorId).sqDistanceTo(problem.center(centerId(sensorId)));
        else
            return sensor(sensorId).sqDistanceTo(problem.sensor(dstId(sensorId)));
    }

    public Node dstNode(int sensorId) {
        if (isConnectedToDataCenter(sensorId))
            return problem.center(centerId(sensorId));
        else
            return problem.sensor(dstId(sensorId));
    }

    public int receivingSensorCount(int sensorId) {
        return receivingSensorCount[sensorId + centersCount()];
    }

    public int connectionCost(int sensorId) {
        return connectionSqDistance(sensorId) * limitedSendingVolume(sensorId);
    }

    public Sensor sensor(int sensorId) {
        return problem.sensor(sensorId);
    }

    /**
     * @return true if it's connected directly to a center
     */
    public boolean isConnectedToDataCenter(int sensorId) {
        return sensorsDst[sensorId] < 0;
    }

    public int dstId(int sensorId) {
        return sensorsDst[sensorId];
    }

    public int centerId(int sensorId) {
        return -1 - sensorsDst[sensorId];
    }

    public int sendingVolume(int sensorId) {
        return sensorsSendingVolume[sensorId];
    }

    public int limitedSendingVolume(int sensorId) {
        return Math.min(sendingVolume(sensorId), problem.sensor(sensorId).maxTransmition());
    }

    public void setSendingVolume(int sensorId, int amount) {
        if (isConnectedToDataCenter(sensorId))
            setCenterVolume(centerId(sensorId), centerVolume(centerId(sensorId)) - limitedSendingVolume(sensorId));
        totalCost -= connectionCost(sensorId);

        sensorsSendingVolume[sensorId] = (short) amount;

        if (isConnectedToDataCenter(sensorId))
            setCenterVolume(centerId(sensorId), centerVolume(centerId(sensorId)) + limitedSendingVolume(sensorId));
        totalCost += connectionCost(sensorId);
    }

    protected void connectToSensorId(int sensorId, int sensorDstId) {
        if (isConnectedToDataCenter(sensorId)) {
            setCenterVolume(centerId(sensorId), centerVolume(centerId(sensorId)) - limitedSendingVolume(sensorId));
            receivingSensorCount[centerId(sensorId)] -= 1;
        } else
            receivingSensorCount[dstId(sensorId) + centersCount()] -= 1;
        totalCost -= connectionCost(sensorId);

        sensorsDst[sensorId] = (short) sensorDstId;

        if (isConnectedToDataCenter(sensorId)) {
            setCenterVolume(centerId(sensorId), centerVolume(centerId(sensorId)) + limitedSendingVolume(sensorId));
            receivingSensorCount[centerId(sensorId)] += 1;
        } else
            receivingSensorCount[dstId(sensorId) + centersCount()] += 1;
        totalCost += connectionCost(sensorId);
    }

    protected void connectToCenterId(int sensorId, int centerId) {
        if (isConnectedToDataCenter(sensorId)) {
            setCenterVolume(centerId(sensorId), centerVolume(centerId(sensorId)) - limitedSendingVolume(sensorId));
            receivingSensorCount[centerId(sensorId)] -= 1;
        } else
            receivingSensorCount[dstId(sensorId) + centersCount()] -= 1;
        totalCost -= connectionCost(sensorId);

        sensorsDst[sensorId] = (short) (-centerId - 1);

        if (isConnectedToDataCenter(sensorId)) {
            setCenterVolume(centerId(sensorId), centerVolume(centerId(sensorId)) + limitedSendingVolume(sensorId));
            receivingSensorCount[centerId(sensorId)] += 1;
        } else
            receivingSensorCount[dstId(sensorId) + centersCount()] += 1;
        totalCost += connectionCost(sensorId);
    }

    public int centerReceivingSensorCount(int centerId) {
        return receivingSensorCount[centerId];
    }

    public int centerVolume(int centerId) {
        return centersVolume[centerId];
    }

    private void setCenterVolume(int centerId, int volume) {
        centersVolume[centerId] = (short) volume;
    }

    public void print() {
        problem.print();

        for (int sensorId = 0; sensorId < sensorsCount(); ++sensorId) {
            Node dst;
            Sensor sensor = problem.sensor(sensorId);

            if (isConnectedToDataCenter(sensorId))
                dst = problem.center(centerId(sensorId));
            else
                dst = problem.sensor(dstId(sensorId));

            System.out.printf("Edge: distance: %.2f", sensor.distanceTo(dst));
            System.out.print(" transmition: " + limitedSendingVolume(sensorId));
            System.out.print(" cost: " + connectionCost(sensorId));
            System.out.print("\t\t");
            System.out.println(" " + sensor + " -> " + dst);
        }

        System.out.println("Cost: " + totalCost());
        System.out.println("Volumne: " + totalVolume() + " Mbits");
    }
}
