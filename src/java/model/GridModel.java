package model;

import java.util.logging.Level;
import java.util.logging.Logger;

import ecoAgents.SquareEcoSystem;
import model.agents.Agent;

public class GridModel {
	// represents a grid, like this
	//@formatter:off
	/* 
	 *    [ ] [ ] [ ] [ ] [ ]  <- one GridRow of length x
	 *    [ ] [ ] [ ] [ ] [ ]
	 * ^  [ ] [ ] [ ] [ ] [ ]
	 * |  [ ] [ ] [ ] [ ] [ ]
	 * y  [ ] [ ] [ ] [ ] [ ]
	 *    x ->
	 */
	//@formatter:on

	private int x;
	private int y;
	private GridRowModel[] rows; // list of y GridRows
	private static Logger logger;
	private static GridModel instance;
	
	public static void createInstance(int x, int y){
		instance = new GridModel(x,y);
		logger= Logger.getLogger(SquareEcoSystem.loggerName);
		logger.logp(Level.INFO, "GridModel", "createInstance", "Grid instance created");
	}
	public static GridModel getInstance(){
		return instance;
	}
	private GridModel(int x, int y) {
		this.x = x;
		this.y = y;
		rows = new GridRowModel[y];
		for (int i = 0; i < y; i++) {
			rows[i] = new GridRowModel(x);
			for (int n = 0; n < x; n++) {
				Coordinates coords = new Coordinates(n,i);
				GridSquareModel square = new GridSquareModel(coords);
				rows[i].addSquare(square, n);
			}
		}

	}

	public GridSquareModel getSquare(Coordinates coordinates) {
		return getSquare(coordinates.getX(), coordinates.getY());
	}

	public GridSquareModel getSquare(int x, int y) {
		if (x >= this.x || y >= this.y || x < 0 || y < 0) {
			return null;
		}
		return rows[y].getSquare(x);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	/**
	 * Get the row of squares at the specified y coordinate
	 * @param i the y coordinate of the row to get
	 * @return The row at this coordinate, or null if the coordinate isn't valid.
	 */
	public GridRowModel getRow(int i) {
		if (i < rows.length) {
			return rows[i];
		}
		return null;
	}

	/**
	 * Move the agent to the specified square, if possible.
	 * The agent is moved in a threadsafe way.
	 * @param agent The agent to move
	 * @param newPos The coordinates to move to
	 * @return True if the agent successfully moved
	 */
	public boolean moveAgent(Agent agent, Coordinates newPos) {
		return moveAgent(agent, newPos.getX(), newPos.getY());
	}

	/**
	 * Move agent to the specified square, if possible.
	 * The agent is moved in a threadsafe way.
	 * @param agent The agent to move
	 * @param newx The x coordinate to move to
	 * @param newy The y coordinate to move to
	 * @return True if the agent successfully moved.
	 */
	public boolean moveAgent(Agent agent, int newx, int newy) {
		boolean result;
		if (!checkSquareFree(newx, newy)) {
			String message = "can't move. direction (" + newx + "," + newy
					+ ") from " + agent.getCoordinates().toString();
			logger.logp(Level.SEVERE, "Grid", "moveAgent", message);
			result = false;
		}
		 else {
			result = true;
			GridSquareModel oldSquare = getSquare(agent.getCoordinates());
			oldSquare.lock();
			oldSquare.removeAgent(agent.getName());
			oldSquare.unlock();
			addAgent(agent, newx, newy);
			logger.logp(Level.INFO, "Grid", "moveAgent", "moving to position "
					+ agent.getCoordinates().toString());
		}
		return result;
	}
	
	/**
	 * A method to check if a square is free for an agent to move to.
	 * @param newx The x coordinate of the square to check.
	 * @param newy The y coordinate of the square to check.
	 * @return True if the square is free, otherwise false.
	 */
	private boolean checkSquareFree(int newx, int newy){
		return ((newx >= 0) && (newy >= 0) && (newx < x) && (newy < y));
	}

	/**
	 * Move the agent in the specified direction, if possible. u for up, d for
	 * down, l for left and r for right
	 * The agent is moved in a threadsafe way.
	 * @param agent
	 *            The agent to move
	 * @param direction
	 *            The direction to move in
	 * @return true if the move was successful, otherwise false
	 */
	public boolean moveAgent(Agent agent, char direction) {
		//create destination coordinates by adding the direction to the
		//agent's current coordinates
		Coordinates destination = Coordinates.add(agent.getCoordinates(),
				Coordinates.charToCoordinates(direction));
		logger.log(Level.INFO, "moving agent from " + agent.getCoordinates().toString() + " to " + destination.toString());
		logger.log(Level.INFO, "directional coordinates are " + Coordinates.charToCoordinates(direction));
		return moveAgent(agent, destination.getX(), destination.getY());
	}

	/**
	 * Adds an agent to the specified square in a threadsafe way.
	 * Acts identically to the other addAgent methods but allows
	 * adding with coordinates for convenience.
	 * @param agent The agent to add.
	 * @param destination The coordinates of the square to add to.
	 * @return True if the agent was successfully added, or false if not.
	 */
	public boolean addAgent(Agent agent, Coordinates destination){
		return addAgent(agent, destination.getX(), destination.getY());
	}
	
	/**
	 * Adds an agent to the specified square in a threadsafe way.
	 * Acts identically to the other addAgent methods but allows
	 * adding with x and y ints for convenience.
	 * @param agent The agent to add.
	 * @param x The x coordinate of the square to add to.
	 * @param y The y coordinate of the square to add to.
	 * @return True if the agent was successfully added or false if not.
	 */
	public boolean addAgent(Agent agent, int newx, int newy){
		if(!checkSquareFree(newx, newy)){
			return false;
		}
		agent.getCoordinates().setX(newx);
		agent.getCoordinates().setY(newy);
		GridSquareModel newSquare = getSquare(agent.getCoordinates());
		newSquare.lock();
		newSquare.addAgent(agent);
		newSquare.unlock();
		return true;
	}
	/**
	 * Iterates through all the grid squares and updates resources.
	 * Squares are updated in a threadsafe way.
	 */	
	public void updateSquares() {
		for (int i = 0; i < y; i++) {
			GridRowModel currentRow = rows[i];
			for (int n = 0; n < x; n++) {
				GridSquareModel currentSquare = currentRow.getSquare(n);
				currentSquare.lock();
				currentSquare.growGrass();
				currentSquare.unlock();
			}
		}
	}

}
