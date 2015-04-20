package model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import events.UpdateListener;
import model.agents.Agent;

public class GridSquareModel {
	private Coordinates coordinates;
	private List<Agent> agents;
	private List<Agent> deadAgents;
	private List<UpdateListener> listeners;
	private int grassHeight = 0;
	private int maxHeight = 9;
	private ReentrantLock lock = new ReentrantLock();

	public GridSquareModel(Coordinates coordinates) {
		listeners = new ArrayList<UpdateListener>();
		this.coordinates = coordinates;
		agents = new ArrayList<Agent>();
		deadAgents = new ArrayList<Agent>();
		grassHeight = 5;
	}

	public Coordinates getCoordinates() {
		return coordinates;
	}

	/**
	 * Acquire the lock on this class.
	 */
	public void lock(){
		lock.lock();
	}
	
	/**
	 * Release the lock on this class.
	 */
	public void unlock(){
		lock.unlock();
	}
	
	/**
	 * Gets all agents currently on this square.
	 * @return The list of agents currently on this square.
	 */
	public List<Agent> getAgents() {
		return agents;
	}
	
	/**
	 * Gets all dead agents on this square.
	 * @return The list of dead agents on this square
	 */
	public List<Agent> getDeadAgents() {
		return deadAgents;
	}

	/**
	 * Add an UpdateListener to notify when the model is updated
	 * 
	 * @param listener
	 *            The listener to add
	 */
	public void addUpdateListener(UpdateListener listener) {
		listeners.add(listener);
	}

	/**
	 * Notify all listeners that there have been updates to the model.
	 */
	private void notifyListeners() {
		for (UpdateListener listener : listeners) {
			listener.modelUpdated();
		}
	}

	/**
	 * Removes all agents with the specified name from the square.
	 * You would expect this to be 1 or 0.
	 * 
	 * @param name
	 *            The name of the agent to remove
	 * @return The count of agents removed
	 */
	public int removeAgent(String name) {
		int removedCount = removeAgentFromList(agents, name);
		notifyListeners();
		return removedCount;
	}
	
	/**
	 * Removes all dead agents with specified name from the square.
	 * @param name The name of the agent to remove
	 * @return The count of agents removed.
	 */
	public int removeDeadAgent(String name) {
		int removedCount = removeAgentFromList(deadAgents, name);
		return removedCount;
	}

	private int removeAgentFromList(List<Agent> list, String name){
		int removedCount = 0;
		for (int i = 0; i < list.size(); i++) {
			Agent agent = list.get(i);
			if (agent.getName().equals(name)) {
				list.remove(i);
				removedCount++;
			}
		}
		
		return removedCount;
	}
	/**
	 * Check if there is visible grass on this square.
	 * @return true if there is visible grass, otherwise false.
	 */
	public boolean isGrass() {
		return grassHeight > 4;
	}

	/**
	 * Set the grass to a specific height.
	 * Normally only used in setup.
	 * @param height The height to set the grass to.
	 */
	protected void setGrassHeight(int height){
		grassHeight = height;
	}
	/**
	 * Get the current height of the grass on this square.
	 * This is how much grass is available for eating etc.
	 * @return The grass height.
	 */
	public int getGrassHeight() {
		return grassHeight;
	}

	/**
	 * Grow grass by one step.
	 */
	public void growGrass() {
		if (grassHeight < maxHeight) {
			grassHeight++;
			notifyListeners();
		}
	}

	/**
	 * Attempts to remove the specified amount of grass from the square.
	 * If there isn't enough grass, removes as much as possible.
	 * @param grassToTake the amount of grass to attempt to take.
	 * @return the amount of grass actually taken
	 */
	public int takeGrass(int grassToTake) {
		int grassTaken;
		if (grassHeight-grassToTake > 0) {
			grassHeight -= grassToTake;
			grassTaken = grassToTake;	
		}
		else if(grassHeight > 0){
			grassTaken = grassHeight;
			grassHeight = 0;
		}
		else {
			grassTaken = 0;
		}
		notifyListeners();
		return grassTaken;
	}

	public void addAgent(Agent agent) {
		agents.add(agent);
		notifyListeners();
	}

}
