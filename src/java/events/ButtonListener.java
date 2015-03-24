package events;


import infrastructure.jason.RunCentralisedMAS;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.Coordinates;
import model.GridModel;
import view.MainWindow;

public class ButtonListener implements ActionListener, ChangeListener {
	private MainWindow mainWindow;

	public ButtonListener(MainWindow frame) {
		mainWindow = frame;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Run")) {
			updateGrid();
			run();
		}
	}
	
	public void stateChanged(ChangeEvent arg0) {
		updateGrid();
		
	}
	
	private void updateGrid() {
		Coordinates gridsize = mainWindow.getCoordinates();
		
		//I am using a singleton as there doesn't seem to be another way to
		//create the grid model outside of the RunCentralisedMAS
		//and still let the SquareEcoSystem get it
		//This is because RunCentralisedMAS automatically launches
		//the Environment we're using (SquareEcoSystem)
		GridModel.createInstance(gridsize.getX(), gridsize.getY());
		GridModel grid= GridModel.getInstance();

		mainWindow.addSquares(grid);
		mainWindow.validate();
		mainWindow.repaint();
	}

	private void run() {
		

		
		//RunCentralisedMAS mas = new RunCentralisedMAS(new String[] {fileField.getText()});
		RunCentralisedMAS mas = new RunCentralisedMAS(new String[] {"C:\\Users\\phoebe\\workspace\\ecoAgents\\ecoAgents.mas2j"});
		new Thread(mas, "AgentSpeakSystem").start();

	}


}
