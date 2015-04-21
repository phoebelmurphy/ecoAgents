package model.agents;

import model.Coordinates;


public class Rabbit extends Agent {
	
	public Rabbit(String name, Coordinates coordinates) {
		super(name, coordinates);
		setSpeed(5);
		setStrength(1);
	}

	@Override
	public boolean prey() {
		return true;
	}
	
	public int safeDistance() {
		return 3;
	}
	
	public int grassEatingSpeed(int grassHeight) {
		if(grassHeight > 3) {
			return 5;
		}
		return 2;
	}

	@Override
	public boolean beAttacked(Agent attacker) {
		
		return true;
	}


}
