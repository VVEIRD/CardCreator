package vv3ird.populatecard.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import vv3ird.populatecard.data.Field;
import vv3ird.populatecard.data.Project;

public class JCSVMapperPanel extends JPanel {

	private static final long serialVersionUID = -5869150781509703293L;

	private Project project = null;
	
	private JMappingPanel[] mappings = null;
	
	/**
	 * Create the panel.
	 */
	public JCSVMapperPanel(Project project) {
		if (project.getCsvHeader() == null)
			throw new IllegalArgumentException("The project must have CSV data loaded");
		this.project = project;
		Set<String> ks = project.getCsvHeader().keySet();
		String[] csvColumns = ks.toArray(new String[0]);
		String[] csvColumnsFinal = new String[csvColumns.length+1];
		for (int i = 0; i < csvColumns.length; i++) {
			csvColumnsFinal[i+1] = csvColumns[i];
		}
		csvColumnsFinal[0] = "";
		List<Field> fields = this.project.getFp().getFields();
		mappings = new JMappingPanel[fields.size()];
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		Box horizontalBox_1 = Box.createHorizontalBox();
		add(horizontalBox_1);
		
		JLabel lblFieldCsv = new JLabel("Field - CSV Mapping");
		horizontalBox_1.add(lblFieldCsv);
		
		Component verticalStrut = Box.createVerticalStrut(5);
		add(verticalStrut);
		
		Box horizontalBox_2 = Box.createHorizontalBox();
		horizontalBox_2.setAlignmentY(Component.CENTER_ALIGNMENT);
		add(horizontalBox_2);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(2);
		horizontalBox_2.add(horizontalStrut_1);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setMaximumSize(new Dimension(800, 600));
		scrollPane.setPreferredSize(new Dimension(320, 450));
		scrollPane.setSize(new Dimension(320, 450));
		horizontalBox_2.add(scrollPane);
		
		JPanel pnMapping = new JPanel();
		scrollPane.setViewportView(pnMapping);
		pnMapping.setLayout(new BoxLayout(pnMapping, BoxLayout.Y_AXIS));
		int i=0;
		for (Field field : fields) {
			JMappingPanel mp = new JMappingPanel(field.getName(), project.getCsvColumn(field.getName()), csvColumnsFinal);
			mappings[i++] = mp;
			pnMapping.add(mp);
		}
		
		Component horizontalStrut = Box.createHorizontalStrut(2);
		horizontalBox_2.add(horizontalStrut);
		
		Component verticalStrut_1 = Box.createVerticalStrut(5);
		add(verticalStrut_1);
		
		Box horizontalBox = Box.createHorizontalBox();
		add(horizontalBox);
	}
	
	public Map<String, String> getMappings() {
		Map<String, String> mapps = new HashMap<>();
		for (JMappingPanel mappingPanel : mappings) {
			String fieldName = mappingPanel.getFieldName();
			String csvColumnName = mappingPanel.getSelectedCsvColumn();
			if(csvColumnName != null && !csvColumnName.isEmpty()) 
				mapps.put(fieldName, csvColumnName);
		}
		return mapps;
	}

}
