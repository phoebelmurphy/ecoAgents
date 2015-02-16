package tests.ecoAgents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import jason.asSyntax.Atom;
import jason.asSyntax.Structure;
import model.Coordinates;
import model.agents.Rabbit;

import org.junit.Before;
import org.junit.Test;

import ecoAgents.SquareEcoSystem;

public class SquareEcoSystemTest {
	private SquareEcoSystem model;
	private Rabbit rabbit;
	
	@Before
	public void setup(){
		model = new SquareEcoSystem();
		model.init(new String[] {""});
		rabbit = model.getRabbit("rabbit1");
	}
	
	@Test
	public void testExecuteAction(){
		
		Structure eat = new Structure("eat");
		boolean eatResult = model.executeAction(rabbit.getName(), eat);
		assertTrue(eatResult);
		
		Structure move = new Structure("move");
		move.addTerm(new Atom("u"));
		int oldX = rabbit.getCoordinates().getX();
		int oldY = rabbit.getCoordinates().getY();
		boolean moveResult = model.executeAction(rabbit.getName(), move);
		assertTrue(moveResult);
		Coordinates newCoordinates = rabbit.getCoordinates();
		assertEquals(oldX, newCoordinates.getX());
		assertEquals(oldY+1, newCoordinates.getY());
	}

}
