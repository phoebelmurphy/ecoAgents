package view;

import java.awt.GridBagLayout;
import javax.swing.JPanel;

import model.GridModel;

public class GridView extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	GridModel model;
	GridRowView[] rows;

	public GridView(GridModel model) {
		GridBagLayout layout = new GridBagLayout();
		layout.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0};
		layout.columnWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0};
		layout.columnWidths = new int[] {10, 10, 10, 10, 10};
		layout.rowHeights = new int[] {10, 10, 10, 10, 10};
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
