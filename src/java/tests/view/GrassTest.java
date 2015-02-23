package tests.view;

import static org.junit.Assert.*;

import java.awt.Color;

import org.junit.Test;

import view.GrassColour;

public class GrassTest {

	
	@Test
	public void testColours(){
		for(int i=0; i<10; i++){
			Color colour = GrassColour.getColour(i);
			assertTrue(colour != null);
		}
	}
}
