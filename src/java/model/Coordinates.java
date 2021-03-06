package model;

public class Coordinates {

	private int x;
	private int y;

	public Coordinates(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	/**
	 * @return a copy of this object
	 */
	public Coordinates clone() {
		return new Coordinates(x,y);
	}

	/**
	 * Check if the provided coordinates are semantically equal to these
	 * coordinates.
	 * 
	 * @param comparable
	 *            The coordinates to compare.
	 * @return True if they are equal, false if not.
	 */
	public boolean equals(Coordinates comparable) {
		return (this.getX() == comparable.getX())
				&& (this.getY() == comparable.getY());
	}

	/**
	 * Adds the provided coordinates to these coordinates
	 * 
	 * @param toAdd
	 *            The coordinates to add.
	 */
	public void add(Coordinates toAdd) {
		x += toAdd.getX();
		y += toAdd.getY();
	}

	/**
	 * Adds the provided coordinates together.
	 * 
	 * @param first
	 *            The first set of coordinates to be added.
	 * @param second
	 *            The second set of coordinates to be added.
	 * @return A set of coordinates representing the sum of the provided
	 *         coordinates.
	 */
	public static Coordinates add(Coordinates first, Coordinates second) {
		int newX = first.getX() + second.getX();
		int newY = first.getY() + second.getY();
		return new Coordinates(newX, newY);
	}

	/**
	 * Subtracts the second coordinates from the first
	 * 
	 * @param first
	 *            The set of coordinates to subtract from
	 * @param second
	 *            The set of coordinates to subtract
	 * @return A set of coordinates representing the result of the subtraction.
	 */
	public static Coordinates subtract(Coordinates first, Coordinates second) {
		int newX = first.getX() - second.getX();
		int newY = first.getY() - second.getY();
		return new Coordinates(newX, newY);
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}

	/**
	 * Converts a char direction indicator to coordinates, for example 'l' would
	 * be converted to (-1, 0).
	 * 
	 * @param direction
	 *            u, d, l or r
	 * @return direction as coordinates
	 */
	public static Coordinates charToCoordinates(char direction) {
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

	public boolean adjacentTo(Coordinates coordinates) {
		return (coordinates.getX() == x && (coordinates.getY() == y - 1 || coordinates
				.getY() == y + 1))
				|| (coordinates.getY() == y && (coordinates.getX() == x - 1 || coordinates
						.getX() == x + 1));
	}
	
	public static int getDistance(Coordinates origin, Coordinates destination){
		Coordinates diff = subtract(origin, destination);
		int x = Math.abs(diff.getX());
		int y = Math.abs(diff.getY());
		return x+y;
	}
}
