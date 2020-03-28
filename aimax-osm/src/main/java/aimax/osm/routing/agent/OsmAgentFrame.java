package aimax.osm.routing.agent;

import aima.gui.applications.search.map.MapAgentFrame;
import aima.gui.applications.search.map.SearchFactory;

/**
 * Simple frame for running map agents in maps defined by OSM data.
 * @author Ruediger Lunde
 */
public class OsmAgentFrame extends MapAgentFrame {

	private static final long serialVersionUID = 1L;

	/** Creates a new frame. */
	public OsmAgentFrame() {
		setTitle("OMAS - the Osm Map Agent Simulator");
		setSelectors(new String[]{
				SCENARIO_SEL, AGENT_SEL,
				SEARCH_SEL, SEARCH_MODE_SEL, HEURISTIC_SEL},
				new String[]{
				"Select Scenario", "Select Agent",
				"Select Search Strategy", "Select Search Mode", "Select Heuristic"}
		);
		setSelectorItems(SCENARIO_SEL,
				new String[] {"Use any way", "Travel by car", "Travel by bicycle"}, 0);
		setSelectorItems(AGENT_SEL,
				new String[] {"Offline Search", "Online Search (LRTA*)"}, 0);
		setSelectorItems(SEARCH_SEL,
				SearchFactory.getInstance().getSearchStrategyNames(), 5);
		setSelectorItems(SEARCH_MODE_SEL, SearchFactory.getInstance()
				.getSearchModeNames(), 1); // change the default!
		setSelectorItems(HEURISTIC_SEL, new String[] { "=0",
				"SLD" }, 1);
		getMessageLogger().setLogLater(true);
	}
}
