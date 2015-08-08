package com.ffeichta.Compressor;

/**
 * Class which contains the main() method for the application
 * 
 * @author Fabian
 * 
 */
public class Main {

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new com.ffeichta.Compressor.JPGCompressorGUI();
			}
		});
	}
}
