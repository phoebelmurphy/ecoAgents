package view;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import model.GridModel;
import model.GridRowModel;
import model.GridSquareModel;
import model.agents.Agent;

public class GridView extends Canvas {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private GridModel model;

	public GridView(GridModel model) {

		this.model = model;
	}

	/**
	 * All the heavy work is done here
	 */
	private void init(Graphics graphics) {
		int numberOfRows = model.getY();
		int numberOfColumns = model.getX();
		int width = getWidth();
		int height = getHeight();
		int squareWidth = width / numberOfRows;
		int squareHeight = height / numberOfColumns;
		for (int i = 0; i < numberOfRows; i++) {
			GridRowModel row = model.getRow(i);
			for(int h=0; h<numberOfColumns; h++) {
				GridSquareModel square = row.getSquare(h);
				graphics.setColor(GrassColour.getColour(square.getGrassHeight()));
				graphics.fillRect((i*squareWidth)-1, (h*squareHeight)-1, squareWidth-2, squareHeight-2);
				
			}
		}
	}
	
	public void paint(Graphics graphics)
	  {
			init(graphics);
			validate();
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
