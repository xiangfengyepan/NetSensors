package IA;

import IA.State.State;

public class Test {
    private int seed;
    private int ncenters;
    private int nsens;

    public Test() {
        this.seed = 1;
        this.ncenters = 2;
        this.nsens = 10;
    }

    public void run() {
        State board = new State(ncenters, nsens, seed);
        board.iniSolution();
        board.print();
    }
}
