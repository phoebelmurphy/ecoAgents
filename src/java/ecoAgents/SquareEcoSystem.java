package ecoAgents;

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.environment.Environment;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import view.MainWindow;
import model.Agent;
import model.Animal;
import model.Coordinates;
import model.GridModel;
import model.GridSquareModel;

public class SquareEcoSystem extends Environment {

	Random random = new Random();
	Logger logger = Logger.getLogger("env.logger");
	int x, y;
	GridModel grid;
	Agent rabbits[];

	Agent fox;

	public void init(String[] args) {
		logger.setLevel(Level.ALL);
		x = 5;
		y = 5;
		grid = new GridModel(x, y, logger);
		rabbits = new Agent[2];
		addRabbits();
		addFox();
		updatePercepts();
		
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.addSquares(grid);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
					logger.log(Level.SEVERE, "window crashed", e);
				}
//			}
//		});
	}

	/**
	 * Add a fox to the grid
	 */
	private void addFox() {
		fox = new Agent("fox", new Coordinates(1, 2), Animal.FOX);
		grid.getSquare(1, 2).addAgent(fox);
	}

	/**
	 * Add rabbits to the grid.
	 */
	private void addRabbits() {
		for (int i = 0; i < rabbits.length; i++) {
			rabbits[i] = new Agent("rabbit" + (i + 1), new Coordinates(i, i),
					Animal.RABBIT);
			grid.getSquare(i, i).addAgent(rabbits[i]);
		}
	}

	public boolean executeAction(String ag, Structure action) {
		logger.logp(Level.INFO, "SquareEcoSystem", "executeAction",
				"executing action " + action.getFunctor() + " for agent " + ag);
		boolean result = false;
		if (action.getFunctor().equals("eat")) {
			logger.logp(Level.INFO, "SquareEcoSystem", "executeAction",
					"agent name " + ag + " is eating");
			result = eat(ag);
		} else if (action.getFunctor().equals("move")) {
			logger.logp(Level.INFO, "SquareEcoSystem", "updateGrassPercept",
					"agent name " + ag + " is moving");
			result = move(ag, action.getTerm(0).toString().charAt(0));
		}
		if (result) {
			updatePercepts();
		}
		return result;
	}

	private void updatePercepts() {
		grid.growGrass();
		for (Agent rabbit : rabbits) {
			clearPercepts(rabbit.getName());
		}

		updateRabbitPercepts();
		updatePositionPercepts();
	}

	/**
	 * Updates percepts for all rabbits
	 */
	private void updateRabbitPercepts() {
		for (int i = 0; i < rabbits.length; i++) {
			updateGrassPercept(rabbits[i]);
			updateSurroundingPercepts(rabbits[i]);
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
			logger.logp(Level.INFO, "SquareEcoSystem", "updateGrassPercept",
					"agent name " + rabbit.getName() + " sees grass");
			addPercept(rabbit.getName(), Literal.parseLiteral("grass"));
		} else {
			logger.logp(Level.INFO, "SquareEcoSystem", "updateGrassPercept",
					"agent name " + rabbit.getName() + "doesn't see grass");
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

		//TODO better design for this mess
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
					"agent " + rabbit.getName() + " saw a fox! (" + direction + ")");
			addPercept(rabbit.getName(),
					Literal.parseLiteral("animal(fox, " + direction + ")"));
		}
	}
	
	private void checkGrass(GridSquareModel square, char direction, Agent rabbit){
		if(square!= null && square.isGrass()){
			logger.logp(Level.INFO, "SquareEcoSystem", "updateFoxPercept",
					"agent " + rabbit.getName() + " saw grass (" + direction + ")");
			addPercept(rabbit.getName(), Literal.parseLiteral("resource(grass," +direction + ")"));
		}
	}

	/**
	 * Finds the rabbit with the specified name if one exists
	 * 
	 * @param agent
	 *            the name of the rabbit to find
	 * @return the rabbit as an agent object or null if the rabbit doesn't exist
	 */
	private Agent getRabbit(String agent) {
		Agent rabbit = null;
		for (int i = 0; i < rabbits.length; i++) {
			if (rabbits[i].getName().equals(agent)) {
				rabbit = rabbits[i];
			}
		}
		return rabbit;
	}

	/**
	 * Updates the position percepts for all agents.
	 */
	private void updatePositionPercepts() {
		for (int i = 0; i < rabbits.length; i++) {
			updatePositionPercept(rabbits[i]);
		}
		updatePositionPercept(fox);
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

	public boolean eat(String agent) {
		if (agent.contains("rabbit")) {
			return eatGrass(agent);
		} else if (agent.contains("fox")) {
			return eatRabbit(fox);
		}
		logger.logp(Level.INFO, "SquareEcoSystem", "eat",
				"agent not recognised " + agent);
		return false;
	}

	public boolean eatRabbit(Agent agent) {
		boolean result = false;
		GridSquareModel currentSquare = grid.getSquare(agent.getCoordinates());
		List<Agent> squareAgents = currentSquare.getAgents();
		//eat the first rabbit we find
		//TODO maybe make this smarter
		for (int i=0; i<squareAgents.size() && !result; i++) {
			if(squareAgents.get(i).getType() == Animal.RABBIT){
				result = true;
				//TODO work out a way to kill a rabbit when the fox eats it
			}
		}
		return result;
	}

	public boolean eatGrass(String agent) {
		Agent rabbit = getRabbit(agent);
		logger.logp(Level.INFO, "SquareEcoSystem", "eatGrass",
				"agent " + rabbit.getName() + " is attempting to eat grass at "
						+ rabbit.getCoordinates());
		if (grid.getSquare(rabbit.getCoordinates()).isGrass()) {
			grid.getSquare(rabbit.getCoordinates()).setGrass(false);
			return true;
		}
		logger.logp(Level.INFO, "SquareEcoSystem", "eatGrass",
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
		 if(agent.contains("rabbit")){
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
	public boolean move(String agentString, char direction) {
		Agent agent = getAgent(agentString);
		
		logger.logp(Level.INFO, "SquareEcoSystem", "executeAction",
				"direction: " + direction);
		return grid.moveAgent(agent, direction);
	}
}
