package model;

public class GridRowModel {
	private GridSquareModel[] squares;
	private int length;
	public GridRowModel(int length){
		squares = new GridSquareModel[length];
		this.length = length;
	}
	
	public int getLength(){
		return length;
	}
	
	public GridSquareModel[] getSquares(){
		return squares;
	}
	public GridSquareModel getSquare(int position){
		if(position>=0 && position < length){
			return squares[position];
		}
		return null;
	}
	
	public void addSquare(GridSquareModel square, int position){
		squares[position] = square;
	}


}
