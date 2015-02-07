package ecoAgents;

import java.util.Random;

public class Grid {
	//represents a grid, like this
	//@formatter:off
	/* 
	 *    [ ] [ ] [ ] [ ] [ ]  <- one GridRow of length x
	 *    [ ] [ ] [ ] [ ] [ ]
	 * ^  [ ] [ ] [ ] [ ] [ ]
	 * |  [ ] [ ] [ ] [ ] [ ]
	 * y  [ ] [ ] [ ] [ ] [ ]
	 *    x ->
	 */
	//@formatter:on
	
	
	private int x;
	private int y;
	private GridRow[] rows; //list of y GridRows
	private Random random = new Random();
	
	public Grid(int x, int y){
		this.x = x;
		this.y = y;
		rows = new GridRow[y];
		for(int i=0; i<y; i++){
			rows[i] = new GridRow(x);
			for(int n=0; n<x; n++){
				Coordinates coords = new Coordinates(i, n);
				GridSquare square = new GridSquare(coords);
				rows[i].addSquare(square, n);
			}
		}
		growGrass();
		
	}
	
	public GridSquare getSquare(Coordinates coordinates) {
		return getSquare(coordinates.getX(), coordinates.getY());
	}
	
	public GridSquare getSquare(int x, int y){
		if (x >= this.x || y >= this.y || x<0 || y<0)  {
			return null;
		}
		return rows[y].getSquare(x);
	}
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	/**
	 * Flower gleam and glow
	 * Let your magic shine
	 * Make there be more grass
	 * It is growing time
	 * Iterates through all the grid squares and randomly adds grass to some.
	 * Maybe later grass will spread from one square to the next.
	 */
	public void growGrass(){
		for(int i=0; i<y; i++){
			GridRow currentRow = rows[i];
			for(int n=0; n<x; n++){
				GridSquare currentSquare = currentRow.getSquare(n);
				if(!currentSquare.isGrass()){
					if(random.nextInt(100) < 20){
						currentSquare.setGrass(true);
					}
				}
			}
		}
	}

}
