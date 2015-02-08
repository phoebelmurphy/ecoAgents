package model;


public class Agent {
	private String name;
	private Coordinates coordinates;
	private Animal type;
	public Agent(String name, Coordinates coordinates, Animal type){
		this.name = name;
		this.setCoordinates(coordinates);
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
	public Animal getType() {
		return type;
	}


}
