package com.ffeichta.Compressor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

/**
 * Thread who compress an saves a image and if needs than creates new
 * directories
 * 
 * @author Fabian
 * 
 */
public class CompressAndSaveThread extends Thread {

	/**
	 * References to the main-GUI and the compress-Dialog
	 */
	private CompressButtonDialog compressButtonDialog = null;
	private JPGCompressorGUI jpgCompressorGUI = null;
	/**
	 * Vector contains threads, this is needed for the ProgressBar
	 */
	private Vector<Thread> threadList = null;

	public CompressAndSaveThread(CompressButtonDialog compressButtonDialog,
			JPGCompressorGUI jpgCompressorGUI) {
		this.compressButtonDialog = compressButtonDialog;
		this.jpgCompressorGUI = jpgCompressorGUI;
		this.start();
	}

	@Override
	public void run() {
		threadList = new Vector<>();
		String path = null;
		if (compressButtonDialog.getOption() == CompressButtonDialog.Options.CURRENTDIRETORY) {
			path = jpgCompressorGUI.pathToFile;
		} else {
			if (compressButtonDialog.getTextFieldText() != null) {
				path = compressButtonDialog.getTextFieldText();
			} else {
				CompressorException.behandleException(compressButtonDialog,
						new FileNotFoundException(
								"The entered path does not exist!"));
				return;
			}
		}
		// Create an empty folder if it needs
		if (compressButtonDialog.createSubdirectory()) {
			path += "compressed_pictures\\";
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdir()) {
					CompressorException.behandleException(compressButtonDialog,
							new FileNotFoundException(
									"Can not create a new empty directory!"));
				}
			} else {
				// Path already exists
				boolean success = false;
				int i = 1;
				String pathTemp = null;
				while (!success) {
					pathTemp = path.substring(0, path.length() - 1) + "_" + i
							+ "\\";
					File tempFile = new File(pathTemp);
					if (tempFile.mkdir()) {
						success = true;
						path = pathTemp;
					}
					i++;
				}
			}
		}
		final String pathTemp = path;
		for (double i = compressButtonDialog.getFrom(); i < compressButtonDialog
				.getTo(); i = i + compressButtonDialog.getSliderValue()) {
			final double quality = i / 100;
			Thread t = new Thread() {
				@Override
				public void run() {
					try {
						JPGImageCompress.compressImage(
								jpgCompressorGUI.imageComponent.getImage(),
								pathTemp, quality);
					} catch (IOException e) {
						CompressorException.behandleException(jpgCompressorGUI,
								e);
					}
				}
			};
			threadList.add(t);
			t.start();
		}
	}

	public Vector<Thread> getThreadList() {
		return threadList;
	}
}
