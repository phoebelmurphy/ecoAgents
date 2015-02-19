package tests.model;
import static org.junit.Assert.*;

import model.Coordinates;
import model.GridModel;
import model.GridSquareModel;

import org.junit.Before;
import org.junit.Test;

public class GridModelTest {
	
	private GridModel model;
	private int x, y;
	
	@Before
	public void setup(){
		x=5;
		y=5;
		GridModel.createInstance(x, y);
		model = GridModel.getInstance();
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
	
	@Test
	public void testGetNullSquares(){
		GridSquareModel nullX = model.getSquare(x, y-1);
		GridSquareModel nullY = model.getSquare(x-1, y);
		GridSquareModel nullBoth = model.getSquare(x,y);
		GridSquareModel negativeX = model.getSquare(-1, 0);
		GridSquareModel negativeY = model.getSquare(0, -1);
		assertNull(nullX);
		assertNull(nullY);
		assertNull(nullBoth);
		assertNull(negativeX);
		assertNull(negativeY);
	}
	
//TODO work out a way to test moving an agent!
	
//TODO test growing grass

}
