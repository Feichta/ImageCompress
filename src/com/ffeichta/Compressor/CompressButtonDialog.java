package com.ffeichta.Compressor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultFormatter;

/**
 * Shows the Frame when the button "Compress" is clicked
 * 
 * @author Fabian
 * 
 */
public class CompressButtonDialog extends JDialog {

	/**
	 * Components for the GUI
	 */
	protected JLabel labelQuality;
	protected JSpinner spinnerFrom;
	protected JLabel labelTo;
	protected JSpinner spinnerTo;
	protected JLabel labelPercent;
	protected JLabel labelSteps;
	protected JSlider slider;
	protected JRadioButton radioCurrent;
	protected JRadioButton radioHere;
	protected JButton filechooser;
	protected JTextField textFieldPath;
	protected JCheckBox checkBoxSubdirectory;
	protected JPanel panelSouth;
	protected JPanel panelNorth = null;
	protected JLabel labelNumber;
	private JButton start;
	private JButton cancel;
	private JFormattedTextField jtff = null;
	private ButtonGroup group = null;

	/**
	 * Reference to the GUI
	 */
	protected JPGCompressorGUI jpgCompressorGUI = null;

	public CompressButtonDialog(JPGCompressorGUI jpgCompressorGUI,
			final String path) {
		super(jpgCompressorGUI);
		this.jpgCompressorGUI = jpgCompressorGUI;
		setTitle("Compress");
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setSize(467, 356);
		setLocationRelativeTo(null);
		setResizable(false);
		setModal(true);
		getContentPane().setLayout(null);

		panelNorth = new JPanel();
		panelNorth.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "Quality options",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelNorth.setBounds(10, 16, 443, 90);
		panelNorth.setLayout(null);
		getContentPane().add(panelNorth);

		labelQuality = new JLabel("Quality: from");
		labelQuality.setBounds(10, 26, 81, 14);
		panelNorth.add(labelQuality);

		CompressDialogChangeListener changeListener = new CompressDialogChangeListener(
				this);

		spinnerFrom = new JSpinner();
		spinnerFrom.setModel(new SpinnerNumberModel(0, 0, 99, 1));
		spinnerFrom.setBounds(82, 24, 48, 20);
		panelNorth.add(spinnerFrom);
		spinnerFrom.addChangeListener(changeListener);
		((JSpinner.DefaultEditor) spinnerFrom.getEditor()).getTextField()
				.addKeyListener(new KeyAdapter() {
					@Override
					public void keyTyped(KeyEvent e) {
						double numTemp = (getTo() - getFrom())
								/ (double) slider.getValue();
						int numberPictures = (int) Math.abs(Math.ceil(numTemp));
						String msg = slider.getValue() + "    ("
								+ numberPictures + " pictures)";
						labelNumber.setText(msg);
					}
				});
		JFormattedTextField jtf = ((JSpinner.DefaultEditor) spinnerFrom
				.getEditor()).getTextField();
		DefaultFormatter formatter = (DefaultFormatter) jtf.getFormatter();
		formatter.setCommitsOnValidEdit(true);

		labelTo = new JLabel("% to");
		labelTo.setBounds(131, 26, 29, 14);
		panelNorth.add(labelTo);

		spinnerTo = new JSpinner();
		spinnerTo.setModel(new SpinnerNumberModel(100, 1, 100, 1));
		spinnerTo.setBounds(158, 24, 48, 20);
		panelNorth.add(spinnerTo);
		spinnerTo.addChangeListener(changeListener);
		((JSpinner.DefaultEditor) spinnerTo.getEditor()).getTextField()
				.addKeyListener(new KeyAdapter() {
					@Override
					public void keyTyped(KeyEvent e) {
						double numTemp = (getTo() - getFrom())
								/ (double) slider.getValue();
						int numberPictures = (int) Math.abs(Math.ceil(numTemp));
						String msg = slider.getValue() + "    ("
								+ numberPictures + " pictures)";
						labelNumber.setText(msg);
					}
				});
		jtff = ((JSpinner.DefaultEditor) spinnerTo.getEditor()).getTextField();
		DefaultFormatter formatterr = (DefaultFormatter) jtff.getFormatter();
		formatterr.setCommitsOnValidEdit(true);

		labelSteps = new JLabel("Step size:");
		labelSteps.setBounds(10, 57, 57, 14);
		panelNorth.add(labelSteps);

		slider = new JSlider();
		slider.setPaintLabels(true);
		slider.setMaximum(10);
		slider.setValue(1);
		slider.setMinimum(1);
		slider.setToolTipText("Select the size of steps between the two percentages");
		slider.setBounds(68, 53, 200, 26);
		slider.addChangeListener(changeListener);
		panelNorth.add(slider);

		labelPercent = new JLabel("%");
		labelPercent.setBounds(208, 26, 22, 14);
		panelNorth.add(labelPercent);

		labelNumber = new JLabel("");
		labelNumber.setBounds(273, 58, 105, 14);
		double numTemp = (getTo() - getFrom()) / (double) slider.getValue();
		int numberPictures = (int) Math.abs(Math.ceil(numTemp));
		String msg = slider.getValue() + "    (" + numberPictures
				+ " pictures)";
		labelNumber.setText(msg);
		panelNorth.add(labelNumber);

		panelSouth = new JPanel();
		panelSouth.setBorder(new TitledBorder(null, "Save options",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelSouth.setBounds(10, 125, 443, 164);
		panelSouth.setLayout(null);
		getContentPane().add(panelSouth);

		radioCurrent = new JRadioButton(
				"Save compressed pictures in the directory of the current picture");
		radioCurrent.setSelected(true);
		radioCurrent.setBounds(8, 25, 402, 25);
		radioCurrent.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (radioCurrent.isSelected()) {
					textFieldPath.setEnabled(false);
					filechooser.setEnabled(false);
				}

			}
		});
		panelSouth.add(radioCurrent);

		radioHere = new JRadioButton("Save compressed pictures here");
		radioHere.setBounds(8, 60, 202, 25);
		radioHere.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (radioHere.isSelected()) {
					textFieldPath.setEnabled(true);
					filechooser.setEnabled(true);
				}

			}
		});
		panelSouth.add(radioHere);

		group = new ButtonGroup();
		group.add(radioCurrent);
		group.add(radioHere);

		textFieldPath = new JTextField();
		textFieldPath.setBounds(28, 88, 278, 22);
		textFieldPath.setColumns(10);
		textFieldPath.setText(path);
		textFieldPath.setEnabled(false);
		panelSouth.add(textFieldPath);

		filechooser = new JButton("Choose directory");
		filechooser.setBounds(310, 88, 123, 23);
		filechooser.setEnabled(false);
		filechooser.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(path);
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(CompressButtonDialog.this) == JFileChooser.APPROVE_OPTION) {
					textFieldPath.setText(chooser.getSelectedFile().toString()
							+ "\\");
				}
			}
		});
		panelSouth.add(filechooser);

		checkBoxSubdirectory = new JCheckBox("Create subdirectory");
		checkBoxSubdirectory.setSelected(true);
		checkBoxSubdirectory.setBounds(8, 121, 141, 25);
		panelSouth.add(checkBoxSubdirectory);

		start = new JButton("Start");
		start.setBounds(137, 296, 89, 23);
		this.getRootPane().setDefaultButton(start);
		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				boolean error = false;
				if (getFrom() >= getTo()
						|| Integer.valueOf(labelNumber.getText().substring(
								labelNumber.getText().indexOf("(") + 1,
								labelNumber.getText().lastIndexOf(" "))) <= 0
						|| getTextFieldText() == null) {
					error = true;
					if (getFrom() >= getTo()) {
						int valueToSet = Integer.valueOf((spinnerFrom
								.getValue().toString())) + 1;
						spinnerTo.setValue(valueToSet);
					}
				}
				if (!error) {
					setVisible(false);
					dispose();

					CompressAndSaveThread thread = new CompressAndSaveThread(
							CompressButtonDialog.this,
							CompressButtonDialog.this.jpgCompressorGUI);
					try {
						thread.join();
					} catch (InterruptedException e1) {
						CompressorException.behandleException(
								CompressButtonDialog.this, e1);
					}
					DialogProgressBar d = new DialogProgressBar(
							CompressButtonDialog.this.jpgCompressorGUI, thread
									.getThreadList(), getTo() - getFrom()
									/ slider.getValue());
					d.setVisible(true);
					d.dispose();
				} else {
					JOptionPane
							.showMessageDialog(
									CompressButtonDialog.this,
									"The entered values are not correct.\n\n\"From\" must be "
											+ "less than \"to\", the number of pictures can't be zero\nand "
											+ "if selected, the entered path must be valid.",
									"Error", JOptionPane.INFORMATION_MESSAGE);
				}

			}
		});
		getContentPane().add(start);

		cancel = new JButton("Cancel");
		cancel.setBounds(231, 296, 98, 23);
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		getContentPane().add(cancel);
	}

	/**
	 * Returns the value from the Spinner "from"
	 * 
	 * @return the endered value in type int
	 */
	public int getFrom() {
		int ret = 0;
		double value = Math.rint(Double.parseDouble(spinnerFrom.getValue()
				.toString()));
		ret = (int) value;
		return ret;
	}

	/**
	 * Returns the value from the Spinner "to"
	 * 
	 * @return the endered value in type int
	 */
	public int getTo() {
		int ret = 0;
		double value = Math.rint(Double.parseDouble(spinnerTo.getValue()
				.toString()));
		ret = (int) value;
		return ret;
	}

	/**
	 * Returns the entered Path. If it is not entered or it doesn't exist, the
	 * return is null
	 * 
	 * @return Path
	 */
	public String getTextFieldText() {
		String ret = null;
		if (textFieldPath.getText() != null
				&& textFieldPath.getText().length() > 0) {
			File f = new File(textFieldPath.getText());
			if (f.isDirectory()) {
				ret = textFieldPath.getText();
			}
		}
		return ret;
	}

	/**
	 * 
	 * Checks if the RadioButton is selected
	 * 
	 * @return enum from type Options
	 */
	public Options getOption() {
		Options ret = Options.CURRENTDIRETORY;
		if (radioHere.isSelected()) {
			ret = Options.NOTCURRENTDIRECTORY;
		}
		return ret;
	}

	/**
	 * Checks if the user want to save the compressed pictures in a subdirectory
	 * 
	 * @return
	 */
	public boolean createSubdirectory() {
		return checkBoxSubdirectory.isSelected();
	}

	// Enum which contains currentdirectory and notcurrentdirectory
	public enum Options {
		CURRENTDIRETORY, NOTCURRENTDIRECTORY
	}

	public int getSliderValue() {
		return slider.getValue();
	}
}
