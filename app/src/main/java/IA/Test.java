package IA;

import java.util.Random;

import IA.State.Heuristic;
import IA.State.SolutionSearch;
import IA.State.State;
import IA.State.StateSuccessor;
import aima.search.framework.HeuristicFunction;
import aima.search.framework.SuccessorFunction;

public class Test {
    private State state;
    private Random myRandom;

    public void provaSA() {
        // TODO
    }

    public void provaHC() {

        // Variables per a escriurte el fitxer resultat
        int info_einicial, info_heuristic, info_k, info_iter, info_passos_iter;
        boolean[] info_operadors; 
        String info_algoritme;
        double info_lambda;

        SolutionSearch search;
        SuccessorFunction operators = null;
        HeuristicFunction heuristic = null;

        myRandom = new Random();

        // Change to try other ini solutions
        int ncenters = 2;
        int nsens = 10;
        int seed = 1;
        state = new State(ncenters, nsens, seed);

        info_einicial = 0; // Change to try other ini solutions
        state.generateIniSolution(0);


        boolean[] ops = { true, false, false }; // TODO this need to be random to try many operatods
        info_operadors = ops;

        operators = new StateSuccessor(ops);

        heuristic = new Heuristic(); // TODO implement random heuristic

        
        // TODO prints
        // System.err.println("n: " + n + " ncent: " + ncent + " nrep: " + nrep + " heu: " + info_heuristic + " op: "
        //         + info_operadors + " ei: " + (info_einicial + 1));

        try {
            search = new SolutionSearch(state, operators, heuristic);
            search.executeSearch();
            // search.fitxerResultats(); // TODO implement fitxerResultats if we want to ouput in a file
        } catch (Exception e) {
            System.err.println("Nothing happends: " + e.toString());
        }
    }

    public void run(String filePath) {
        int ncenters = 2;
        int nsens = 10;
        int seed = 1;
        State board = new State(ncenters, nsens, seed);

        if (filePath.isBlank())
            board.iniSolution();
        else
            board.readIniSolution(filePath);

        board.print();
    }
}
