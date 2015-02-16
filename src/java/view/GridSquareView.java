package view;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

import events.UpdateListener;
import model.GridSquareModel;
import model.agents.Agent;

public class GridSquareView extends JPanel implements UpdateListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	GridSquareModel model;
	
	public GridSquareView(GridSquareModel model){
		this.model=model;
		model.addUpdateListener(this);
		setOpaque(true);
		updateGrass();
	}
	
	private void updateGrass(){
		if(model.isGrass()){
			setBackground(Color.GREEN);
		}
		else {
			setBackground(Color.BLACK);
		}
	}

	private void updateRabbits() {
		removeAll();
		for(Agent agent : model.getAgents()){
			add(new JLabel(agent.getName()));
		}
		validate();
	}
	public void modelUpdated() {
		updateGrass();
		updateRabbits();
		repaint();
	}

}
