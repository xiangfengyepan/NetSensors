package IA;

import IA.State.State;
import aima.search.framework.HeuristicFunction;

public class HeuristicCost implements HeuristicFunction {
	public double getHeuristicValue(Object objectState) {
		State state = (State) objectState;
		double cost = state.totalCost();
		double volume = state.totalVolume();

		// Surprisingly, multiplying 8 times is significantly faster than Math.pow(_, 8)
		double volume2 = volume * volume;
		return cost / (volume2 * volume2 * volume2 * volume2);
	}
}
