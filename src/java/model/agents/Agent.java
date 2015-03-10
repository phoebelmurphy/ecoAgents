package model.agents;

import java.util.concurrent.locks.ReentrantLock;

import model.Coordinates;



public abstract class Agent {
	private String name;
	private Coordinates coordinates;
	private int speed;
	private int strength;
	private int daysOld;
	private ReentrantLock lock = new ReentrantLock();
	private boolean alive = true;
	private Agent prey;
	
	public Agent(String name, Coordinates coordinates){
		this.name = name;
		this.setCoordinates(coordinates);
		daysOld =0;
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
	
	protected void setStrength(int strength) {
		this.strength = strength;;
	}
	
	protected void setAge(int daysOld){
		this.daysOld = daysOld;
	}
	
	public int getStrength() {
		return strength;
	}
	
	public void addDayToAge() {
		daysOld++;
	}
	
	public int getAgeInDays(){
		return daysOld;
	}
	public void lock(){
		lock.lock();
	}
	public void unlock() {
		lock.unlock();
	}
	/**
	 * A method to check if this kind of agent is a prey animal.
	 * @return true if prey, false if not
	 */
	public abstract boolean prey();
	
	public abstract boolean beAttacked(Agent attacker);
	
	public void kill() {
		alive = false;
	}
	
	public boolean isAlive() {
		return alive;
	}
	public void setPrey(Agent preyAgent) {
		prey = preyAgent;
		
	}
	
	public Agent getPrey() {
		return prey;
	}


}
