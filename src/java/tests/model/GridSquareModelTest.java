package tests.model;

import static org.junit.Assert.*;
import model.Coordinates;
import model.GridSquareModel;

import org.junit.Before;
import org.junit.Test;

public class GridSquareModelTest {

	GridSquareModel model;
	int x=3;
	int y=11;
	
	@Before
	public void setup(){
		model = new GridSquareModel(new Coordinates(x,y));		
	}
	
	@Test
	public void testGrass(){
		int grassHeight = model.getGrassHeight();
		assertTrue(grassHeight > 0);
		int grassTaken = model.takeGrass(1);
		assertEquals(grassHeight - grassTaken, model.getGrassHeight());
		
	}
	
	@Test
	public void testCoordinates(){
		Coordinates shouldBe = new Coordinates(x,y);
		assertTrue(shouldBe.equals(model.getCoordinates()));
	}
	
	
}
