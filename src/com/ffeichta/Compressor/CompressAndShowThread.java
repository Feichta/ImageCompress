package com.ffeichta.Compressor;

import java.io.IOException;

/**
 * Thread who compress an image and shows it in the imageComponent
 * 
 * @author Fabian
 * 
 */
public class CompressAndShowThread extends Thread {

	/**
	 * Reference to the GUI
	 */
	private JPGCompressorGUI jpgCompressorGUI = null;
	/**
	 * If direction is false, then the quality goes up (back Button), else it
	 * goes down (forward Button)
	 */
	private boolean direction = false;

	public CompressAndShowThread(JPGCompressorGUI jpgCompressorGUI,
			boolean direction) {
		this.jpgCompressorGUI = jpgCompressorGUI;
		this.direction = direction;
		// Kill all threads when closing the application
		this.setDaemon(true);
		this.start();
	}

	@Override
	public void run() {
		try {
			if (!direction) {
				jpgCompressorGUI.actualQuality += 0.01;
			} else {
				jpgCompressorGUI.actualQuality -= 0.01;
			}
			if (jpgCompressorGUI.actualQuality < 0) {
				jpgCompressorGUI.actualQuality = 0.0;
			}
			jpgCompressorGUI.imageComponent.setImage(JPGImageCompress
					.compressImage(jpgCompressorGUI.getBufferedImage(),
							jpgCompressorGUI.actualQuality));
		} catch (IOException e) {
			CompressorException.behandleException(jpgCompressorGUI, e);
		}
	}
}
