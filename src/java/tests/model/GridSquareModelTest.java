package tests.model;

import static org.junit.Assert.*;
import model.Coordinates;
import model.GridSquareModel;
import model.agents.Rabbit;

import org.junit.Before;
import org.junit.Test;

public class GridSquareModelTest {

	GridSquareModel model;
	int x=3;
	int y=11;
	
	@Before
	public void setup(){
		model = new GridSquareModel(new Coordinates(x,y));
		model.setGrass(false);
		
	}
	
	@Test
	public void testGrass(){
		assertFalse(model.isGrass());
		model.setGrass(true);
		assertTrue(model.isGrass());
	}
	
	@Test
	public void testCoordinates(){
		Coordinates shouldBe = new Coordinates(x,y);
		assertTrue(shouldBe.equals(model.getCoordinates()));
	}
	
	@Test
	public void testUpdates(){
		TestUpdateListener listener = new TestUpdateListener();
		model.addUpdateListener(listener);
		assertFalse(listener.updated());
		Rabbit testRabbit = new Rabbit("testrabbit", new Coordinates(x,y));
		model.addAgent(testRabbit);
		assertTrue(listener.updated());
		listener.reset();
		model.removeAgent(testRabbit.getName());
		assertTrue(listener.updated());
		
	}
}
