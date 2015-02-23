package view;

import java.awt.Color;

public class GrassColour {
	
	private static int grassRColours[] = new int[] { 120, 130, 135, 140, 145, 150,
			155, 160, 170, 180 };
	private static int grassGColours[] = new int[] { 90, 100, 130, 150, 190, 210, 230,
			245, 250, 255 };
	private static int grassBColours[] = new int[] { 30, 30, 35, 40, 45, 50, 55, 60,
			65, 65 };
	
	
	public static Color getColour(int height){
		int r = grassRColours[height];
		int g = grassGColours[height];
		int b = grassBColours[height];
		Color grassColour = new Color(r,g,b);
		return grassColour;
	}
	
	public static int maxHeight() {
		return grassRColours.length;
	}

}
