package model.agents;

import model.Coordinates;



public abstract class Agent {
	private String name;
	private Coordinates coordinates;
	private int speed;
	
	public Agent(String name, Coordinates coordinates){
		this.name = name;
		this.setCoordinates(coordinates);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Coordinates getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}

	public int getSpeed() {
		return speed;
	}
	protected void setSpeed(int speed) {
		this.speed = speed;
	}
	
	/**
	 * A method to check if this kind of agent is a prey animal.
	 * @return true if prey, false if not
	 */
	public abstract boolean prey();


}
