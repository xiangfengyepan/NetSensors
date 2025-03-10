package IA.State;

import java.util.ArrayList;
import java.util.Random;

public final class DataCenters extends ArrayList<Center> {

    public DataCenters(int nsens, int seed) {
        Random rand = new Random(seed);

        for (int i = 0; i < nsens; i++) {
            int cx = rand.nextInt(100);
            int cy = rand.nextInt(100);

            Center center = new Center(cx, cy);
            this.add(center);
        }
    }
}
