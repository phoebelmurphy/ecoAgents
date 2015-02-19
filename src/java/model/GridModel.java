package model;

import java.util.Random;
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
	private Random random = new Random();
	private static Logger logger = Logger.getLogger(SquareEcoSystem.loggerName);
	private static GridModel instance;
	
	public static void createInstance(int x, int y){
		instance = new GridModel(x,y);
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
		growGrass();

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

	public GridRowModel getRow(int i) {
		if (i < rows.length) {
			return rows[i];
		}
		return null;
	}

	public boolean moveAgent(Agent agent, Coordinates newPos) {
		return moveAgent(agent, newPos.getX(), newPos.getY());
	}

	public boolean moveAgent(Agent agent, int newx, int newy) {
		boolean result;
		wait(agent);
		if ((newx < 0) || (newy < 0) || (newx >= x) || (newy >= y)) {
			String message = "can't move. direction (" + newx + "," + newy
					+ ") from " + agent.getCoordinates().toString();
			logger.logp(Level.SEVERE, "Grid", "moveAgent", message);
			result = false;
		} else {
			result = true;
			getSquare(agent.getCoordinates()).removeAgent(agent.getName());
			agent.getCoordinates().setX(newx);
			agent.getCoordinates().setY(newy);
			getSquare(agent.getCoordinates()).addAgent(agent);
			logger.logp(Level.INFO, "Grid", "moveAgent", "moving to position "
					+ agent.getCoordinates().toString());
		}
		return result;
	}

	private void wait(Agent agent){
		try{
			Thread.sleep(3000/agent.getSpeed());
		}
		catch(Exception e){
			
		}
	}
	/**
	 * Move the agent in the specified direction, if possible. u for up, d for
	 * down, l for left and r for right
	 * 
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

		return moveAgent(agent, destination.getX(), destination.getY());
	}

	
	/**
	 * Iterates through all the grid squares and randomly adds
	 * grass to some. Maybe later grass will spread from one square to the next.
	 */
	public void growGrass() {
		for (int i = 0; i < y; i++) {
			GridRowModel currentRow = rows[i];
			for (int n = 0; n < x; n++) {
				GridSquareModel currentSquare = currentRow.getSquare(n);
				if (!currentSquare.isGrass()) {
					if (random.nextInt(100) < 5) {
						currentSquare.setGrass(true);
					}
				}
			}
		}
	}

}
