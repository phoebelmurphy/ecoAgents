package model.agents;

import model.Coordinates;


public class Rabbit extends Agent {

	public Rabbit(String name, Coordinates coordinates) {
		super(name, coordinates);
		setSpeed(5);
	}

	@Override
	public boolean prey() {
		return true;
	}

}
