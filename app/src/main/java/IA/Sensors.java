package IA; 

import java.util.ArrayList;
import java.util.Random;

public class Sensors extends ArrayList<Sensor> {

    public Sensors(int nsens, int seed) {
        Random rand = new Random(seed);

        for (int i = 0; i < nsens; i++) {
            float capacity = getRandomCapacity(rand);
            int cx = rand.nextInt(100);
            int cy = rand.nextInt(100);
            
            Sensor sensor = new Sensor(capacity, cx, cy);
            this.add(sensor);
        }
    }

    private int getRandomCapacity(Random rand) {
        int[] capacities = {1, 2, 5};
        return capacities[rand.nextInt(capacities.length)];
    }
}
