package main;

import IA.StateSuccessors;

import java.util.Random;

import IA.HeuristicCost;
import IA.InitialState;
import IA.SolutionSearch;
import IA.StateParser;
import IA.State.State;
import aima.search.framework.HeuristicFunction;
import aima.search.framework.SuccessorFunction;

public class Test {
    public static void provaHC(int iterations) {
        int nCenters = 4;
        int nSens = 100;
        int centerSeed = 1234; // 0
        int sensorSeed = 4321; // 1234

        State state = new State(nCenters, nSens, centerSeed, sensorSeed);
        double bestSolution = Double.POSITIVE_INFINITY;
        State bestSolutionState = state;

        // Try out different SuccessorFunctions
        SuccessorFunction operators = new StateSuccessors(state.problem);

        double averageHeuristic = 0;
        double averageTime = 0;
        double averageExpandNode = 0;

        for (int i = 0; i < iterations; ++i) {
            InitialState.inilializeConnections(state);

            HeuristicFunction heuristic = new HeuristicCost();

            try {
                SolutionSearch search = new SolutionSearch(state, operators, heuristic);
                search.executeSearch();
                System.out.println(search.getProperties());
                averageTime += search.getTime()/iterations;
                averageExpandNode += Integer.valueOf(search.getNodesexp()) / iterations;


                // Print final state
                State finalState = search.getEstatFinal();
                double finalHeuristic = heuristic.getHeuristicValue(finalState);
                averageHeuristic += finalHeuristic / iterations;

                if (finalHeuristic < bestSolution) {
                    bestSolution = finalHeuristic;
                    bestSolutionState = finalState;

                    // finalState.print();
                    System.out.println("Cost: " + finalState.totalCost());
                    System.out.println("Volume: " + finalState.totalVolume());
                    System.out.println("Best Score (To minimize): " + finalHeuristic);
                }

            } catch (Exception e) {
                System.err.println("Nothing happends: " + e.toString());
            }
        }

        // bestSolutionState.print();
        System.out.println("Avg Score: " + averageHeuristic);
        System.out.println("Avg Time: " + averageTime + " ms");
        System.out.println("Avg Expanded Node: " + averageExpandNode);


        System.out.println("Best Score (To minimize): " + bestSolution);
    }

    
    public static void provaSA(int iterations) {
        int nCenters = 4;
        int nSens = 100;
        int centerSeed = 1234; // 0
        int sensorSeed = 4321; // 1234

        State state = new State(nCenters, nSens, centerSeed, sensorSeed);
        double bestSolution = Double.POSITIVE_INFINITY;
        State bestSolutionState = state;

        // Try out different SuccessorFunctions
        SuccessorFunction operators = new StateSuccessors(state.problem);

        // TODO change parameters
        int iterationSA = 1000;
        int stopIteration = 1;
        int k = 1;
        double lambda = 0.005;
        // for i in iterationSA
        // if (temp < stopIteration) temp = k
        // else temp = k*e^(-lambda*t)  

        double averageHeuristic = 0;
        for (int i = 0; i < iterations; ++i) {
            InitialState.inilializeConnections(state);

            HeuristicFunction heuristic = new HeuristicCost();

            try {
                SolutionSearch search = new SolutionSearch(state, operators, heuristic, iterationSA, stopIteration, k, lambda);
                search.executeSearch();

                // Print final state
                State finalState = search.getEstatFinal();
                double finalHeuristic = heuristic.getHeuristicValue(finalState);
                averageHeuristic += finalHeuristic / iterations;

                if (finalHeuristic < bestSolution) {
                    bestSolution = finalHeuristic;
                    bestSolutionState = finalState;

                    // finalState.print();
                    System.out.println("Cost: " + finalState.totalCost());
                    System.out.println("Volume: " + finalState.totalVolume());
                    System.out.println("Best Score (To minimize): " + finalHeuristic);
                }

            } catch (Exception e) {
                System.err.println("Nothing happends: " + e.toString());
            }
        }

        bestSolutionState.print();
        System.out.println("Avg Score: " + averageHeuristic);
        System.out.println("Best Score (To minimize): " + bestSolution);
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
