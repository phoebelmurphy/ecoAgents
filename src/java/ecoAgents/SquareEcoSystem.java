package ecoAgents;

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.environment.Environment;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import controller.ResourceManager;
import model.Coordinates;
import model.GridModel;
import model.GridSquareModel;
import model.agents.Agent;
import model.agents.Fox;
import model.agents.Rabbit;

public class SquareEcoSystem extends Environment {

	public static final String loggerName = "env.logger";

	private Logger logger = Logger.getLogger(loggerName);
	private int x, y;
	private GridModel grid;
	private Rabbit rabbits[];

	private Fox fox;

	public void init(String[] args) {
		x = 5;
		y = 5;
		grid = GridModel.getInstance();
		ResourceManager manager = new ResourceManager(grid);
		new Thread(manager, "resourcemanager").start();
		rabbits = new Rabbit[2];
		addRabbits();
		addFox();
		updateRabbitPercepts();
		updateFoxPercepts();

	}

	/**
	 * Add a fox to the grid
	 */
	private void addFox() {
		fox = new Fox("fox1", new Coordinates(1, 2));
		grid.addAgent(fox, 4, 4);
	}

	/**
	 * Add rabbits to the grid.
	 */
	private void addRabbits() {
		for (int i = 0; i < rabbits.length; i++) {
			rabbits[i] = new Rabbit("rabbit" + (i + 1), new Coordinates(i, i));
			grid.addAgent(rabbits[i], i, i);
		}
	}

	public boolean executeAction(String ag, Structure action) {
		logger.logp(Level.INFO, "SquareEcoSystem", "executeAction",
				"executing action " + action.getFunctor() + " for agent " + ag);
		boolean result = false;
		if (action.getFunctor().equals("eat")) {
			result = eatGrass(ag);
		} else if (action.getFunctor().equals("move")) {
			char direction = action.getTerm(0).toString().charAt(0);
			result = move(ag, direction);
		} else if (action.getFunctor().equals("pounce")) {
			String victim = action.getTerm(0).toString();
			result = attack(ag, victim);
		} else if (action.getFunctor().equals("look")) {
			String prey = action.getTerm(0).toString();
			logger.log(Level.INFO, "looking for " + prey);
			result = lookFor(ag, prey);
		} else if (action.getFunctor().equals("moveTowards")) {
			String preyagent = action.getTerm(0).toString();
			result = moveTowards(ag, preyagent);
		} else if (action.getFunctor().equals("eatPrey")) {
			String preyAgent = action.getTerm(0).toString();
			result = eatPrey(ag, preyAgent);
		}
		if (ag.contains("rabbit")) {
			updateRabbitPercepts();
		} else {
			logger.log(Level.INFO, "updating fox percepts");
			updateFoxPercepts();
		}
		return result;
	}

	private boolean eatPrey(String ag, String preyAgent) {
		Agent prey = getAgent(preyAgent);
		GridSquareModel preySquare = grid.getSquare(prey.getCoordinates());
		preySquare.removeAgent(preyAgent);
		return true;
	}

	private boolean moveTowards(String ag, String preyagent) {
		Agent hunter = getAgent(ag);
		Agent prey = getAgent(preyagent);
		if (prey == null) {
			return false;
		}
		Coordinates preyCoordinates = prey.getCoordinates();
		wait(hunter, hunter.getSpeed());
		logger.log(Level.INFO, "moving to " + prey.getCoordinates());
		boolean result = grid.moveTowards(hunter, preyCoordinates);
		return result;
	}

	private boolean lookFor(String ag, String prey) {
		Agent hunter = getAgent(ag);
		wait(fox, fox.getSpeed());
		Agent preyAgent = grid.findClosest(hunter, prey);
		hunter.setPrey(preyAgent);
		return true;
	}

	private void updateFoxPercepts() {
		clearPercepts(fox.getName());
		fox.lock();
		if (fox.getPrey() != null && fox.getPrey().isAlive()) {
			String percept = "visible(rabbit, " + fox.getPrey().getName() + ")";
			addPercept(fox.getName(), Literal.parseLiteral("visible(rabbit)"));
			addPercept(fox.getName(), Literal.parseLiteral(percept));
			logger.log(Level.INFO, "adding percept " + percept);
		}
		if (fox.getPrey() != null
				&& fox.getPrey().getCoordinates()
						.adjacentTo(fox.getCoordinates())
				&& fox.getPrey().isAlive()) {
			addPercept(
					fox.getName(),
					Literal.parseLiteral("canPounce(" + fox.getPrey().getName()
							+ ")"));
		}
		if (fox.getPrey() != null && !fox.getPrey().isAlive()) {
			String percept = "killed(rabbit, " + fox.getPrey().getName() + ")";
			addPercept(fox.getName(), Literal.parseLiteral(percept));
		}
		updatePositionPercept(fox);
		fox.unlock();

	}

	/**
	 * Updates percepts for all rabbits
	 */
	private void updateRabbitPercepts() {
		for (Agent rabbit : rabbits) {
			clearPercepts(rabbit.getName());
		}
		for (int i = 0; i < rabbits.length; i++) {
			updateGrassPercept(rabbits[i]);
			updateSurroundingPercepts(rabbits[i]);
		}
		for (int i = 0; i < rabbits.length; i++) {
			updatePositionPercept(rabbits[i]);
		}
	}

	/**
	 * Updates grass percepts for a single rabbit
	 * 
	 * @param rabbit
	 *            The rabbit to update
	 */
	private void updateGrassPercept(Agent rabbit) {
		if (grid.getSquare(rabbit.getCoordinates()).isGrass()) {
			addPercept(rabbit.getName(), Literal.parseLiteral("grass"));
		} else {
			addPercept(rabbit.getName(), Literal.parseLiteral("mud"));
		}
		// TODO look for grass next to current square
	}

	/**
	 * Updates fox percepts for a single rabbit.
	 * 
	 * @param rabbit
	 *            The rabbit to update
	 */
	private void updateSurroundingPercepts(Agent rabbit) {
		int rabbitX = rabbit.getCoordinates().getX();
		int rabbitY = rabbit.getCoordinates().getY();
		GridSquareModel up, down, left, right;
		// these will be set to null by grid if the squares don't exist
		up = grid.getSquare(rabbitX, rabbitY + 1);
		down = grid.getSquare(rabbitX, rabbitY - 1);
		right = grid.getSquare(rabbitX + 1, rabbitY);
		left = grid.getSquare(rabbitX - 1, rabbitY);

		// TODO better design for this mess
		checkFox(up, 'u', rabbit);
		checkGrass(up, 'u', rabbit);
		checkFox(down, 'd', rabbit);
		checkGrass(down, 'd', rabbit);
		checkFox(left, 'l', rabbit);
		checkGrass(left, 'l', rabbit);
		checkFox(right, 'r', rabbit);
		checkGrass(right, 'r', rabbit);
	}

	private void checkFox(GridSquareModel square, char direction, Agent rabbit) {
		if (square != null && square.getAgents().contains(fox)) {
			logger.logp(Level.INFO, "SquareEcoSystem", "updateFoxPercept",
					"agent " + rabbit.getName() + " saw a fox! (" + direction
							+ ")");
			addPercept(rabbit.getName(),
					Literal.parseLiteral("animal(fox, " + direction + ")"));
		}
	}

	private void checkGrass(GridSquareModel square, char direction, Agent rabbit) {
		if (square != null && square.isGrass()) {
			addPercept(rabbit.getName(),
					Literal.parseLiteral("resource(grass," + direction + ")"));
		}
	}

	/**
	 * Finds the rabbit with the specified name if one exists
	 * 
	 * @param agent
	 *            the name of the rabbit to find
	 * @return the rabbit as an agent object or null if the rabbit doesn't exist
	 */
	public Rabbit getRabbit(String agent) {
		Rabbit rabbit = null;
		for (int i = 0; i < rabbits.length; i++) {
			if (rabbits[i].getName().equals(agent)) {
				rabbit = rabbits[i];
			}
		}
		return rabbit;
	}

	private void updatePositionPercept(Agent agent) {
		if (agent.getCoordinates().getX() > 0) {
			addPercept(agent.getName(), Literal.parseLiteral("space(l)"));
		}
		if (agent.getCoordinates().getX() < x) {
			addPercept(agent.getName(), Literal.parseLiteral("space(r)"));
		}
		if (agent.getCoordinates().getY() > 0) {
			addPercept(agent.getName(), Literal.parseLiteral("space(d)"));
		}
		if (agent.getCoordinates().getY() < y) {
			addPercept(agent.getName(), Literal.parseLiteral("space(u)"));
		}
	}

	/**
	 * Specified fox attacks named prey
	 * 
	 * @param ag
	 * @param preyName
	 * @return
	 */
	private boolean attack(String agent, String preyName) {
		Fox ag = fox; // TODO allow for more than one fox!!
		boolean result = false;
		GridSquareModel currentSquare = grid.getSquare(ag.getCoordinates());
		// eat the first rabbit we find? TODO maybe eat slowest rabbit?
		Agent prey = fox.getPrey();
		if (prey != null) {
			prey.lock();
			boolean success = prey.beAttacked(ag);
			wait(ag, ag.eatRabbitSpeed());
			if (success) {
				result = true;
				prey.kill();
			}
			prey.unlock();
			grid.moveAgent(fox, fox.getPrey().getCoordinates());
		}
		return result;
	}

	/**
	 * Waits for the correct amount of time based on the agents speed.
	 * 
	 * @param agent
	 */
	private void wait(Agent agent, int actionSpeed) {
		try {
			Thread.sleep(3000 / actionSpeed);
		} catch (Exception e) {

		}
	}

	private boolean eatGrass(String agent) {
		Rabbit rabbit = getRabbit(agent);
		logger.logp(Level.INFO, "SquareEcoSystem", "eatGrass",
				"agent " + rabbit.getName() + " is attempting to eat grass at "
						+ rabbit.getCoordinates());
		GridSquareModel square = grid.getSquare(rabbit.getCoordinates());
		square.lock();
		boolean success = false;
		if (square.isGrass()) {
			square.takeGrass(1);
			wait(rabbit, rabbit.grassEatingSpeed(square.getGrassHeight()));
			success = true;
		}
		square.unlock();
		if (success) {
			return true;
		}
		logger.logp(Level.WARNING, "SquareEcoSystem", "eatGrass",
				"failed to eat grass at position: "
						+ rabbit.getCoordinates().toString());
		return false;
	}

	/**
	 * Convert agent string to agent object representation.
	 * 
	 * @param agent
	 *            The agent name
	 * @return The agent object representation.
	 */
	private Agent getAgent(String agent) {
		if (agent.contains("rabbit")) {
			return getRabbit(agent);
		}
		return fox;

	}

	/**
	 * Check the agent can move in the specified direction then move it,
	 * updating the agent's coordinates and the lists of agents on the
	 * appropriate GridSquares.
	 * 
	 * @param agentString
	 *            The name of the agent to move.
	 * @param direction
	 *            The direction to move in.
	 * @return
	 */
	private boolean move(String agentString, char direction) {
		Agent agent = getAgent(agentString);
		wait(agent, agent.getSpeed());
		return grid.moveAgent(agent, direction);
	}
}
