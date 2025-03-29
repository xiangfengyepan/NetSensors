package main;

import IA.StateSuccessors;
import IA.StateSuccessorsSA;

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
        double averageCenterUsed = 0;
        double averageCost = 0;
        double averageVolume = 0;

        for (int i = 0; i < iterations; ++i) {
            InitialState.inilializeConnections(state);

            HeuristicFunction heuristic = new HeuristicCost();

            double inicialHeuristic = heuristic.getHeuristicValue(state);
            System.out.println("Inicial heuristic (To minimize): " + inicialHeuristic);

            try {
                SolutionSearch search = new SolutionSearch(state, operators, heuristic);
                search.executeSearch();
                System.out.println(search.getProperties());
                averageTime += search.getTime() / iterations;
                averageExpandNode += Integer.valueOf(search.getNodesexp()) / iterations;

                // Print final state
                State finalState = search.getEstatFinal();
                double finalHeuristic = heuristic.getHeuristicValue(finalState);
                averageHeuristic += finalHeuristic / iterations;

                averageCost += finalState.totalCost() / iterations;
                averageVolume += finalState.totalVolume() / iterations;

                System.out.println("Cost: " + finalState.totalCost());
                System.out.println("Volume: " + finalState.totalVolume());

                int centerUsed = 0;
                for (int j = 0; j < finalState.centersCount(); j++) {
                    if (finalState.centerVolume(j) != 0)
                        centerUsed++;
                }
                System.out.println("#Centros Used: " + centerUsed + " / " + finalState.centersCount());
                averageCenterUsed += (double) centerUsed / finalState.centersCount() / iterations;

                if (finalHeuristic < bestSolution) {
                    bestSolution = finalHeuristic;
                    bestSolutionState = finalState;

                    // finalState.print();
                    System.out.println("Best Score (To minimize): " + finalHeuristic);
                }
                System.out.println("\n---------------------------------------------");

            } catch (Exception e) {
                System.err.println("Nothing happends: " + e.toString());
            }
        }

        // bestSolutionState.print();
        System.out.println("Avg Score: " + averageHeuristic);
        System.out.println("Avg Time: " + averageTime + " ms");
        System.out.println("Avg Expanded Node: " + averageExpandNode);
        System.out.println("Avg Center Used: " + averageCenterUsed);
        System.out.println("Avg Cost: " + averageCost);
        System.out.println("Avg Volume: " + averageVolume);
        System.out.println("");
        System.out.println("Best Score (To minimize): " + bestSolution);
        System.out.println("Best Cost: " + bestSolutionState.totalCost());
        System.out.println("Best Volume: " + bestSolutionState.totalVolume());
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
        SuccessorFunction operators = new StateSuccessorsSA(state.problem);

        // TODO change parameters
        int iterationSA = 20000;
        int stepIteration = 200; // baixar la tempratura despres de step iterations
        int k = 20; // factor per temperatura [0-20]
        double lambda = 0.005; // factor de refrigeri en cada step [0.005-0.05]

        double averageHeuristic = 0;
        double averageTime = 0;
        double averageExpandNode = 0;
        double averageCenterUsed = 0;
        double averageCost = 0;
        double averageVolume = 0;

        for (int i = 0; i < iterations; ++i) {
            InitialState.inilializeConnections(state);
            HeuristicFunction heuristic = new HeuristicCost();

            double inicialHeuristic = heuristic.getHeuristicValue(state);
            System.out.println("Inicial heuristic (To minimize): " + inicialHeuristic);

            try {
                SolutionSearch search = new SolutionSearch(state, operators, heuristic, iterationSA, stepIteration, k,
                        lambda);
                search.executeSearch();
                System.out.println(search.getProperties());
                averageTime += search.getTime() / iterations;
                averageExpandNode += Integer.valueOf(search.getNodesexp()) / iterations;

                // Print final state
                State finalState = search.getEstatFinal();
                double finalHeuristic = heuristic.getHeuristicValue(finalState);
                averageHeuristic += finalHeuristic / iterations;

                averageCost += finalState.totalCost() / iterations;
                averageVolume += finalState.totalVolume() / iterations;

                System.out.println("Cost: " + finalState.totalCost());
                System.out.println("Volume: " + finalState.totalVolume());

                int centerUsed = 0;
                for (int j = 0; j < finalState.centersCount(); j++) {
                    if (finalState.centerVolume(j) != 0)
                        centerUsed++;
                }
                System.out.println("#Centros Used: " + centerUsed + " / " + finalState.centersCount());
                averageCenterUsed += centerUsed / finalState.centersCount() / iterations;

                if (finalHeuristic < bestSolution) {
                    bestSolution = finalHeuristic;
                    bestSolutionState = finalState;

                    // finalState.print();
                    System.out.println("Best Score (To minimize): " + finalHeuristic);
                }
                System.out.println("\n---------------------------------------------");

            } catch (Exception e) {
                System.err.println("Nothing happends: " + e.toString());
            }
        }

        // bestSolutionState.print();
        System.out.println("Avg Score: " + averageHeuristic);
        System.out.println("Avg Time: " + averageTime + " ms");
        System.out.println("Avg Expanded Node: " + averageExpandNode);
        System.out.println("Avg Center Used: " + averageCenterUsed);
        System.out.println("Avg Cost: " + averageCost);
        System.out.println("Avg Volume: " + averageVolume);
        System.out.println("");
        System.out.println("Best Score (To minimize): " + bestSolution);
        System.out.println("Best Cost: " + bestSolutionState.totalCost());
        System.out.println("Best Volume: " + bestSolutionState.totalVolume());
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
