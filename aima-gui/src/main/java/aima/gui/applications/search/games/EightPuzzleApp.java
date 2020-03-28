package aima.gui.applications.search.games;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.swing.JButton;

import aima.core.agent.Action;
import aima.core.agent.Agent;
import aima.core.agent.Environment;
import aima.core.agent.EnvironmentState;
import aima.core.agent.Percept;
import aima.core.agent.impl.AbstractEnvironment;
import aima.core.environment.eightpuzzle.EightPuzzleBoard;
import aima.core.environment.eightpuzzle.EightPuzzleFunctionFactory;
import aima.core.environment.eightpuzzle.EightPuzzleGoalTest;
import aima.core.environment.eightpuzzle.ManhattanHeuristicFunction;
import aima.core.environment.eightpuzzle.MisplacedTilleHeuristicFunction;
import aima.core.search.framework.GraphSearch;
import aima.core.search.framework.Problem;
import aima.core.search.framework.Search;
import aima.core.search.framework.SearchAgent;
import aima.core.search.informed.AStarSearch;
import aima.core.search.informed.GreedyBestFirstSearch;
import aima.core.search.local.SimulatedAnnealingSearch;
import aima.core.search.uninformed.BreadthFirstSearch;
import aima.core.search.uninformed.DepthLimitedSearch;
import aima.core.search.uninformed.IterativeDeepeningSearch;
import aima.core.util.datastructure.XYLocation;
import aima.gui.framework.AgentAppController;
import aima.gui.framework.AgentAppEnvironmentView;
import aima.gui.framework.AgentAppFrame;
import aima.gui.framework.MessageLogger;
import aima.gui.framework.SimpleAgentApp;
import aima.gui.framework.SimulationThread;

/**
 * Graphical 8-puzzle game application. It demonstrates the performance
 * of different search algorithms. Additionally, users can make experiences with
 * human problem solving.
 * 
 * @author Ruediger Lunde
 */
public class EightPuzzleApp extends SimpleAgentApp {

	/** List of supported search algorithm names. */
	protected static List<String> SEARCH_NAMES = new ArrayList<String>();
	/** List of supported search algorithms. */
	protected static List<Search> SEARCH_ALGOS = new ArrayList<Search>();

	/** Adds a new item to the list of supported search algorithms. */
	public static void addSearchAlgorithm(String name, Search algo) {
		SEARCH_NAMES.add(name);
		SEARCH_ALGOS.add(algo);
	}

	static {
		addSearchAlgorithm("Breadth First Search (Graph Search)",
				new BreadthFirstSearch(new GraphSearch()));
		addSearchAlgorithm("Depth Limited Search (9)",
				new DepthLimitedSearch(9));
		addSearchAlgorithm("Iterative Deepening Search",
				new IterativeDeepeningSearch());
		addSearchAlgorithm("Greedy Best First Search (MisplacedTileHeursitic)",
				new GreedyBestFirstSearch(new GraphSearch(),
						new MisplacedTilleHeuristicFunction()));
		addSearchAlgorithm("Greedy Best First Search (ManhattanHeursitic)",
				new GreedyBestFirstSearch(new GraphSearch(),
						new ManhattanHeuristicFunction()));
		addSearchAlgorithm("AStar Search (MisplacedTileHeursitic)",
				new AStarSearch(new GraphSearch(),
						new MisplacedTilleHeuristicFunction()));
		addSearchAlgorithm("AStar Search (ManhattanHeursitic)",
				new AStarSearch(new GraphSearch(),
						new ManhattanHeuristicFunction()));
		addSearchAlgorithm("Simulated Annealing Search",
				new SimulatedAnnealingSearch(new ManhattanHeuristicFunction()));
	}

	/** Returns an <code>EightPuzzleView</code> instance. */
	public AgentAppEnvironmentView createEnvironmentView() {
		return new EightPuzzleView();
	}

	/** Returns a <code>EightPuzzleFrame</code> instance. */
	@Override
	public AgentAppFrame createFrame() {
		return new EightPuzzleFrame();
	}

	/** Returns a <code>EightPuzzleController</code> instance. */
	@Override
	public AgentAppController createController() {
		return new EightPuzzleController();
	}

	// ///////////////////////////////////////////////////////////////
	// main method

	/**
	 * Starts the application.
	 */
	public static void main(String args[]) {
		new EightPuzzleApp().startApplication();
	}

	// ///////////////////////////////////////////////////////////////
	// some inner classes

	/**
	 * Adds some selectors to the base class and adjusts its size.
	 */
	protected static class EightPuzzleFrame extends AgentAppFrame {
		private static final long serialVersionUID = 1L;
		public static String ENV_SEL = "EnvSelection";
		public static String SEARCH_SEL = "SearchSelection";

		public EightPuzzleFrame() {
			setTitle("Eight Puzzle Application");
			setSelectors(new String[] { ENV_SEL, SEARCH_SEL }, new String[] {
					"Select Environment", "Select Search" });
			setSelectorItems(ENV_SEL, new String[] { "Three Moves", "Medium",
					"Extreme", "Random" }, 0);
			setSelectorItems(SEARCH_SEL, (String[]) SEARCH_NAMES
					.toArray(new String[] {}), 0);
			setEnvView(new EightPuzzleView());
			setSize(800, 600);
		}
	}

	/**
	 * Displays the informations provided by a
	 * <code>EightPuzzleEnvironment</code> on a panel using an grid of buttons.
	 * By pressing a button, the user can move the corresponding tile to the
	 * adjacent gap.
	 */
	protected static class EightPuzzleView extends AgentAppEnvironmentView
			implements ActionListener {
		private static final long serialVersionUID = 1L;
		protected JButton[] squareButtons;

		protected EightPuzzleView() {
			setLayout(new GridLayout(3, 3));
			Font f = new java.awt.Font(Font.SANS_SERIF, Font.PLAIN, 32);
			squareButtons = new JButton[9];
			for (int i = 0; i < 9; i++) {
				JButton square = new JButton("");
				square.setFont(f);
				square.addActionListener(this);
				squareButtons[i] = square;
				add(square);
			}
		}

		@Override
		public void setEnvironment(Environment env) {
			super.setEnvironment(env);
			showState();
		}

		/** Agent value null indicates a user initiated action. */
		@Override
		public void agentActed(Agent agent, Action action,
				EnvironmentState resultingState) {
			showState();
			notify((agent == null ? "User: " : "") + action.toString());
		}

		@Override
		public void agentAdded(Agent agent, EnvironmentState resultingState) {
			showState();
		}

		/**
		 * Displays the board state by labeling and coloring the square buttons.
		 */
		protected void showState() {
			int[] vals = ((EightPuzzleEnvironment) env).getBoard().getState();
			for (int i = 0; i < 9; i++) {
				squareButtons[i].setBackground(vals[i] == 0 ? Color.LIGHT_GRAY
						: Color.WHITE);
				squareButtons[i].setText(vals[i] == 0 ? "" : Integer
						.toString(vals[i]));
			}
		}

		/**
		 * When the user presses square buttons the board state is modified
		 * accordingly.
		 */
		@Override
		public void actionPerformed(ActionEvent ae) {
			for (int i = 0; i < 9; i++) {
				if (ae.getSource() == squareButtons[i]) {
					EightPuzzleController contr = (EightPuzzleController) getController();
					XYLocation locGap = ((EightPuzzleEnvironment) env)
							.getBoard().getLocationOf(0);
					if (locGap.getXCoOrdinate() == i / 3) {
						if (locGap.getYCoOrdinate() == i % 3 - 1)
							contr.executeUserAction(EightPuzzleBoard.RIGHT);
						else if (locGap.getYCoOrdinate() == i % 3 + 1)
							contr.executeUserAction(EightPuzzleBoard.LEFT);
					} else if (locGap.getYCoOrdinate() == i % 3) {
						if (locGap.getXCoOrdinate() == i / 3 - 1)
							contr.executeUserAction(EightPuzzleBoard.DOWN);
						else if (locGap.getXCoOrdinate() == i / 3 + 1)
							contr.executeUserAction(EightPuzzleBoard.UP);
					}
				}
			}
		}
	}

	/**
	 * Defines how to react on standard simulation button events.
	 */
	protected static class EightPuzzleController extends AgentAppController {

		protected EightPuzzleEnvironment env = null;
		protected SearchAgent agent = null;
		protected boolean dirty;

		/** Prepares next simulation. */
		@Override
		public void clear() {
			prepare(null);
		}

		/**
		 * Creates an eight puzzle environment and clears the current search
		 * agent.
		 */
		@Override
		public void prepare(String changedSelector) {
			AgentAppFrame.SelectionState selState = frame.getSelection();
			EightPuzzleBoard board = null;
			switch (selState.getIndex(EightPuzzleFrame.ENV_SEL)) {
			case 0: // three moves
				board = new EightPuzzleBoard(new int[] { 1, 2, 5, 3, 4, 0, 6,
						7, 8 });
				break;
			case 1: // medium
				board = new EightPuzzleBoard(new int[] { 1, 4, 2, 7, 5, 8, 3,
						0, 6 });
				break;
			case 2: // extreme
				board = new EightPuzzleBoard(new int[] { 0, 8, 7, 6, 5, 4, 3,
						2, 1 });
				break;
			case 3: // random
				board = new EightPuzzleBoard(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8});
				Random r = new Random(System.currentTimeMillis());
				for (int i = 0; i < 200 ; i++) {
					switch (r.nextInt(4)) {
					case 0: board.moveGapUp(); break;
					case 1: board.moveGapDown(); break;
					case 2: board.moveGapLeft(); break;
					case 3: board.moveGapRight(); break;
					}
				}
			}
			env = new EightPuzzleEnvironment(board);
			agent = null;
			dirty = false;
			frame.getEnvView().setEnvironment(env);
		}

		/**
		 * Creates a new search agent and adds it to the current environment if
		 * necessary.
		 */
		protected void addAgent() throws Exception {
			if (agent == null) {
				int pSel = frame.getSelection().getIndex(
						EightPuzzleFrame.SEARCH_SEL);
				Problem problem = new Problem(env.getBoard(),
						EightPuzzleFunctionFactory.getActionsFunction(),
						EightPuzzleFunctionFactory.getResultFunction(),
						new EightPuzzleGoalTest());
				Search search = SEARCH_ALGOS.get(pSel);
				agent = new SearchAgent(problem, search);
				env.addAgent(agent);
			}
		}

		/** Checks whether simulation can be started. */
		@Override
		public boolean isPrepared() {
			return !dirty && (agent == null || !agent.isDone());
		}

		/** Starts simulation. */
		@Override
		public void run(MessageLogger logger) {
			logger.log("<simulation-log>");
			try {
				addAgent();
				while (!agent.isDone() && !frame.simulationPaused()) {
					Thread.sleep(500);
					env.step();
				}
			} catch (InterruptedException e) {
				// nothing to do...
			} catch (Exception e) {
				e.printStackTrace(); // probably search has failed...
			}
			logger.log(getStatistics());
			logger.log("</simulation-log>\n");
		}

		/** Executes one simulation step. */
		@Override
		public void step(MessageLogger logger) {
			try {
				addAgent();
				env.step();
			} catch (Exception e) {
				e.printStackTrace(); // probably search has failed...
			}
		}

		/** Updates the status of the frame after simulation has finished. */
		public void update(SimulationThread simulationThread) {
			if (simulationThread.isCanceled()) {
				frame.setStatus("Task canceled.");
			} else if (frame.simulationPaused()) {
				frame.setStatus("Task paused.");
			} else {
				frame.setStatus("Task completed.");
			}
		}

		/** Provides a text with statistical information about the last run. */
		private String getStatistics() {
			StringBuffer result = new StringBuffer();
			Properties properties = agent.getInstrumentation();
			Iterator<Object> keys = properties.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				String property = properties.getProperty(key);
				result.append("\n" + key + " : " + property);
			}
			return result.toString();
		}

		public void executeUserAction(Action action) {
			env.executeAction(null, action);
			agent = null;
			dirty = true;
			frame.updateEnabledState();
		}
	}

	/** Simple environment maintaining just the current board state. */
	protected static class EightPuzzleEnvironment extends AbstractEnvironment {
		EightPuzzleBoard board;

		protected EightPuzzleEnvironment(EightPuzzleBoard board) {
			this.board = board;
		}

		protected EightPuzzleBoard getBoard() {
			return board;
		}

		/** Executes the provided action and returns null. */
		@Override
		public EnvironmentState executeAction(Agent agent, Action action) {
			if (action == EightPuzzleBoard.UP)
				board.moveGapUp();
			else if (action == EightPuzzleBoard.DOWN)
				board.moveGapDown();
			else if (action == EightPuzzleBoard.LEFT)
				board.moveGapLeft();
			else if (action == EightPuzzleBoard.RIGHT)
				board.moveGapRight();
			if (agent == null)
				updateEnvironmentViewsAgentActed(agent, action, null);
			return null;
		}

		/** Returns null. */
		@Override
		public EnvironmentState getCurrentState() {
			return null;
		}

		/** Returns null. */
		@Override
		public Percept getPerceptSeenBy(Agent anAgent) {
			return null;
		}
	}
}
