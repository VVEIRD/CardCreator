package vv3ird.populatecard.gui;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.Box;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class JLogPanel extends JPanel {
	
	private static final long serialVersionUID = -4476577248213507867L;
	
	private StatusListener list = null;
	
	private JPanel logPanel;

	/**
	 * Create the panel.
	 */
	public JLogPanel(StatusListener list) {
		this.list = list;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setSize(new Dimension(320, 360));
		setPreferredSize(new Dimension(320, 360));
		setMaximumSize(new Dimension(320, 360));
		
		Component verticalStrut_2 = Box.createVerticalStrut(5);
		add(verticalStrut_2);
		
		Box horizontalBox_1 = Box.createHorizontalBox();
		add(horizontalBox_1);
		
		JButton btnClearLog = new JButton("Clear log");
		btnClearLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				list.clearLog();
			}
		});
		horizontalBox_1.add(btnClearLog);
		
		Component verticalStrut_1 = Box.createVerticalStrut(5);
		add(verticalStrut_1);
		
		Box horizontalBox = Box.createHorizontalBox();
		add(horizontalBox);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(5);
		horizontalBox.add(horizontalStrut_1);
		
		JScrollPane scrollPane = new JScrollPane();
		horizontalBox.add(scrollPane);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		logPanel = new JPanel();
		scrollPane.setViewportView(logPanel);
		logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));
		
		Component horizontalStrut_2 = Box.createHorizontalStrut(5);
		horizontalBox.add(horizontalStrut_2);
		
		Component verticalStrut_3 = Box.createVerticalStrut(5);
		add(verticalStrut_3);

		populateLogPanel();
	}

	private void populateLogPanel() {
		logPanel.removeAll();
		for (String logE : list.getLog()) {

			Box LogEntry = Box.createHorizontalBox();
			logPanel.add(LogEntry);

			Component horizontalStrut = Box.createHorizontalStrut(5);
			LogEntry.add(horizontalStrut);
			JLabel lblLog = new JLabel(logE);
			LogEntry.add(lblLog);

			Component verticalStrut = Box.createVerticalStrut(18);
			LogEntry.add(verticalStrut);

			Component horizontalGlue = Box.createHorizontalGlue();
			LogEntry.add(horizontalGlue);
			horizontalStrut = Box.createHorizontalStrut(5);
			LogEntry.add(horizontalStrut);
		}		
		logPanel.revalidate();
		logPanel.repaint();
	}

}
