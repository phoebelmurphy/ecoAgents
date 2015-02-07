package ecoAgents;

public class GridRow {
	private GridSquare[] squares;
	private int length;
	public GridRow(int length){
		squares = new GridSquare[length];
		this.length = length;
	}
	
	public int getLength(){
		return length;
	}
	
	public GridSquare[] getSquares(){
		return squares;
	}
	public GridSquare getSquare(int position){
		return squares[position];
	}
	
	public void addSquare(GridSquare square, int position){
		squares[position] = square;
	}


}
