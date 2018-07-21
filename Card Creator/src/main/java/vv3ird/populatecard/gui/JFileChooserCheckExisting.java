package vv3ird.populatecard.gui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class JFileChooserCheckExisting extends JFileChooser {
	
	private static final long serialVersionUID = 1L;
	
	public JFileChooserCheckExisting(String path) {
		super(path);
	}

	@Override
	public void approveSelection() {
		File f = getSelectedFile();
		if (f.exists() && getDialogType() == SAVE_DIALOG) {
			int result = JOptionPane.showConfirmDialog(this, "The file exists, overwrite?", "Existing file",
					JOptionPane.YES_NO_CANCEL_OPTION);
			switch (result) {
			case JOptionPane.YES_OPTION:
				super.approveSelection();
				return;
			case JOptionPane.NO_OPTION:
				return;
			case JOptionPane.CLOSED_OPTION:
				return;
			case JOptionPane.CANCEL_OPTION:
				cancelSelection();
				return;
			}
		}
		super.approveSelection();
	}
}

