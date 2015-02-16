package tests.agents;

import static org.junit.Assert.*;
import model.Coordinates;
import model.agents.Fox;
import model.agents.Rabbit;

import org.junit.Before;
import org.junit.Test;

public class AgentTest {

	private Rabbit rabbit;
	private Fox fox;
	private int x,y;
	@Before
	public void setup(){
		//there are no significance to these numbers
		x=8;
		y=97;
		rabbit = new Rabbit("testrabbit", new Coordinates(x,y));
		fox = new Fox("testfox", new Coordinates(x,y));
	}
	
	@Test
	public void testSpeed(){
		int rabbitSpeed = rabbit.getSpeed();
		int foxSpeed = fox.getSpeed();
		assertTrue(rabbitSpeed > foxSpeed);
	}
	
	@Test
	public void testName(){
		assertEquals("testrabbit", rabbit.getName());
		assertEquals("testfox", fox.getName());
	}
	
	@Test
	public void testPrey(){
		assertTrue(rabbit.prey());
		assertFalse(fox.prey());
	}
	
	@Test
	public void testCoordinates(){
		Coordinates testCoordinates = new Coordinates(x,y);
		assertTrue(testCoordinates.equals(rabbit.getCoordinates()));
		assertTrue(testCoordinates.equals(fox.getCoordinates()));
	}
}
