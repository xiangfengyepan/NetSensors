package IA.State;

import aima.search.framework.GoalTest;

public class FinalState implements GoalTest {

    public boolean isGoalState(Object state) {
        return ((State) (state)).isGoalState();
    }
}
