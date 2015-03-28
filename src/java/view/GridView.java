package view;

import java.awt.Canvas;
import java.awt.Graphics;




import javax.swing.JPanel;

import events.UpdateListener;
import model.GridModel;
import model.GridRowModel;
import model.GridSquareModel;

public class GridView extends JPanel implements UpdateListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private GridModel model;
	private int squareHeight;
	private int squareWidth;
	private int numberOfRows;
	private int numberOfColumns;

	public GridView(GridModel model) {
		this.model = model;
		//init();
	}

	/**
	 * All the heavy work is done here
	 */
	private void init() {
		numberOfRows = model.getY();
		numberOfColumns = model.getX();
		int width = getWidth();
		int height = getHeight();
		squareWidth = width / numberOfRows;
		squareHeight = height / numberOfColumns;
		
	}
	
	@Override
	public void paintComponent(Graphics graphics)
	  {
		init();
		removeAll();
		for (int i = 0; i < numberOfRows; i++) {
			GridRowModel row = model.getRow(i);
			for(int h=0; h<numberOfColumns; h++) {
				GridSquareModel square = row.getSquare(h);
				graphics.setColor(GrassColour.getColour(square.getGrassHeight()));
				graphics.fillRect((i*squareWidth)-1, (h*squareHeight)-1, squareWidth-2, squareHeight-2);
				
			}
		}
			repaint();
	   }
	
	public void updateGrass(){
	
		//setBackground(GrassColour.getColour(model.getGrassHeight()));
	}

//	private void updateRabbits() {
//		removeAll();
//		for (Agent agent : model.getAgents()) {
//			add(new JLabel(agent.getName()));
//		}
//		for (Agent agent : model.getDeadAgents()) {
//			add(new JLabel(agent.getName()+"dead"));
//		}
//	}

	public void modelUpdated() {
//		updateGrass();
//		updateRabbits();
//		add(new JLabel(model.getCoordinates().toString()));
//		add(new JLabel("height: " + model.getGrassHeight()));
//		validate();
//		repaint();
	}

}
