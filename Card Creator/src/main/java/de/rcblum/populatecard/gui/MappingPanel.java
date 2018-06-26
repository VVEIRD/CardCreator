package de.rcblum.populatecard.gui;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JTextPane;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JComboBox;

public class MappingPanel extends JPanel {
	
	private String fieldName = null;
	
	private String[] csvFields = null; 
	private JComboBox<String> cbCsvFields;
	
	public MappingPanel(String fieldName, String csvField, String[] csvColumns) {
		this.fieldName = fieldName;
		this.csvFields = csvColumns;
		int selectedIndex = -1;
		for (int i = 0; i < csvColumns.length&&selectedIndex<0; i++) {
			if(csvColumns[i].equals(csvField) || csvColumns[i].equals(fieldName)) {
				selectedIndex=i;
			}
		}
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		Component horizontalStrut = Box.createHorizontalStrut(2);
		add(horizontalStrut);
		
		JTextPane textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setText(fieldName);
		textPane.setPreferredSize(new Dimension(150, 20));
		textPane.setSize(new Dimension(150, 20));
		textPane.setMaximumSize(new Dimension(150, 20));
		setSize(new Dimension(300, 24));
		setMaximumSize(new Dimension(600, 24));
		setPreferredSize(new Dimension(300, 24));
		add(textPane);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		add(horizontalGlue);
		
		Component verticalStrut = Box.createVerticalStrut(24);
		add(verticalStrut);
		
		cbCsvFields = new JComboBox(csvColumns);
		cbCsvFields.setSelectedIndex(selectedIndex);
		cbCsvFields.setPreferredSize(new Dimension(150, 20));
		cbCsvFields.setSize(new Dimension(150, 20));
		cbCsvFields.setMaximumSize(new Dimension(150, 20));
		add(cbCsvFields);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(2);
		add(horizontalStrut_1);
	}
	
	
	public String getFieldName() {
		return fieldName;
	}
	
	public String getSelectedCsvColumn() {
		return (String)cbCsvFields.getSelectedItem();
	}
}
