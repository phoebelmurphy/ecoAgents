package view;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import events.UpdateListener;
import model.GridModel;
import model.GridRowModel;
import model.GridSquareModel;
import model.agents.Agent;

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
	private boolean initialised = false;
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
		squareWidth = width / numberOfColumns;
		squareHeight = height / numberOfRows;
		
	}
	
	@Override
	public void paintComponent(Graphics graphics)
	  {
		if(!initialised){
			init();
			initialised = true;
		}
		removeAll();
		for (int y = 0; y < numberOfRows; y++) {
			for(int x=0; x<numberOfColumns; x++) {
				GridSquareModel square = model.getSquare(x,y);
				graphics.setColor(GrassColour.getColour(square.getGrassHeight()));
				graphics.fillRect((x*squareWidth), (y*squareHeight), squareWidth-1, squareHeight-1);
				graphics.setColor(Color.BLACK);
				for(Agent agent : square.getAgents()){
					graphics.drawString(agent.getName(), x*squareWidth,y*squareHeight);
				}
				
			}
		}
			repaint();
	   }



	public void modelUpdated() {
		//TODO pobably doesnt even work because of multithreading
		initialised = false;
	}

}
