package view;


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
	private GridSquareModel model;


	public GridSquareView(GridSquareModel model) {
		this.model = model;
		model.addUpdateListener(this);
		setOpaque(true);
		modelUpdated();
	}

	private void updateGrass() {
		setBackground(GrassColour.getColour(model.getGrassHeight()));
	}

	private void updateRabbits() {
		removeAll();
		for (Agent agent : model.getAgents()) {
			add(new JLabel(agent.getName()));
		}
	}

	public void modelUpdated() {
		updateGrass();
		updateRabbits();
		add(new JLabel(model.getCoordinates().toString()));
		add(new JLabel("height: " + model.getGrassHeight()));
		validate();
		repaint();
	}

}
