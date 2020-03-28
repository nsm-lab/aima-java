package aima.core.environment.map;

import aima.core.search.framework.BidirectionalProblem;
import aima.core.search.framework.DefaultGoalTest;
import aima.core.search.framework.Problem;

/**
 * @author Ciaran O'Reilly
 * 
 */
public class BidirectionalMapProblem extends Problem implements
		BidirectionalProblem {

	Map map;

	Problem reverseProblem;

	public BidirectionalMapProblem(Map map, String initialState,
			String goalState) {
		super(initialState, MapFunctionFactory.getActionsFunction(map),
				MapFunctionFactory.getResultFunction(), new DefaultGoalTest(
						goalState), new MapStepCostFunction(map));

		this.map = map;

		reverseProblem = new Problem(goalState,
				MapFunctionFactory.getActionsFunction(map),
				MapFunctionFactory.getResultFunction(), new DefaultGoalTest(
						initialState), new MapStepCostFunction(map));
	}

	//
	// START Interface BidrectionalProblem
	public Problem getOriginalProblem() {
		return this;
	}

	public Problem getReverseProblem() {
		return reverseProblem;
	}
	// END Interface BirectionalProblem
	//
}
