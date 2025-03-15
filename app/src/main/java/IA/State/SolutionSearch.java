package IA.State;

import aima.search.framework.*;
import aima.search.informed.SimulatedAnnealingSearch;
import aima.search.informed.HillClimbingSearch;

import java.util.List;
import java.util.Iterator;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public class SolutionSearch {

    public final static int HILL_CLIMBING = 0;
    public final static int SIMULATED_ANNEALING = 1;
    public final static int HC = HILL_CLIMBING;
    public final static int SA = SIMULATED_ANNEALING;

    private Problem problem;
    private Search search;
    private SearchAgent agent;
    private String properties;
    private String actions;

    private String nodesexp;
    private long time;

    /** Creates a new instance of ConnectatCercador */
    public SolutionSearch(State state, SuccessorFunction operators, HeuristicFunction heuristic) {
        problem = new Problem(state, operators, new FinalState(), heuristic);
        search = new HillClimbingSearch();
    }

    public SolutionSearch(State state, SuccessorFunction operators, HeuristicFunction heuristic, int iteration,
            int stepIteration, int k, double lambda) {
        problem = new Problem(state, operators, new FinalState(), heuristic);
        search = new SimulatedAnnealingSearch(iteration, stepIteration, k, lambda);
    }

    public void executeSearch() {

        try {
            Date d1, d2;
            Calendar a, b;

            d1 = new Date();
            agent = new SearchAgent(problem, search);
            d2 = new Date();

            a = Calendar.getInstance();
            b = Calendar.getInstance();
            a.setTime(d1);
            b.setTime(d2);

            time = b.getTimeInMillis() - a.getTimeInMillis();

            System.out.println(time + " ms");

            // THIS are extra
            setInstrumentation(agent.getInstrumentation());
            setAccions(agent.getActions());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public State getEstatFinal() {
        return (State) search.getGoalState();
    }


    // THIS METHODS are extra inspect de search actions and properties
    private void setInstrumentation(Properties properties) {
        this.properties = new String();
        this.properties += "Search Time: " + time + " ms\n";
        Iterator<Object> keys = properties.keySet().iterator();
        if (keys.hasNext()) {
            String key = (String) keys.next();
            String property = properties.getProperty(key);
            this.properties += "Expanded Nodes: " + property + "\n";
            nodesexp = property;
        }
    }

    public String getProperties() {
        return properties;
    }

    private void setAccions(List<String> actionList) {
        this.actions = new String();
        for (int i = 0; i < actionList.size(); i++) {
            String action = (String) actionList.get(i);
            this.actions += action + "\n";
        }
    }

    public String getAccions() {
        return actions;
    }

    public String getNodesexp() {
        return nodesexp;
    }

    public void fitxerResultats() {
        // TODO if we want the result in a file
    }

}
