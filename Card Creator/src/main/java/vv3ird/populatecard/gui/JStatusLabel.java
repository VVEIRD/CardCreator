package vv3ird.populatecard.gui;

import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;

public class JStatusLabel extends JLabel implements StatusListener {

	List<String> log = new LinkedList<>();

	/**
	 * 
	 */
	private static final long serialVersionUID = 2219917505825554693L;

	public JStatusLabel() {
		super();
		log = new LinkedList<>();
	}

	public JStatusLabel(Icon arg0, int arg1) {
		super(arg0, arg1);
		log = new LinkedList<>();
	}

	public JStatusLabel(Icon arg0) {
		super(arg0);
		log = new LinkedList<>();
	}

	public JStatusLabel(String arg0, Icon arg1, int arg2) {
		super(arg0, arg1, arg2);
		log = new LinkedList<>();
	}

	public JStatusLabel(String arg0, int arg1) {
		super(arg0, arg1);
		log = new LinkedList<>();
	}

	public JStatusLabel(String arg0) {
		super(arg0);
		log = new LinkedList<>();
	}

	@Override
	public void setText(String text) {
		super.setText(text);
		if (text != null && log != null)
			log.add(text);
	}

	@Override
	public String[] getLog() {
		// TODO Auto-generated method stub
		return log.toArray(new String[0]);
	}

	@Override
	public void clearLog() {
		log.clear();
	}

}
