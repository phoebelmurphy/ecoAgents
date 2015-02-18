package events;

import jason.jeditplugin.Config;
import jason.jeditplugin.MASLauncherInfraTier;
import jason.mas2j.MAS2JProject;
import jason.mas2j.parser.mas2j;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

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
		// get the mas2j file we selected
		// try to load it
		// if it doesn't load we want to show an error and not close the window
		// if it does load, good, we want to launch the main window
		// the main window needs a link to the mas2j file so it can run the sim
		// TODO work out how to pass args to the Environment init
		mas2j masFile = null;
		String name = fileField.getText().trim();
		try {
			masFile = new mas2j(new FileInputStream(name));

		} catch (FileNotFoundException e) {
			System.out.println("whoops, no file");
			e.printStackTrace();
		}

		if (masFile == null) {
			return;
		}
		// following code adapted from mas2j.java, part of the jason source code
		boolean debugmas = false;
		MASLauncherInfraTier launcher = null;
		// parsing
		try {
			MAS2JProject project = masFile.mas();
			Config.get().fix();
			File file = new File(name);
			File directory = file.getAbsoluteFile().getParentFile();
			project.setDirectory(directory.toString());
			project.setProjectFile(file);
			launcher = project.getInfrastructureFactory().createMASLauncher();
			launcher.setProject(project);
			launcher.writeScripts(debugmas);

		} catch (Exception e) {
			System.err.println("mas2j: parsing errors found... \n" + e);
		}
		// end of mas2j.java code

		int x = 5;
		int y = 5;
		if (launcher == null) {
			return;
		}
		Logger logger = Logger.getLogger("env.logger");
		GridModel grid = new GridModel(x, y, logger);

		mainWindow.addSquares(grid);
		mainWindow.validate();
		mainWindow.repaint();

		new Thread(launcher, "MAS-Launcher").start();

	}

}
