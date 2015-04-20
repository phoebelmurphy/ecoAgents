package ecoAgents;

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.environment.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
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
	private ArrayList<Rabbit> rabbits;
	private ReentrantLock rabbitsLock = new ReentrantLock();
	private Fox foxes[];
	private int rabbitId = 0;

	public void init(String[] args) {
		grid = GridModel.getInstance();
		x = grid.getX();
		y=grid.getY();
		ResourceManager manager = new ResourceManager(grid);
		new Thread(manager, "resourcemanager").start();
		rabbits = new ArrayList<Rabbit>();
		foxes = new Fox[1];
		addRabbits();
		addFox();
		updateRabbitPercepts();
		updateAllFoxes();

	}

	/**
	 * Add a fox to the grid
	 */
	private void addFox() {
		for (int i = 0; i < foxes.length; i++) {
			foxes[i] = new Fox("fox" + (i + 1), new Coordinates(8, 8));
			grid.addAgent(foxes[i], 6, 3*i);
		}
	}

	/**
	 * Add rabbits to the grid.
	 */
	private void addRabbits() {
		rabbitsLock.lock();
		for (int i = 0; i < 5; i++) {
			rabbitId++;
			rabbits.add(new Rabbit("rabbit" + (rabbitId), new Coordinates(i, i)));
			grid.addAgent(rabbits.get(i), i, i);
		}
		rabbitsLock.unlock();
	}

	public boolean executeAction(String ag, Structure action) {
		boolean result = false;
		if (action.getFunctor().equals("eat")) {
			result = eatGrass(ag);
		} else if (action.getFunctor().equals("move")) {
			//char direction = action.getTerm(0).toString().charAt(0);
			//result = move(ag, direction);
			result = true;
			moveRabbit(getRabbit(ag));
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
		else if(action.getFunctor().equals("haveBaby")){
			result = haveBaby(ag);
		}
		if (ag.contains("rabbit")) {
			updateRabbitPercepts();
		} else {
			logger.log(Level.INFO, "updating fox percepts");
			updateAllFoxes();
		}
		return result;
	}

	private boolean haveBaby(String ag) {
		rabbitsLock.lock();
		rabbitId++;
		Agent parent = getAgent(ag);
		String newName = "rabbit" + (rabbitId);
		Rabbit newRabbit = new Rabbit(newName, parent.getCoordinates().clone());
		parent.setChild(newRabbit);
		rabbits.add(newRabbit);
		grid.addAgent(newRabbit);
		rabbitsLock.unlock();
		return true;
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
		wait(hunter, hunter.getSpeed());
		Agent preyAgent = grid.findClosest(hunter, prey);
		hunter.setPrey(preyAgent);
		return true;
	}
	
	private void updateAllFoxes(){
		for(Fox fox : foxes) {
			updateFoxPercepts(fox);
		}
	}

	private void updateFoxPercepts(Fox fox) {
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
		rabbitsLock.lock();
		for (Rabbit rabbit : rabbits) {
			updateSingleRabbit(rabbit);
		}
		rabbitsLock.unlock();
	}
	
	private void updateSingleRabbit(Rabbit rabbit){
		clearPercepts(rabbit.getName());
		updateGrassPercept(rabbit);
		updateSurroundingPercepts(rabbit);
		updatePositionPercept(rabbit);
		if(rabbit.getChild() != null) {
			addPercept(rabbit.getName(), Literal.parseLiteral("babyName(" + rabbit.getChild().getName() + ")"));
			//rabbit.setChild(null);
		}
		if(rabbit.shouldMove()){
			addPercept(rabbit.getName(), Literal.parseLiteral("shouldMove"));
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
		GridSquareModel up2, down2, left2, right2;
		// these will be set to null by grid if the squares don't exist
		up = grid.getSquare(rabbitX, rabbitY + 1);
		up2 = grid.getSquare(rabbitX, rabbitY + 2);
		down = grid.getSquare(rabbitX, rabbitY - 1);
		down2 = grid.getSquare(rabbitX, rabbitY -2);
		right = grid.getSquare(rabbitX + 1, rabbitY);
		right2 = grid.getSquare(rabbitX + 2, rabbitY);
		left = grid.getSquare(rabbitX - 1, rabbitY);
		left2 = grid.getSquare(rabbitX - 2, rabbitY);

		// TODO better design for this mess
		checkFox(up, 'u', rabbit);
		checkFox(up2, 'u', rabbit);
		checkGrass(up, 'u', rabbit);
		checkFox(down, 'd', rabbit);
		checkFox(down2, 'd', rabbit);
		checkGrass(down, 'd', rabbit);
		checkFox(left, 'l', rabbit);
		checkFox(left2, 'l', rabbit);
		checkGrass(left, 'l', rabbit);
		checkFox(right, 'r', rabbit);
		checkFox(right2, 'r', rabbit);
		checkGrass(right, 'r', rabbit);
	}

	private void checkFox(GridSquareModel square, char direction, Agent rabbit) {
		boolean sawFox = false;
		if(square == null) {
			return;
		}
		List<Agent> agents = square.getAgents();
		for(Agent agent : agents) {
			if (agent.getName().contains("fox")){
				sawFox = true;
			}
		}
		if (sawFox) {
			addPercept(rabbit.getName(),
					Literal.parseLiteral("animal(fox, " + direction + ")"));
		}
	}
	
	private Fox findNearestFox(Agent rabbit){
		Fox fox = (Fox) grid.findClosest(rabbit, "fox");
		return fox;
	}
	
	private void moveRabbit(Rabbit rabbit) {
		Fox fox = findNearestFox(rabbit);
		int x = rabbit.getCoordinates().getX();
		int y = rabbit.getCoordinates().getY();

		GridSquareModel bestSquare;
		if(Coordinates.getDistance(rabbit.getCoordinates(), fox.getCoordinates()) < rabbit.safeDistance() ){
			//move away from fox
			//find smallest out of x dif and y dif
			int dx = x - fox.getCoordinates().getX();
			int dy = y - fox.getCoordinates().getY();
			if (Math.abs(dx) >=Math.abs(dy) && dy >=0 && grid.getSquare(x, y-1) != null ){ 
				bestSquare = grid.getSquare(x, y-1);
			}
			else if (Math.abs(dx) >=Math.abs(dy) && dy <=0 && grid.getSquare(x, y+1) != null ){
				bestSquare = grid.getSquare(x, y+1);
			}
			else if (dx >= 0 && grid.getSquare(x-1, y) != null){
				bestSquare = grid.getSquare(x-1, y);
			}
			else if( dx <= 0 && grid.getSquare(x+1, y) != null ){
				bestSquare = grid.getSquare(x+1, y);
			}
			else if (dy >= 0 && grid.getSquare(x, y-1) != null ) {
				//not moving the most away from the fox but better than nothing
				bestSquare = grid.getSquare(x, y-1);
			}
			else {
				//might work, or might be null, but there's nothing else to do
				bestSquare = grid.getSquare(x, y+1);
			}
		}
		else {
			//its safe so find some grass
			bestSquare = grid.getSquare(x, y);
			Coordinates[] directions = new Coordinates[] { new Coordinates(x+1, y), new Coordinates(x, y+1),
				new Coordinates(x-1, y), new Coordinates(x, y-1)
			};
			for(Coordinates direction : directions) {
				if(bestSquare == null || grid.getSquare(direction).getGrassHeight() > bestSquare.getGrassHeight()){
					bestSquare = grid.getSquare(direction);
				}
			}
			
		}
		grid.moveAgent(rabbit, bestSquare.getCoordinates());
	}

	private void checkGrass(GridSquareModel square, char direction, Agent rabbit) {
		if (square != null && square.isGrass()) {
			addPercept(rabbit.getName(),
					Literal.parseLiteral("resource(grass," + direction + ")"));
		}
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
		Fox fox = getFox(agent); // TODO allow for more than one fox!!
		if(fox == null) {
			return false;
		}
		boolean result = false;
		// eat the first rabbit we find? TODO maybe eat slowest rabbit?
		Agent prey = fox.getPrey();
		if (prey != null) {
			prey.lock();
			boolean success = prey.beAttacked(fox);
			wait(fox, fox.eatRabbitSpeed());
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
		Agent foundAgent = null;
		if (agent.contains("rabbit")) {
			foundAgent =  getRabbit(agent);
		}
		else if(agent.contains("fox")) {
			foundAgent =  getFox(agent);
		}
		return foundAgent;

	}

	/**
	 * Finds the rabbit with the specified name if one exists
	 * 
	 * @param agent
	 *            the name of the rabbit to find
	 * @return the rabbit object or null if the rabbit doesn't exist
	 */
	public Rabbit getRabbit(String agent) {
		Rabbit rabbit = null;
		for (int i = 0; i < rabbits.size(); i++) {
			if (rabbits.get(i).getName().equals(agent)) {
				rabbit = rabbits.get(i);
			}
		}
		return rabbit;
	}
	
	/**
	 * Finds the fox with the specified name if one exists
	 * 
	 * @param agent
	 *            the name of the fox to find
	 * @return the fox object or null if the fox doesn't exist
	 */
	public Fox getFox(String agent) {
		Fox fox = null;
		for (int i = 0; i < foxes.length; i++) {
			if (foxes[i].getName().equals(agent)) {
				fox = foxes[i];
			}
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
