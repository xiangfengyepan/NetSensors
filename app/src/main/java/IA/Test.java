package IA;

import IA.State.State;

public class Test {
    private int seed;
    private int ncenters;
    private int nsens;

    public Test() {
        this.ncenters = 2;
        this.nsens = 10;
        this.seed = 1;
    }

    public void run(String filePath) {
        State board = new State(ncenters, nsens, seed);
   
        if (filePath.isBlank())
            board.iniSolution();
        else 
            board.readIniSolution(filePath);

        board.print();
    }
}
