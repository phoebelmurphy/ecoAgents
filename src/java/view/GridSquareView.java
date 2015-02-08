package view;

import java.awt.Color;

import javax.swing.JPanel;

import model.GridSquareModel;

public class GridSquareView extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	GridSquareModel model;
	
	public GridSquareView(GridSquareModel model){
		this.model=model;
		setOpaque(true);
		if(model.isGrass()){
			setBackground(Color.GREEN);
		}
		else {
			setBackground(Color.BLACK);
		}
		repaint();
	}

}
