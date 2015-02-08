package model;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	Logger logger;

	public GridModel(int x, int y, Logger logger) {
		this.x = x;
		this.y = y;
		this.logger = logger;
		rows = new GridRowModel[y];
		for (int i = 0; i < y; i++) {
			rows[i] = new GridRowModel(x);
			for (int n = 0; n < x; n++) {
				Coordinates coords = new Coordinates(i, n);
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
		Coordinates directionCoordinates = charToCoordinates(direction);
		int newX = agent.getCoordinates().getX() + directionCoordinates.getX();
		int newY = agent.getCoordinates().getY() + directionCoordinates.getY();

		return moveAgent(agent, newX, newY);
	}

	/**
	 * Converts a char direction indicator to coordinates, for example 'l' would
	 * be converted to (-1, 0).
	 * 
	 * @param direction
	 *            u, d, l or r
	 * @return direction as coordinates
	 */
	public Coordinates charToCoordinates(char direction) {
		int dx = 0;
		int dy = 0;
		if (direction == 'l') {
			dx = -1;
		} else if (direction == 'r') {
			dx = 1;
		} else if (direction == 'd') {
			dy = -1;
		} else if (direction == 'u') {
			dy = 1;
		}
		return new Coordinates(dx, dy);
	}

	/**
	 * Flower gleam and glow Let your magic shine Make there be more grass It is
	 * growing time Iterates through all the grid squares and randomly adds
	 * grass to some. Maybe later grass will spread from one square to the next.
	 */
	public void growGrass() {
		for (int i = 0; i < y; i++) {
			GridRowModel currentRow = rows[i];
			for (int n = 0; n < x; n++) {
				GridSquareModel currentSquare = currentRow.getSquare(n);
				if (!currentSquare.isGrass()) {
					if (random.nextInt(100) < 20) {
						currentSquare.setGrass(true);
					}
				}
			}
		}
	}

}
