package IA;

import IA.State.State;
import aima.search.framework.HeuristicFunction;

public class HeuristicCost implements HeuristicFunction {
	public double getHeuristicValue(Object objectState) {
		State state = (State) objectState;
		return state.totalCost();
	}
}
