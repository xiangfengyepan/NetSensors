package IA.State;

public final class State {
    private DataCenters dataCenters;
    private Sensors sensors;

    private Node[] sensorConnectedTo;
    private int[] sensorReceivingVolume;
    private float totalCost;

    public State(State state) {
        dataCenters = state.dataCenters;
        sensors = state.sensors;

        sensorConnectedTo = state.sensorConnectedTo.clone();
        sensorReceivingVolume = state.sensorReceivingVolume.clone();
        totalCost = state.totalCost;
    }

    public State(int ncenters, int nsens, int seed) {
        dataCenters = new DataCenters(ncenters, seed);
        sensors = new Sensors(nsens, seed);
        sensorConnectedTo = new Node[sensors.size()];
        sensorReceivingVolume = new int[sensors.size()];

        totalCost = 0.f;
    }

    // Connect to nearest center (Example)
    public void iniSolution() {
        for (int sensorIndex = 0; sensorIndex < sensors.size(); ++sensorIndex) {
            Node src = sensors.get(sensorIndex);

            Center neardestDataCenter = null;
            int neardestDistance = Integer.MAX_VALUE;

            for (Center dst : dataCenters) {
                int distance = src.sqDistance(dst);
                if (distance < neardestDistance) {
                    neardestDataCenter = dst;
                    neardestDistance = distance;
                }
            }

            // This could happen if the center has already the max number of connections
            assert canConnectSensor(sensorIndex, neardestDataCenter);

            connectSensor(sensorIndex, neardestDataCenter);
        }
    }

    private boolean canConnectSensor(int sensorIndex, Node dst) {
        if (dst == null)
            return true;

        if (sensorConnectedTo[sensorIndex] == dst)
            return false; // Can't connect if already connected

        Node src = sensors.get(sensorIndex);

        // Check dst connection limit
        // TODO

        // Check if it creates a cycle.
        // We iterate from dst until we reach an end, or src.
        // TODO

        return true;
    }

    private void connectSensor(int sensorIndex, Node dst) {
        Node src = sensors.get(sensorIndex);

        // Update distance cost
        if (sensorConnectedTo[sensorIndex] != null)
            totalCost -= src.sqDistance(sensorConnectedTo[sensorIndex]);
        if (dst != null)
            totalCost += src.sqDistance(dst);

        // Update volume cost
        // TODO

        // Update connection state
        sensorConnectedTo[sensorIndex] = dst;

        // Update receiving volume state
        // TODO
    }

    private int getTotalVolume() {
        int totalVolume = 0;

        // Iterate edges that `dst.isCenter()`, and compute sensorSendingVolume
        // TODO

        return totalVolume;
    }

    public void print() {
        int index = 0;
        for (Sensor sensor : sensors) {
            System.out.println("Sensor: " + index + " Cap=" + sensor.getCapacity() + " X=" + sensor.getCx() + " Y="
                    + sensor.getCy());
            ++index;
        }
        index = 0;
        for (Center center : dataCenters) {
            System.out.println("Center: " + index + " X=" + center.getCx() + " Y=" + center.getCy());
            ++index;
        }

        for (int sensorIndex = 0; sensorIndex < sensors.size(); ++sensorIndex) {
            // TODO edge index shoud distinguish between sensors and centers
            Node src = sensors.get(sensorIndex);
            Node dst = sensorConnectedTo[sensorIndex];
            if (dst == null)
                continue;

            System.out.print("Edge: distance: " + String.format("%.2f", src.distance(dst)));
            System.out.print(" volume: " + sensorReceivingVolume[sensorIndex]);
            System.out.println(" " + src + " -> " + dst);
        }

        System.out.println("Cost: " + totalCost);
        System.out.println("Volumne: " + getTotalVolume() + " Mbits");
    }
}
