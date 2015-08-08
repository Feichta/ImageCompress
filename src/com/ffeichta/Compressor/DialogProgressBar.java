package com.ffeichta.Compressor;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

/**
 * Dialog shows the progressBar while images are compressed and saved
 * 
 * @author Fabian
 * 
 */
public class DialogProgressBar extends JDialog {

	private JProgressBar progressBar;
	private Vector<Thread> threadList = null;

	public DialogProgressBar(JFrame owner, final Vector<Thread> threadList,
			int anzahlThreads) {
		super(owner);
		this.threadList = threadList;
		setTitle("Saving pictures");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(423, 122);
		setLocationRelativeTo(null);
		setModal(true);
		addWindowListener(new WindowListener());
		getContentPane().setLayout(null);

		UIManager.put("ProgressBar.selectionBackground", Color.GRAY);
		UIManager.put("ProgressBar.selectionForeground", Color.GRAY);
		progressBar = new JProgressBar();
		progressBar.setForeground(Color.ORANGE);
		progressBar.setBackground(Color.GRAY);
		progressBar.setStringPainted(true);
		progressBar.setBounds(10, 10, 387, 30);
		progressBar.setMinimum(0);
		progressBar.setMaximum(anzahlThreads);
		progressBar.setValue(0);
		getContentPane().add(progressBar);

		JButton cancel = new JButton("Cancel");
		this.getRootPane().setDefaultButton(cancel);
		cancel.setBounds(159, 50, 89, 23);
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						abort();
					}
				}).start();

			}
		});
		getContentPane().add(cancel);

		/**
		 * This Thread starts when instantiating this Dialog and updates every
		 * 0.5 seconds the ProgressBar. When compressing and saving is finished
		 * this Thread kills himself
		 */
		new Thread() {
			@Override
			public void run() {
				while (!isInterrupted()) {
					if (threadList != null) {
						for (int i = 0; i < threadList.size(); i++) {
							if (threadList.get(i).getState() == Thread.State.TERMINATED) {
								progressBar.setValue(100 - threadList.size());
								threadList.remove(i);
								if (threadList.size() == 0) {
									interrupt();
									DialogProgressBar.this.setVisible(false);
								}
							}
						}
						try {
							sleep(500);
						} catch (InterruptedException e) {
							interrupt();
						}
					}
				}
			}
		}.start();

	}

	/**
	 * Listener for the Close-Button. Does the same as when the "Cancel" button
	 * is clicked
	 * 
	 * @author Fabian
	 * 
	 */
	private class WindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					abort();
				}
			}).start();
		}
	}

	/**
	 * Asks the user if he wants to stop compressing.
	 * 
	 * ATTENTION!!!
	 * 
	 * Uses the stop() method of class Thread which doesn't stop threads safely
	 */
	public void abort() {
		int result = JOptionPane
				.showConfirmDialog(
						DialogProgressBar.this,
						"Do you want to stop compressing pictures?\n\nWARNING: After that, your filesystem could contain incorrect files!",
						"Abort process", JOptionPane.YES_NO_CANCEL_OPTION);
		if (result == JOptionPane.YES_OPTION) {
			for (int i = 0; i < threadList.size(); i++) {
				threadList.get(i).stop();
			}
			setVisible(false);
			dispose();
		}
	}
}
