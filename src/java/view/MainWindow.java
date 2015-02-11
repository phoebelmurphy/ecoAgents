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

import model.GridModel;



public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JPanel gridpane;

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1014, 708);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		
		JPanel controlpane = new JPanel();
		getContentPane().add(controlpane, BorderLayout.EAST);
		GridBagLayout gbl_controlpane = new GridBagLayout();
		gbl_controlpane.columnWidths = new int[]{119, 109, 0};
		gbl_controlpane.rowHeights = new int[]{25, 0, 0, 0};
		gbl_controlpane.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_controlpane.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
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
		
		JLabel lblIncludeFox = new JLabel("Include fox?");
		GridBagConstraints gbc_lblIncludeFox = new GridBagConstraints();
		gbc_lblIncludeFox.insets = new Insets(0, 0, 0, 5);
		gbc_lblIncludeFox.gridx = 0;
		gbc_lblIncludeFox.gridy = 2;
		controlpane.add(lblIncludeFox, gbc_lblIncludeFox);
		
		JCheckBox includeFoxes = new JCheckBox("");
		includeFoxes.setSelected(true);
		GridBagConstraints gbc_includeFoxes = new GridBagConstraints();
		gbc_includeFoxes.anchor = GridBagConstraints.NORTH;
		gbc_includeFoxes.gridx = 1;
		gbc_includeFoxes.gridy = 2;
		controlpane.add(includeFoxes, gbc_includeFoxes);
		
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
}