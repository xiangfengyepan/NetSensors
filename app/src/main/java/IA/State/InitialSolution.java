package IA.State;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class InitialSolution {
    private State state;
    private DataCenters dataCenters;
    private Sensors sensors;

    public InitialSolution(State state) {
        this.state = state;
        this.dataCenters = state.getDataCenters();
        this.sensors = state.getSensors(); 
    }

    // Connect to nearest center (Example)
    public void iniSolution_0() {
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
            assert state.canConnectSensor(sensorIndex, neardestDataCenter);
            state.connectSensor(sensorIndex, neardestDataCenter);
        }
    }

     public void readIniSolution(String filePath) {
        System.out.println("Directorio actual: " + System.getProperty("user.dir"));
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            line = br.readLine().trim();
            int ncenters = Integer.valueOf(line.split(" ")[0]);
            int nsens = Integer.valueOf(line.split(" ")[1]);
            int centerSeed = Integer.valueOf(line.split(" ")[2]);
            int sensorSeed = Integer.valueOf(line.split(" ")[3]);

            // reinicialize the State
            state.generateState(ncenters, nsens, centerSeed, sensorSeed);
            this.dataCenters = state.getDataCenters();
            this.sensors = state.getSensors(); 

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
                assert state.canConnectSensor(srcIndex, dst);
                state.connectSensor(srcIndex, dst);
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    }
}
