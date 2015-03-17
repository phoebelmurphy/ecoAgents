package view;

import java.awt.GridBagLayout;
import javax.swing.JPanel;

import model.GridModel;

public class GridView extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private GridModel model;
	private GridRowView[] rows;

	public GridView(GridModel model) {
		GridBagLayout layout = new GridBagLayout();
		layout.rowWeights = new double[model.getX()];
		layout.rowHeights = new int[model.getX()];
		for(int i=0; i<model.getX(); i++){
			layout.rowWeights[i] = 1.0;
			layout.rowHeights[i] = 10;
		}

		layout.columnWeights = new double[model.getY()];
		layout.columnWidths = new int[model.getY()];
		
		for(int i=0; i<model.getY(); i++){
			layout.columnWeights[i] = 1.0;
			layout.columnWidths[i] = 10;
		}
		
		setLayout(layout);
		this.model = model;
		init();
	}

	/**
	 * All the heavy work is done here
	 */
	private void init() {
		rows = new GridRowView[model.getY()];
		for (int i = 0; i < rows.length; i++) {
			rows[i] = new GridRowView(model.getRow(i), this, i);
			rows[i].initialiseSquares();
		}
	}
	

}
