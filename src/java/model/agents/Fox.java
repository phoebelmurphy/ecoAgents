package model.agents;

import model.Coordinates;

public class Fox extends Agent {

	public Fox(String name, Coordinates coordinates) {
		super(name, coordinates);
		setSpeed(3);
		setStrength(5);
	}

	@Override
	public boolean prey() {
		return false;
	}
	
	public int eatRabbitSpeed() {
		return 2;
	}

	@Override
	public boolean beAttacked(Agent attacker) {
		if(getStrength() > attacker.getStrength()){
			return true;
		}
		return false;
	}

}
