package tests;

import static org.junit.Assert.*;
import model.Coordinates;

import org.junit.Test;

public class TestCoordinates {
	
	@Test
	public void testEquals(){
		//the numbers in this test were not chosen for
		//any particular reason
		int x =3;
		int y=7;
		Coordinates one = new Coordinates(x,y);
		Coordinates two = new Coordinates(x,y);
		assertTrue(one.equals(two));
		Coordinates three = new Coordinates(x-1, y);
		assertFalse(one.equals(three));
	}

	@Test
	public void testCharToCoordinates() {
		//test that the conversion between chars representing cardinal
		//directions and coordinates representing those directions
		//works correctly
		Coordinates up, down, left, right;
		up = new Coordinates(0, 1);
		down = new Coordinates(0, -1);
		left = new Coordinates(-1, 0);
		right = new Coordinates(1, 0);
		assertTrue(up.equals(Coordinates.charToCoordinates('u')));
		assertTrue(down.equals(Coordinates.charToCoordinates('d')));
		assertTrue(left.equals(Coordinates.charToCoordinates('l')));
		assertTrue(right.equals(Coordinates.charToCoordinates('r')));
	}
	
	@Test
	public void testToString(){
		//these numbers were not chosen for any particular reason
		//they were hardcoded rather than using variables to make
		//it easier to see how the string we are comparing to
		//should look
		Coordinates coordinates = new Coordinates(4,3);
		assertEquals("(4,3)", coordinates.toString());
	}

	@Test
	public void testAdd(){
		//again, these numbers were not chosen for any special reason
		//although obviously dy being 0 means y won't change
		//which we do test for
		int x = 19;
		int y=3;
		int dx = 1;
		int dy = 0;
		Coordinates origin = new Coordinates(x,y);
		Coordinates movement = new Coordinates(dx,dy);
		Coordinates expectedDestination = new Coordinates(x+dx, y+dy);
		
		//create new coordinates which are sum of origin and movement
		Coordinates staticResult = Coordinates.add(origin, movement);
		//check we got the correct result
		assertTrue(staticResult.equals(expectedDestination));
		//check origin wasn't altered
		assertEquals(origin.getX(), x);
		//now add movement to the origin object
		origin.add(movement);
		//this SHOULD alter origin
		assertTrue(origin.equals(expectedDestination));
		//remember y should stay the same as dy is 0
		assertEquals(origin.getY(), y);
	}
	
	
}
