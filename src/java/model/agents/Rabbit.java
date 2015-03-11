package model.agents;

import java.util.Random;

import model.Coordinates;


public class Rabbit extends Agent {
	
	private Random random = new Random();
	public Rabbit(String name, Coordinates coordinates) {
		super(name, coordinates);
		setSpeed(5);
	}

	@Override
	public boolean prey() {
		return true;
	}
	
	public int grassEatingSpeed(int grassHeight) {
		if(grassHeight > 3) {
			return 5;
		}
		return 2;
	}

	@Override
	public boolean beAttacked(Agent attacker) {
		boolean caught = false;
		boolean success = false;
		//roll to see if attacker catches rabbit
		//this method is called when the attacker is very close
		//so rabbit is unlikely to get away
		//TODO look up stats on how foxes catch rabbits
		if( (getSpeed() < attacker.getSpeed()) ){
			caught = random.nextInt(100) > 5;
		}
		else if(getSpeed() == attacker.getSpeed()){
			caught = random.nextInt(100) > 20;
		}
		else if( getSpeed() > attacker.getSpeed())
		{
			caught = random.nextInt(100) > 30;
		}
		else {
			caught = false;
			//this shouldn't happen though
		}
		
		if(!caught) {
			success = false;
		}
		else if(attacker.getStrength() > getStrength()) {
			success = random.nextInt(100) > 10;
		}
		else if(attacker.getStrength() == getStrength()){
			success = random.nextInt(100) > 50;
		}
		else if(attacker.getStrength() < this.getStrength()) {
			success = random.nextInt(100) > 80;
		}
		else {
			success = false;
			//this shouldn't happen though
		}
		return true;
	}

}
