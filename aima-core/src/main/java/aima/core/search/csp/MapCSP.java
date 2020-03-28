package aima.core.search.csp;


/**
 * Artificial Intelligence A Modern Approach (3rd Ed.): Figure 6.1, Page 204.<br>
 * <br>
 * The principal states and territories of Australia. Coloring this map can be
 * viewed as a constraint satisfaction problem (CSP). The goal is to assign
 * colors to each region so that no neighboring regions have the same color.
 * 
 * @author Ruediger Lunde
 * @author Mike Stampone
 */
public class MapCSP extends CSP {
	public static final Variable NSW = new Variable("NSW");
	public static final Variable NT = new Variable("NT");
	public static final Variable Q = new Variable("Q");
	public static final Variable SA = new Variable("SA");
	public static final Variable T = new Variable("T");
	public static final Variable V = new Variable("V");
	public static final Variable WA = new Variable("WA");
	public static final String RED = "RED";
	public static final String GREEN = "GREEN";
	public static final String BLUE = "BLUE";

	/**
	 * Returns the principle states and territories of Australia as a list of
	 * variables.
	 * 
	 * @return the principle states and territories of Australia as a list of
	 *         variables.
	 */
	 private void collectVariables() {
		addVariable(NSW);
		addVariable(WA);
		addVariable(NT);
		addVariable(Q);
		addVariable(SA);
		addVariable(V);
		addVariable(T);
	}

	/**
	 * Constructs a map CSP for the principal states and territories of
	 * Australia, with the colors Red, Green, and Blue.
	 */
	public MapCSP() {
		collectVariables();

		Domain colors = new Domain(new Object[] { RED, GREEN, BLUE });

		for (Variable var : getVariables())
			setDomain(var, colors);

		addConstraint(new NotEqualConstraint(WA, NT));
		addConstraint(new NotEqualConstraint(WA, SA));
		addConstraint(new NotEqualConstraint(NT, SA));
		addConstraint(new NotEqualConstraint(NT, Q));
		addConstraint(new NotEqualConstraint(SA, Q));
		addConstraint(new NotEqualConstraint(SA, NSW));
		addConstraint(new NotEqualConstraint(SA, V));
		addConstraint(new NotEqualConstraint(Q, NSW));
		addConstraint(new NotEqualConstraint(NSW, V));
	}
}