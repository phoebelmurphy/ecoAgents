package model;

import java.util.ArrayList;
import java.util.List;

import events.UpdateListener;
import model.agents.Agent;

public class GridSquareModel {
	private Coordinates coordinates;
	private List<Agent> agents;
	private List<UpdateListener> listeners;
	private boolean grass;
	public GridSquareModel(Coordinates coordinates) {
		listeners = new ArrayList<UpdateListener>();
		this.coordinates = coordinates;
		agents = new ArrayList<Agent>();
		setGrass(true);
	}
	
	public Coordinates getCoordinates(){
		return coordinates;
	}
	
	public List<Agent> getAgents(){
		return agents;
	}
	
	/**
	 * Add an UpdateListener to notify when the model is updated
	 * @param listener The listener to add
	 */
	public void addUpdateListener(UpdateListener listener){
		listeners.add(listener);
	}
	
	private void notifyListeners(){
		for(UpdateListener listener : listeners) {
			listener.modelUpdated();
		}
	}
	/**
	 * Removes all agents with the specified name from the list.
	 * @param name The name of the agent to remove
	 * @return The count of agents removed
	 */
	public int removeAgent(String name) {
		int removedCount = 0;
		for(int i=0; i<agents.size(); i++){
			Agent agent = agents.get(i);
			if (agent.getName().equals(name)){
				agents.remove(i);
				removedCount++;
			}
		}
		notifyListeners();
		return removedCount;
	}

	public boolean isGrass() {
		return grass;
	}

	public void setGrass(boolean grass) {
		this.grass = grass;
		notifyListeners();
	}
	
	public void addAgent(Agent agent){
		agents.add(agent);
		notifyListeners();
	}	

}
