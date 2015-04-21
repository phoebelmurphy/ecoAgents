package tests.ecoAgents;

import static org.junit.Assert.assertTrue;
import jason.asSyntax.Structure;
import model.Coordinates;
import model.GridModel;
import model.agents.Fox;
import model.agents.Rabbit;

import org.junit.Before;
import org.junit.Test;

import ecoAgents.SquareEcoSystem;

public class SquareEcoSystemTest {
	private SquareEcoSystem model;
	private Rabbit rabbit;
	int x,y;
	@Before
	public void setup(){
		GridModel.createInstance(10, 10);
		model = new SquareEcoSystem();
		model.init(new String[] {""});
		rabbit = model.getRabbit("rabbit1");
		GridModel grid = GridModel.getInstance();
		x = grid.getX();
		y=grid.getY();
	}
	
	@Test
	public void testExecuteAction(){
		
		Structure eat = new Structure("eat");
		boolean eatResult = model.executeAction(rabbit.getName(), eat);
		assertTrue(eatResult);
		
		Structure move = new Structure("move");
		Coordinates oldCoordinates = rabbit.getCoordinates();
		boolean moveResult = model.executeAction(rabbit.getName(), move);
		assertTrue(moveResult);
		Coordinates newCoordinates = rabbit.getCoordinates();
		//should be next to or on prev position
		boolean moved  = newCoordinates.adjacentTo(oldCoordinates);
		if(!moved) {
			assertTrue(newCoordinates.equals(oldCoordinates));
		}
	}
	
	@Test
	public void testFox (){
		Fox fox = model.getFox("fox1");
		assertTrue(fox != null);
		assertTrue(x - fox.getCoordinates().getX() >= 0);
		assertTrue(y - fox.getCoordinates().getY() >= 0);
	}
	

}
