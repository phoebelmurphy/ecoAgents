package tests;
import static org.junit.Assert.*;

import java.util.logging.Logger;

import model.Coordinates;
import model.GridModel;
import model.GridSquareModel;

import org.junit.Before;
import org.junit.Test;

public class GridModelTest {
	
	private GridModel model;
	private Logger logger = Logger.getLogger("testlogger");
	private int x, y;
	
	@Before
	public void setup(){
		x=5;
		y=5;
		model = new GridModel(x, y, logger);
	}
	
	@Test
	public void testModelInitialised(){
		assertEquals(x, model.getX());
		assertEquals(y, model.getY());
		
	}
	@Test
	public void testGetSquare(){
		for(int i=0; i<x; i++){
			for(int n=0; n<y; n++){
				GridSquareModel square = model.getSquare(i, n);
				Coordinates squareCoords = square.getCoordinates();
				Coordinates testCoords = new Coordinates(i,n);
				assertTrue("Square should be in correct place", squareCoords.equals(testCoords));
				assertTrue("Square should have grass", square.isGrass());
			}
		}
	}
	
//TODO work out a way to test moving an agent!
	
	

}