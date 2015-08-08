package com.ffeichta.Compressor;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Thread who compress an image and saves the result in a BufferedImage
 * 
 * @author Fabian
 * 
 */
public class PlayThread implements Runnable {

	private JPGCompressorGUI jpgCompressorGUI = null;
	private BufferedImage resultImage = null;
	private Thread compressThread = null;

	public PlayThread(JPGCompressorGUI jpgCompressorGUI) {
		this.jpgCompressorGUI = jpgCompressorGUI;
		compressThread = new Thread(this);
		// Kill all threads when closing the application
		compressThread.setDaemon(true);
		compressThread.start();
	}

	@Override
	public void run() {
		try {
			jpgCompressorGUI.actualQuality -= 0.01;
			if (jpgCompressorGUI.actualQuality < 0) {
				jpgCompressorGUI.actualQuality = 0.0;
			}
			resultImage = JPGImageCompress.compressImage(
					jpgCompressorGUI.getBufferedImage(),
					jpgCompressorGUI.actualQuality);

		} catch (IOException e) {
			CompressorException.behandleException(jpgCompressorGUI, e);
		}
	}

	public BufferedImage getResultImage() {
		return resultImage;
	}

	public Thread getCompressThread() {
		return compressThread;
	}
}
