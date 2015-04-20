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
//		boolean caught = false;
//		boolean success = false;
//		//roll to see if attacker catches rabbit
//		//this method is called when the attacker is very close
//		//so rabbit is unlikely to get away
//		//TODO look up stats on how foxes catch rabbits
//		if( (getSpeed() < attacker.getSpeed()) ){
//			caught = random.nextInt(100) > 5;
//		}
//		else if(getSpeed() == attacker.getSpeed()){
//			caught = random.nextInt(100) > 20;
//		}
//		else if( getSpeed() > attacker.getSpeed())
//		{
//			caught = random.nextInt(100) > 30;
//		}
//		else {
//			caught = false;
//			//this shouldn't happen though
//		}
//		
//		if(!caught) {
//			success = false;
//		}
//		else if(attacker.getStrength() > getStrength()) {
//			success = random.nextInt(100) > 10;
//		}
//		else if(attacker.getStrength() == getStrength()){
//			success = random.nextInt(100) > 50;
//		}
//		else if(attacker.getStrength() < this.getStrength()) {
//			success = random.nextInt(100) > 80;
//		}
//		else {
//			success = false;
//			//this shouldn't happen though
//		}
		return true;
	}

	/**
	 * Determines if the rabbit is better off staying where it is,
	 * or if it would be benefited by moving.
	 * For example to somewhere with more grass, or
	 * if there is a fox nearby.
	 * @return true if the rabbit should move
	 */
	public boolean shouldMove(){
		return true;
	}
	
	public Coordinates idealLocation(){
		return null;
	}
}
