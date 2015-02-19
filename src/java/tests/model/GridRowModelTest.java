package tests.model;

import static org.junit.Assert.*;
import model.Coordinates;
import model.GridRowModel;
import model.GridSquareModel;

import org.junit.Before;
import org.junit.Test;

public class GridRowModelTest {

	GridRowModel model;
	int x = 6;//the length of the row
	int y = 8;//the position of the row in the grid
	@Before
	public void setup(){
		model = new GridRowModel(x);
		for(int i=0; i<x; i++ ){
			GridSquareModel square = new GridSquareModel(new Coordinates(i,y));
			model.addSquare(square, i);
		}
	}
	
	
	@Test
	public void testGetSquares(){
		GridSquareModel squares[] = model.getSquares();
		for(int i=0; i<x; i++){
			GridSquareModel gotSquare = model.getSquare(i);
			Coordinates shouldBe = new Coordinates(i,y);
			assertTrue(shouldBe.equals(gotSquare.getCoordinates()));
			assertTrue(shouldBe.equals(squares[i].getCoordinates()));
		}
	}
	
	@Test
	public void testNullSquares(){
		GridSquareModel minusOne = model.getSquare(-1);
		GridSquareModel pastEnd = model.getSquare(x);
		assertNull(minusOne);
		assertNull(pastEnd);
	}
	
	@Test
	public void testGetLength(){
		assertEquals(x, model.getLength());
	}
	
}
