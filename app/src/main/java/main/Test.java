package main;

import IA.StateSuccessors;
import IA.HeuristicCost;
import IA.InitialState;
import IA.SolutionSearch;
import IA.StateParser;
import IA.State.State;
import aima.search.framework.HeuristicFunction;
import aima.search.framework.SuccessorFunction;

public class Test {
    public static void provaSA() {
        // TODO
    }

    public static void provaHC(boolean repeatSearch) {
        int nCenters = 4;
        int nSens = 100;
        int centerSeed = 1234;
        int sensorSeed = 4321;

        State state = new State(nCenters, nSens, centerSeed, sensorSeed);
        double bestSolution = Double.POSITIVE_INFINITY;

        do {
            InitialState.inilializeConnections(state);

            // Try out different SuccessorFunctions
            SuccessorFunction operators = new StateSuccessors();

            // TODO: implement some other heuristics
            HeuristicFunction heuristic = new HeuristicCost();

            try {
                SolutionSearch search = new SolutionSearch(state, operators, heuristic);
                search.executeSearch();

                // Print final state
                State finalState = search.getEstatFinal();
                double finalHeuristic = heuristic.getHeuristicValue(finalState);

                if (finalHeuristic < bestSolution) {
                    bestSolution = finalHeuristic;
                    search.getEstatFinal().print();
                    System.out.println("Best Score (To minimize): " + finalHeuristic);
                }

            } catch (Exception e) {
                System.err.println("Nothing happends: " + e.toString());
            }
        } while (repeatSearch);
    }

    public static void printInitialConnection() {
        int ncenters = 4;
        int nsens = 100;
        int centerSeed = 1234;
        int sensorSeed = 4321;

        State state = new State(ncenters, nsens, centerSeed, sensorSeed);
        InitialState.inilializeConnections(state);

        state.print();
    }

    public static void printStateFromFiles() {
        int filesCount = 3;
        for (int i = 0; i < filesCount; i++) {
            printStateFromFile(i);
        }
    }

    public static void printStateFromFile(int i) {
        final String FILE_DIR_PATH = "./src/main/inputs/";
        String filePath = FILE_DIR_PATH + "input" + i + ".ini";
        State state = StateParser.fromFile(filePath);
        state.print();
    }
}
