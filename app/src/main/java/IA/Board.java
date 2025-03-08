package IA;

import java.util.ArrayList;

public class Board {
    private DataCenters dataCenters;
    private Sensors sensors;
    private ArrayList<Edge> edges;

    float totalCost;
    int totalVolumne;

    public Board(int ncenters, int nsens, int seed) {
        this.dataCenters = new DataCenters(ncenters, seed);
        this.sensors = new Sensors(nsens, seed);
        edges = new ArrayList<Edge>();

        totalCost = 0.f;
        totalVolumne = 0;
    }

    public void iniSolution() {
        for (Sensor sensorA : sensors) {
            Center neardestDataCenter = new Center(0, 0);
            float neardestDistance = Float.POSITIVE_INFINITY;
            for (Center center : dataCenters) {
                float distance = getDistance(sensorA.getCx(), sensorA.getCy(), center.getCx(), center.getCy());
                if (distance < neardestDistance)
                {
                    neardestDataCenter = center;
                    neardestDistance = distance;
                }
            }
            int nodeA = sensors.indexOf(sensorA);
            int nodeB = dataCenters.indexOf(neardestDataCenter);
            edges.add(new Edge(nodeA, nodeB));
        }
    }

    private boolean checkEdge(Edge edge)
    {
        Sensor sensorA = sensors.get(edge.getNodeA());
        Center centerB = dataCenters.get(edge.getNodeB());

        if (true){
            //
        }

        return true;
    }

    private void getSolutionCost() {
        // TODO edge index shoud distinguish between sensors and centers
        for (Edge edge : edges) {
            Sensor sensorA = sensors.get(edge.getNodeA());
            Center centerB = dataCenters.get(edge.getNodeB());
            float distance = getDistance(sensorA.getCx(), sensorA.getCy(), centerB.getCx(), centerB.getCy());
            float volumne = sensorA.getCapacity();
            totalCost += calcCost(distance, volumne);
            totalVolumne += volumne;
        }
    }

    private float getDistance(int Acx, int Acy, int Bcx, int Bcy) {
        return (float) Math.sqrt(Math.pow(Math.abs(Acx - Bcx), 2) + Math.pow(Math.abs(Acy - Bcy), 2));
    }

    private float calcCost(float distance, float volumne)
    {
        // TODO maybe need double
        return (float) Math.pow(distance, 2) * volumne;
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

        for (Edge edge: edges)
        {
            // TODO edge index shoud distinguish between sensors and centers
            Sensor sensorA = sensors.get(edge.getNodeA());
            Center centerB = dataCenters.get(edge.getNodeB());
            float distance = getDistance(sensorA.getCx(), sensorA.getCy(), centerB.getCx(), centerB.getCy());
            System.out.println("Edge: distance: " + String.format("%.2f", distance)  + " volumne: " + sensorA.getCapacity() + " " + edge.getNodeA() + "->" + edge.getNodeB());
        }

        getSolutionCost();
        System.out.println("Cost: " + totalCost);
        System.out.println("Volumne: " + totalVolumne + " Mbits");
    }
}
