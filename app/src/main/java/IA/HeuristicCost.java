package IA;

import IA.State.State;
import aima.search.framework.HeuristicFunction;

public class HeuristicCost implements HeuristicFunction {
	public double getHeuristicValue(Object objectState) {
		State state = (State) objectState;
		double cost = state.totalCost();
		double volume = state.totalVolume();

		// Surprisingly, multiplying 4 times is significantly faster than Math.pow(_, 4)
		return cost / (volume * volume);
	}
}
