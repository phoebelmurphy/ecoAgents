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
	private GridSquareModel model;
	private int grassRColours[] = new int[] { 120, 130, 135, 140, 145, 150,
			155, 160, 170, 180 };
	private int grassGColours[] = new int[] { 90, 100, 130, 150, 190, 210, 230,
			150, 270, 280 };
	private int grassBColours[] = new int[] { 30, 30, 35, 40, 45, 50, 55, 60,
			65, 65 };

	public GridSquareView(GridSquareModel model) {
		this.model = model;
		model.addUpdateListener(this);
		setOpaque(true);
		modelUpdated();
	}

	private void updateGrass() {
		int grassHeight = model.getGrassHeight();
		Color grassColour = new Color(grassRColours[grassHeight],
				grassGColours[grassHeight], grassBColours[grassHeight]);
		setBackground(grassColour);
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
		validate();
		repaint();
	}

}
