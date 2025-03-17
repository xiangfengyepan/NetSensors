package IA;

import IA.State.State;
import aima.search.framework.HeuristicFunction;

public class HeuristicCost implements HeuristicFunction {
	public double getHeuristicValue(Object objectState) {
		State state = (State) objectState;
		double cost = state.totalCost();
		double volume = state.totalVolume();
		return Math.pow(cost, 1. / 8.) / volume;
	}
}
