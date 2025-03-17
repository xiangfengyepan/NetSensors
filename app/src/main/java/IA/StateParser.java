package IA;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import IA.State.State;

public class StateParser {
    public static State fromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            String[] firstLine = br.readLine().trim().split(" ");
            int nCenters = Integer.valueOf(firstLine[0]);
            int nSens = Integer.valueOf(firstLine[1]);
            int centerSeed = Integer.valueOf(firstLine[2]);
            int sensorSeed = Integer.valueOf(firstLine[3]);

            // reinicialize the State
            State state = new State(nCenters, nSens, centerSeed, sensorSeed);

            for (int srcIndex = 0; srcIndex < nSens; srcIndex++) {
                line = br.readLine().trim();
                if (line.isEmpty() || line.startsWith("#"))
                    continue;

                // we assume that the input is correct so we shoul not have index out of bounce
                Character type = line.charAt(0);
                int id = Integer.valueOf(line.substring(1));

                // TODO: Handle UnableToConnect
                if (type == 'c') {
                    state.connectToCenter(srcIndex, id);
                } else if (type == 's') {
                    state.connectToSensor(srcIndex, id);
                }
            }

            return state;

        } catch (IOException e) {
            System.err.println("Current directory: " + System.getProperty("user.dir"));
            System.err.println("Unable to read the file: " + e.getMessage());
            throw new Error(":(");
        }
    }
}
