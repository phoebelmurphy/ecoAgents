package view;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import model.GridRowModel;

public class GridRowView {
	private GridSquareView[] squares;
	private GridRowModel model;
	private GridView view;
	private int verticalPosition;

	/**
	 * This is a simple class to make handling the 2d array easier
	 * 
	 * @param model
	 *            The corresponding GridRowModel representing the same row.
	 * @param view
	 *            The view this row belongs to.
	 */
	public GridRowView(GridRowModel model, GridView view, int verticalPosition) {
		squares = new GridSquareView[model.getLength()];
		this.model=model;
		this.view = view;
		this.verticalPosition = verticalPosition;
	}

	public int getLength() {
		return model.getLength();
	}

	public GridSquareView[] getSquares() {
		return squares;
	}

	public GridSquareView getSquare(int position) {
		return squares[position];
	}

	public void addSquare(GridSquareView square, int position) {
		if(square==null){
			return;
		}
		if (squares[position] != null) { // TODO will this be initialised to null?
			view.remove(squares[position]);
			squares[position]=null;
		}
		squares[position] = square;
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(0, 0, 5, 5);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = position;
		constraints.gridy = verticalPosition;
		view.add(square, constraints);
	}
	
	public void initialiseSquares(){
		for(int i=0; i<squares.length; i++){
			GridSquareView square = new GridSquareView(model.getSquare(i));
			addSquare(square, i);
		}
	}

}
