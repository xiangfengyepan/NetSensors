package IA.State;

import IA.State.ProblemParameters.Center;
import IA.State.ProblemParameters.Node;
import IA.State.ProblemParameters.ProblemParameters;
import IA.State.ProblemParameters.Sensor;

public final class Graph {
    private final ProblemParameters problem;

    // 0..nodesDst.length => index to `sensors`
    // -1 => connected to dataCenter
    private final short[] sensorsDst;
    private final short[] sensorsCenter;
    private final short[] sensorsSendingVolume;

    private final int[] centersCost;
    private final short[] centersVolume;

    Graph(Graph state) {
        problem = state.problem;

        sensorsDst = state.sensorsDst.clone();
        sensorsCenter = state.sensorsCenter.clone();
        sensorsSendingVolume = state.sensorsSendingVolume.clone();
        centersCost = state.centersCost.clone();
        centersVolume = state.centersVolume.clone();
    }

    public Graph(int nCenters, int nSens, int centerSeed, int sensorSeed) {
        problem = new ProblemParameters(nCenters, nSens, centerSeed, sensorSeed);

        sensorsDst = new short[nSens];
        sensorsCenter = new short[nSens];
        sensorsSendingVolume = new short[nSens];
        centersCost = new int[nCenters];
        centersVolume = new short[nCenters];

        // Initialize with non zero values.
        // This initialization does not check all the problem restrictions.
        // For correct initializations, use a InitialState class.
        for (int i = 0; i < nSens; ++i) {
            sensorsDst[i] = -1;
            sensorsCenter[i] = (short) problem.sensor(i).nearestCenters()[0];
            sensorsSendingVolume[i] = (short) problem.sensor(i).maxCaptureVolume();

            int sqDist = problem.sensor(i).sqDistanceTo(problem.center(sensorsCenter[i]));
            centersCost[sensorsCenter[i]] += sqDist * sensorsSendingVolume[i];
            centersVolume[sensorsCenter[i]] += sensorsSendingVolume[i];
        }
    }

    public ProblemParameters problem() {
        return problem;
    }

    public int sensorsCount() {
        return sensorsDst.length;
    }

    public int centersCount() {
        return problem.centersCount();
    }

    public int totalCost() {
        int totalCost = 0;
        for (int cost : centersCost)
            totalCost += cost;
        return totalCost;
    }

    public int totalVolume() {
        int totalVolume = 0;
        for (int volume : centersVolume)
            totalVolume += Math.min(volume, Center.MAX_DATA_VOLUME);
        return totalVolume;
    }

    public SensorNode sensorById(int nodeId) {
        return new SensorNode(nodeId);
    }

    public CenterNode centerById(int centerId) {
        return new CenterNode(centerId);
    }

    public final class SensorNode {
        private int id;

        private SensorNode(int id) {
            assert 0 <= id && id < sensorsDst.length;
            this.id = id;
        }

        public int id() {
            return id;
        }

        public int connectionSqDistance() {
            if (isConnectedToDataCenter())
                return sensor().sqDistanceTo(center().center());
            else
                return sensor().sqDistanceTo(dst().sensor());
        }

        public int connectionCost() {
            return connectionSqDistance() * limitedSendingVolume();
        }

        public Sensor sensor() {
            return problem.sensor(id);
        }

        /**
         * @return true if it's connected directly to a center
         */
        public boolean isConnectedToDataCenter() {
            return sensorsDst[id] == -1;
        }

        public int dstId() {
            return sensorsDst[id];
        }

        public SensorNode dst() {
            return new SensorNode(sensorsDst[id]);
        }

        public int centerId() {
            return sensorsCenter[id];
        }

        public CenterNode center() {
            return new CenterNode(sensorsCenter[id]);
        }

        public int sendingVolume() {
            return sensorsSendingVolume[id];
        }

        public int limitedSendingVolume() {
            return Math.min(sendingVolume(), sensor().maxTransmition());
        }

        public void setSendingVolume(int amount) {
            if (isConnectedToDataCenter())
                center().setVolume(center().volume() - limitedSendingVolume());
            center().setCost(center().cost() - connectionCost());

            sensorsSendingVolume[id] = (short) amount;

            if (isConnectedToDataCenter())
                center().setVolume(center().volume() + limitedSendingVolume());
            center().setCost(center().cost() + connectionCost());
        }

        public void connectToDst(SensorNode node) {
            if (isConnectedToDataCenter())
                center().setVolume(center().volume() - limitedSendingVolume());
            center().setCost(center().cost() - connectionCost());

            sensorsDst[id] = (short) node.id;
            sensorsCenter[id] = (short) node.centerId();

            if (isConnectedToDataCenter())
                center().setVolume(center().volume() + limitedSendingVolume());
            center().setCost(center().cost() + connectionCost());
        }

        public void connectToCenterId(int centerId) {
            if (isConnectedToDataCenter())
                center().setVolume(center().volume() - limitedSendingVolume());
            center().setCost(center().cost() - connectionCost());

            sensorsDst[id] = -1;
            sensorsCenter[id] = (short) centerId;

            if (isConnectedToDataCenter())
                center().setVolume(center().volume() + limitedSendingVolume());
            center().setCost(center().cost() + connectionCost());
        }
    }

    public final class CenterNode {
        private int id;

        private CenterNode(int id) {
            assert 0 <= id && id < centersCount();
            this.id = id;
        }

        public int id() {
            return id;
        }

        public Center center() {
            return problem.center(id);
        }

        public int volume() {
            return centersVolume[id];
        }

        private int cost() {
            return centersCost[id];
        }

        private void setCost(int cost) {
            centersCost[id] = cost;
        }

        private void setVolume(int volume) {
            centersVolume[id] = (short) volume;
        }
    }

    public void print() {
        problem.print();

        for (int sensorId = 0; sensorId < sensorsCount(); ++sensorId) {
            SensorNode node = sensorById(sensorId);
            Sensor src = node.sensor();
            Node dst;

            if (node.isConnectedToDataCenter())
                dst = node.center().center();
            else
                dst = node.dst().sensor();

            System.out.printf("Edge: distance: %.2f", src.distanceTo(dst));
            System.out.print(" transmition: " + node.limitedSendingVolume());
            System.out.print(" cost: " + node.connectionCost());
            System.out.print("\t\t");
            System.out.println(" " + src + " -> " + dst);
        }

        System.out.println("Cost: " + totalCost());
        System.out.println("Volumne: " + totalVolume() + " Mbits");
    }
}
