package events;

import infrastructure.CustomRunMAS;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;




import javax.swing.JFileChooser;
import javax.swing.JTextField;

import model.GridModel;
import view.MainWindow;

public class ButtonListener implements ActionListener {
	private MainWindow mainWindow;

	public ButtonListener(MainWindow frame) {
		System.out.println("created");
		mainWindow = frame;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Run")) {
			System.out.println("running");
			run();
		} else if (e.getActionCommand().equals("Browse")) {
			browse();
		}
		System.out.println("boop");
	}

	private void browse() {
		JFileChooser chooser = new JFileChooser();
		int returnVal = chooser.showOpenDialog(mainWindow);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			JTextField fileField = mainWindow.getFileField();
			fileField.setText(file.getPath());
		}
	}

	private void run() {
		
		JTextField fileField = mainWindow.getFileField();
		if (fileField == null || fileField.getText().isEmpty()) {
			// TODO show error
			System.out.println("no file!");
			return;
		}
		//TODO pull the number from user settings
		GridModel.createInstance(5, 5);
		GridModel grid= GridModel.getInstance();

		mainWindow.addSquares(grid);
		mainWindow.validate();
		mainWindow.repaint();
		CustomRunMAS mas = new CustomRunMAS(new String[] {fileField.getText()});
		new Thread(mas, "AgentSpeakSystem").start();

	}

}
