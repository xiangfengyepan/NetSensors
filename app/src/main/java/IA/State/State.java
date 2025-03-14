package IA.State;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public final class State {
    private DataCenters dataCenters;
    private Sensors sensors;

    private Node[] sensorConnectedTo;
    private int[] sensorReceivingVolume;
    private float totalCost;

    public State(State state) {
        this.dataCenters = state.dataCenters;
        this.sensors = state.sensors;

        this.sensorConnectedTo = state.sensorConnectedTo.clone();
        this.sensorReceivingVolume = state.sensorReceivingVolume.clone();
        this.totalCost = state.totalCost;
    }

    public State(int ncenters, int nsens, int seed) {
        this.dataCenters = new DataCenters(ncenters, seed);
        this.sensors = new Sensors(nsens, seed);
        this.sensorConnectedTo = new Node[sensors.size()];
        this.sensorReceivingVolume = new int[sensors.size()];

        this.totalCost = 0.f;
    }

    public void readIniSolution(String filePath) {
        System.out.println("Directorio actual: " + System.getProperty("user.dir"));
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            line = br.readLine().trim();
            int ncenters = Integer.valueOf(line.split(" ")[0]);
            int nsens = Integer.valueOf(line.split(" ")[1]);
            int seed = Integer.valueOf(line.split(" ")[2]);

            // reinicialize the State
            this.dataCenters = new DataCenters(ncenters, seed);
            this.sensors = new Sensors(nsens, seed);
            this.sensorConnectedTo = new Node[sensors.size()];
            this.sensorReceivingVolume = new int[sensors.size()];
            this.totalCost = 0.f;

            for (int srcIndex = 0; srcIndex < sensors.size(); srcIndex++) {
                line = br.readLine().trim();
                if (line.isEmpty() || line.startsWith("#"))
                    continue;

                // we assume that the input is correct so we shoul not have index out of bounce
                Character type = line.charAt(0);
                String index = line.substring(1);
                Node dst = sensors.get(Integer.valueOf(index));
                if (type == 'c')
                    dst = dataCenters.get(Integer.valueOf(index));
                else if (type == 's')
                    dst = sensors.get(Integer.valueOf(index));

                // This could happen if the center has already the max number of connections
                assert canConnectSensor(srcIndex, dst);
                connectSensor(srcIndex, dst);
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
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
        // TODO check if ok
        int grade = (int) Arrays.stream(sensorConnectedTo).filter(dstAux -> dstAux == dst).count();

        if (dst.isCenter() && grade > Center.MAX_CONNECTIONS)
            return false;
        else if (!dst.isCenter() && grade + 1 > Sensor.MAX_CONNECTIONS)
            return false;

        // Check if it creates a cycle.
        // We iterate from dst until we reach an end, or src.
        // TODO check if ok
        Node act = dst;
        while (act != null && !act.isCenter() && act != src)
            act = sensorConnectedTo[sensors.indexOf(act)];
        System.out.println(act.isCenter() || act == null);
        return act.isCenter() || act == null;

        // return true;
    }

    private void connectSensor(int sensorIndex, Node dst) {
        Node src = sensors.get(sensorIndex);
        // Update distance cost
        // TODO check if ok (its wrong, it complicate to do incremental)
        int volume = ((Sensor) src).getRealSendingVolumne(dst, sensorReceivingVolume[sensorIndex]);
    
        if (sensorConnectedTo[sensorIndex] != null) {
            // TODO check if ok update sensorReceivingVolume
            Node antDst = sensorConnectedTo[sensorIndex];
            if (!sensorConnectedTo[sensorIndex].isCenter())
            {
                int antVolume = ((Sensor) src).getRealSendingVolumne(antDst, sensorReceivingVolume[sensorIndex]);
                sensorReceivingVolume[sensors.indexOf(antDst)] -= antVolume;
            }
            totalCost -= src.sqDistance(antDst) * volume;
        }
        if (dst != null)
            totalCost += src.sqDistance(dst) * volume;

        // Update volume cost
        // TODO (maybe nothing todo)

        // Update connection state
        sensorConnectedTo[sensorIndex] = dst;

        // Update receiving volume state
        // TODO check if ok

        // iterate from the next sensor until find an end
        Node act = src;
        Node next = sensorConnectedTo[sensorIndex];
        while (next != null && !next.isCenter()) {
            int recivedVolume = sensorReceivingVolume[sensors.indexOf(act)];
            int act2NextVolume = ((Sensor) act).getRealSendingVolumne(next, recivedVolume);
            sensorReceivingVolume[sensors.indexOf(next)] += act2NextVolume;
            
            act = next;
            next = sensorConnectedTo[sensors.indexOf(act)];
        }
    }

    private int getTotalVolume() {
        int totalVolume = 0;

        // Iterate edges that `dst.isCenter()`, and compute sensorSendingVolume
        // TODO check if ok
        for (int i = 0; i < sensorConnectedTo.length; i++) {
            Node dst = sensorConnectedTo[i];
            if (dst != null && dst.isCenter()) {
                Sensor src = (Sensor) sensors.get(i);
                int recivedVolume = sensorReceivingVolume[i];
                totalVolume += src.getRealSendingVolumne(dst, recivedVolume);
                // debug purpose
                int leak = (dst instanceof Center) ? (recivedVolume - Center.MAX_Mbps)
                        : (recivedVolume - ((Sensor) dst).getMaxTransmition());
                if (leak > 0) {
                    System.out.println("There is a leak in " + dst + " of " + leak + " Mbits");
                }

            }
        }

        return totalVolume;
    }

    private int getTotalCostLineal() {
        int totalCost = 0;

        // Iterate edges that `dst.isCenter()`, and compute sensorSendingVolume
        // TODO check if ok
        for (int i = 0; i < sensorConnectedTo.length; i++) {
            Node dst = sensorConnectedTo[i];
            if (dst != null) {
                Sensor src = (Sensor) sensors.get(i);
                int volume = src.getRealSendingVolumne(dst, sensorReceivingVolume[sensors.indexOf(src)]);
                totalCost += src.sqDistance(dst) * volume;
            }
        }

        return totalCost;
    }

    public void print() {
        for (Sensor sensor : sensors)
            System.out.println(sensor);
        for (Center center : dataCenters)
            System.out.println(center);

        for (int sensorIndex = 0; sensorIndex < sensors.size(); ++sensorIndex) {
            Sensor src = sensors.get(sensorIndex);
            Node dst = sensorConnectedTo[sensorIndex];
            if (dst == null)
                continue;

            System.out.print("Edge: distance: " + String.format("%.2f", src.distance(dst)));
            int recivedVolume = sensorReceivingVolume[sensorIndex];
            int volume = src.getRealSendingVolumne(dst, recivedVolume);
      
            System.out.print(" transmition: " + volume);
            System.out.print(" cost: "+ src.sqDistance(dst) * volume);
            

            System.out.print("\t\t");
            System.out.println(" " + src + " -> " + dst);
        }

        System.out.println("Cost: " + totalCost);
        System.out.println("Lineal Cost: " + getTotalCostLineal());

        System.out.println("Volumne: " + getTotalVolume() + " Mbits");
    }

    // Others Methods
    // FinalState
    public boolean isGoalState() {
        return false;
    }

    // Getters
    public DataCenters getDataCenters() {
        return dataCenters;
    }

    public Node[] getSensorConnectedTo() {
        return sensorConnectedTo;
    }

    public int[] getSensorReceivingVolume() {
        return sensorReceivingVolume;
    }

    public Sensors getSensors() {
        return sensors;
    }

    public float getTotalCost() {
        return totalCost;
    }

    // OPERADORS TODO
    public void addEdge(Node src, Node dst) {
        // TODO
    }

    // Heuristic TODO

}
