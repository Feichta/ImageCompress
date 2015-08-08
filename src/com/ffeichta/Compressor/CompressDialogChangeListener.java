package com.ffeichta.Compressor;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Listener that updates a label when values from Spinner or Slider change
 * 
 * @author Fabian
 * 
 */
public class CompressDialogChangeListener implements ChangeListener {

	private CompressButtonDialog dialog = null;

	public CompressDialogChangeListener(CompressButtonDialog dialog) {
		this.dialog = dialog;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		double numTemp = (dialog.getTo() - dialog.getFrom())
				/ (double) dialog.slider.getValue();
		int numberPictures = (int) Math.abs(Math.ceil(numTemp));
		String msg = dialog.slider.getValue() + "    (" + numberPictures
				+ " pictures)";
		dialog.labelNumber.setText(msg);
	}
}
