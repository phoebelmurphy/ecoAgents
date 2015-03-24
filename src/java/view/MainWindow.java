package view;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JCheckBox;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SpinnerNumberModel;

import model.Coordinates;
import model.GridModel;

import javax.swing.JButton;

import events.ButtonListener;

import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JSpinner;



public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel gridpane;
	private ButtonListener listener;
	private JSpinner ySpinner;
	private JSpinner xSpinner;

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		listener = new ButtonListener(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1014, 708);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		
		JPanel controlpane = new JPanel();
		getContentPane().add(controlpane, BorderLayout.EAST);
		GridBagLayout gbl_controlpane = new GridBagLayout();
		gbl_controlpane.columnWidths = new int[]{119, 109, 0};
		gbl_controlpane.rowHeights = new int[]{25, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_controlpane.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_controlpane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		controlpane.setLayout(gbl_controlpane);
		
		JLabel lblRabbitCount = new JLabel("Rabbit count");
		GridBagConstraints gbc_lblRabbitCount = new GridBagConstraints();
		gbc_lblRabbitCount.insets = new Insets(0, 0, 5, 5);
		gbc_lblRabbitCount.gridx = 0;
		gbc_lblRabbitCount.gridy = 1;
		controlpane.add(lblRabbitCount, gbc_lblRabbitCount);
		
		JComboBox<Integer> rabbitcount = new JComboBox<Integer>();
		rabbitcount.setModel(new DefaultComboBoxModel<Integer>(new Integer[] {1,2,3,4,5}));
		GridBagConstraints gbc_rabbitcount = new GridBagConstraints();
		gbc_rabbitcount.insets = new Insets(0, 0, 5, 0);
		gbc_rabbitcount.fill = GridBagConstraints.HORIZONTAL;
		gbc_rabbitcount.gridx = 1;
		gbc_rabbitcount.gridy = 1;
		controlpane.add(rabbitcount, gbc_rabbitcount);
		
		JButton btnRun = new JButton("Run");
		btnRun.addActionListener(listener);
		
		JLabel lblFoxCount = new JLabel("Fox count");
		GridBagConstraints gbc_lblFoxCount = new GridBagConstraints();
		gbc_lblFoxCount.insets = new Insets(0, 0, 5, 5);
		gbc_lblFoxCount.gridx = 0;
		gbc_lblFoxCount.gridy = 2;
		controlpane.add(lblFoxCount, gbc_lblFoxCount);
		
		JComboBox<Integer> foxCount = new JComboBox<Integer>();
		foxCount.setModel(new DefaultComboBoxModel<Integer>(new Integer[] {1,2,3,4,5}));
		GridBagConstraints gbc_foxCount = new GridBagConstraints();
		gbc_foxCount.insets = new Insets(0, 0, 5, 0);
		gbc_foxCount.fill = GridBagConstraints.HORIZONTAL;
		gbc_foxCount.gridx = 1;
		gbc_foxCount.gridy = 2;
		controlpane.add(foxCount, gbc_foxCount);
		
		JLabel xLabel = new JLabel("Horizontal size");
		GridBagConstraints gbc_xLabel = new GridBagConstraints();
		gbc_xLabel.insets = new Insets(0, 0, 5, 5);
		gbc_xLabel.gridx = 0;
		gbc_xLabel.gridy = 3;
		controlpane.add(xLabel, gbc_xLabel);
		
		xSpinner = new JSpinner(new SpinnerNumberModel());
		GridBagConstraints gbc_xSpinner = new GridBagConstraints();
		gbc_xSpinner.insets = new Insets(0, 0, 5, 0);
		gbc_xSpinner.gridx = 1;
		gbc_xSpinner.gridy = 3;
		xSpinner.setValue(10);
		controlpane.add(xSpinner, gbc_xSpinner);
		
		JLabel lblVerticalSize = new JLabel("Vertical size");
		GridBagConstraints gbc_lblVerticalSize = new GridBagConstraints();
		gbc_lblVerticalSize.insets = new Insets(0, 0, 5, 5);
		gbc_lblVerticalSize.gridx = 0;
		gbc_lblVerticalSize.gridy = 4;
		controlpane.add(lblVerticalSize, gbc_lblVerticalSize);
		
		ySpinner = new JSpinner(new SpinnerNumberModel());
		GridBagConstraints gbc_ySpinner = new GridBagConstraints();
		gbc_ySpinner.insets = new Insets(0, 0, 5, 0);
		gbc_ySpinner.gridx = 1;
		gbc_ySpinner.gridy = 4;
		ySpinner.setValue(10);
		controlpane.add(ySpinner, gbc_ySpinner);
		GridBagConstraints gbc_btnRun = new GridBagConstraints();
		gbc_btnRun.gridx = 1;
		gbc_btnRun.gridy = 20;
		controlpane.add(btnRun, gbc_btnRun);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
	}

	public void addSquares(GridModel model){
		gridpane = new GridView(model);
		getContentPane().add(gridpane, BorderLayout.CENTER);
	}
	
	public Coordinates getCoordinates() {
		int x = (Integer)xSpinner.getValue();
		int y = (Integer)ySpinner.getValue();
		return new Coordinates(x,y);
	}
}
