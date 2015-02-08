package model.agents;

import model.Coordinates;

public class Fox extends Agent {

	public Fox(String name, Coordinates coordinates) {
		super(name, coordinates);
		setSpeed(3);
	}

	@Override
	public boolean prey() {
		return false;
	}

}
