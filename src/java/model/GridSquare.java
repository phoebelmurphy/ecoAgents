package model;

import java.util.ArrayList;
import java.util.List;

public class GridSquare {
	private Coordinates coordinates;
	private List<Agent> agents;
	private boolean grass;
	public GridSquare(Coordinates coordinates) {
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
		return removedCount;
	}

	public boolean isGrass() {
		return grass;
	}

	public void setGrass(boolean grass) {
		this.grass = grass;
	}
	
	public void addAgent(Agent agent){
		agents.add(agent);
	}	

}
