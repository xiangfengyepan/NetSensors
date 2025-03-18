package IA.State;

import IA.State.ProblemParameters.Center;
import IA.State.ProblemParameters.Node;
import IA.State.ProblemParameters.ProblemParameters;
import IA.State.ProblemParameters.Sensor;

public class Graph {
    private final ProblemParameters problem;

    private final short[] sensorsDst;
    private final short[] sensorsSendingVolume;

    private final short[] centersVolume;
    int totalCost;

    public Graph(Graph state) {
        problem = state.problem;

        sensorsDst = state.sensorsDst.clone();
        sensorsSendingVolume = state.sensorsSendingVolume.clone();
        centersVolume = state.centersVolume.clone();
        totalCost = state.totalCost;
    }

    public Graph(int nCenters, int nSens, int centerSeed, int sensorSeed) {
        problem = new ProblemParameters(nCenters, nSens, centerSeed, sensorSeed);

        sensorsDst = new short[nSens];
        sensorsSendingVolume = new short[nSens];
        totalCost = 0;
        centersVolume = new short[nCenters];

        // Initialize with non zero values.
        // This initialization does not check all the problem restrictions.
        // For correct initializations, use a InitialState class.
        for (int i = 0; i < nSens; ++i) {
            int center = problem.sensor(i).nearestCenters()[0];

            sensorsDst[i] = (short) (-center - 1);
            sensorsSendingVolume[i] = (short) problem.sensor(i).maxCaptureVolume();

            int sqDist = problem.sensor(i).sqDistanceTo(problem.center(center));
            totalCost += sqDist * sensorsSendingVolume[i];
            centersVolume[center] += sensorsSendingVolume[i];
        }
    }

    public void copy(Graph graph) {
        assert problem == graph.problem;
        System.arraycopy(graph.sensorsDst, 0, sensorsDst, 0, sensorsDst.length);
        System.arraycopy(graph.sensorsSendingVolume, 0, sensorsSendingVolume, 0, sensorsSendingVolume.length);
        System.arraycopy(graph.centersVolume, 0, centersVolume, 0, centersVolume.length);
        totalCost = graph.totalCost;
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
        private final int id;

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
            return sensorsDst[id] < 0;
        }

        public int dstId() {
            return sensorsDst[id];
        }

        public SensorNode dst() {
            return new SensorNode(sensorsDst[id]);
        }

        public int centerId() {
            return -1 - sensorsDst[id];
        }

        public CenterNode center() {
            return new CenterNode(-1 - sensorsDst[id]);
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
            totalCost -= connectionCost();

            sensorsSendingVolume[id] = (short) amount;

            if (isConnectedToDataCenter())
                center().setVolume(center().volume() + limitedSendingVolume());
            totalCost += connectionCost();
        }

        public void connectToDst(SensorNode node) {
            if (isConnectedToDataCenter())
                center().setVolume(center().volume() - limitedSendingVolume());
            totalCost -= connectionCost();

            sensorsDst[id] = (short) node.id;

            if (isConnectedToDataCenter())
                center().setVolume(center().volume() + limitedSendingVolume());
            totalCost += connectionCost();
        }

        public void connectToCenterId(int centerId) {
            if (isConnectedToDataCenter())
                center().setVolume(center().volume() - limitedSendingVolume());
            totalCost -= connectionCost();

            sensorsDst[id] = (short) (-centerId - 1);

            if (isConnectedToDataCenter())
                center().setVolume(center().volume() + limitedSendingVolume());
            totalCost += connectionCost();
        }
    }

    public final class CenterNode {
        private final int id;

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
