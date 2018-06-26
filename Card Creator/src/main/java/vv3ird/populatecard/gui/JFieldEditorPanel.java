package vv3ird.populatecard.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import vv3ird.populatecard.control.ProjectManager;
import vv3ird.populatecard.data.Field;
import vv3ird.populatecard.data.Field.CardSide;
import vv3ird.populatecard.data.Field.FieldType;
import vv3ird.populatecard.data.Project;

public class JFieldEditorPanel extends JPanel {
	private static final long serialVersionUID = 2838584356620026755L;
	private JSpinner spinnerPointX;
	private JSpinner spinnerPointY;
	private JSpinner spinnerWidth;
	private JSpinner spinnerHeight;
	private Field field;
	private Project p;
	private JSpinner spinnerFontSize;
	private JComboBox<String> cbFont;
	private JComboBox<Field.FieldType> cbFieldType;
	private JComboBox<Field.CardSide> cbCardSide;
	private JTextField tfName;
	private JCheckBox chbxIndented;
	private JCheckBox chbxResize;

	/**
	 * Create the panel.
	 */
	public JFieldEditorPanel(Field field, Project p) {
		this.field = field;
		this.p = p;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setPreferredSize(new Dimension(360, 240));
		setMaximumSize(new Dimension(360, 240));
		
		Box horizontalBox_6 = Box.createHorizontalBox();
		add(horizontalBox_6);
		
		JLabel lblName = new JLabel("Name:");
		horizontalBox_6.add(lblName);
		
		tfName = new JTextField();
		tfName.setPreferredSize(new Dimension(100, 20));
		tfName.setMaximumSize(new Dimension(100, 20));
		horizontalBox_6.add(tfName);
		
		Component horizontalGlue_6 = Box.createHorizontalGlue();
		horizontalBox_6.add(horizontalGlue_6);
		
		Component verticalStrut_4 = Box.createVerticalStrut(20);
		add(verticalStrut_4);
		
		Box horizontalBox = Box.createHorizontalBox();
		horizontalBox.setAlignmentY(Component.CENTER_ALIGNMENT);
		add(horizontalBox);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		horizontalBox.add(horizontalGlue);
		
		JLabel lblPoint = new JLabel("Coordinates");
		horizontalBox.add(lblPoint);
		
		Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
		horizontalBox.add(rigidArea);
		
		JLabel lblX = new JLabel("X:");
		horizontalBox.add(lblX);
		
		Component rigidArea_1 = Box.createRigidArea(new Dimension(5, 20));
		horizontalBox.add(rigidArea_1);
		spinnerPointX = new JSpinner();
		spinnerPointX.setModel(new SpinnerNumberModel(0, 0, 9999, 1));
		spinnerPointX.setPreferredSize(new Dimension(100, 20));
		spinnerPointX.setMaximumSize(new Dimension(100, 20));
		spinnerPointX.setValue(field.getRect().getX());
		horizontalBox.add(spinnerPointX);
		
		Component rigidArea_2 = Box.createRigidArea(new Dimension(45, 20));
		horizontalBox.add(rigidArea_2);
		
		JLabel lblY = new JLabel("Y:");
		horizontalBox.add(lblY);
		
		Component rigidArea_3 = Box.createRigidArea(new Dimension(5, 20));
		horizontalBox.add(rigidArea_3);
		spinnerPointY = new JSpinner();
		spinnerPointY.setModel(new SpinnerNumberModel(0, 0, 9999, 1));
		spinnerPointY.setPreferredSize(new Dimension(100, 20));
		spinnerPointY.setMaximumSize(new Dimension(100, 20));
		spinnerPointY.setValue(field.getRect().getY());
		horizontalBox.add(spinnerPointY);
		CardSide[] sides = Field.CardSide.values();
		
		Box horizontalBox_1 = Box.createHorizontalBox();
		horizontalBox_1.setAlignmentY(0.5f);
		add(horizontalBox_1);
		
		Component horizontalGlue_1 = Box.createHorizontalGlue();
		horizontalBox_1.add(horizontalGlue_1);
		
		Component rigidArea_4 = Box.createRigidArea(new Dimension(56, 20));
		horizontalBox_1.add(rigidArea_4);
		
		JLabel label_1 = new JLabel("Width:");
		horizontalBox_1.add(label_1);
		
		Component rigidArea_5 = Box.createRigidArea(new Dimension(5, 20));
		horizontalBox_1.add(rigidArea_5);
		spinnerWidth = new JSpinner();
		spinnerWidth.setModel(new SpinnerNumberModel(0, 0, 9999, 1));
		spinnerWidth.setPreferredSize(new Dimension(100, 20));
		spinnerWidth.setMaximumSize(new Dimension(100, 20));
		spinnerWidth.setValue(field.getRect().getWidth());
		horizontalBox_1.add(spinnerWidth);
		
		Component rigidArea_6 = Box.createRigidArea(new Dimension(20, 20));
		horizontalBox_1.add(rigidArea_6);
		
		JLabel label_2 = new JLabel("Height:");
		horizontalBox_1.add(label_2);
		
		Component rigidArea_7 = Box.createRigidArea(new Dimension(5, 20));
		horizontalBox_1.add(rigidArea_7);
		spinnerHeight = new JSpinner();
		spinnerHeight.setModel(new SpinnerNumberModel(0, null, 9999, 1));
		spinnerHeight.setValue(field.getRect().getHeight());
		spinnerHeight.setPreferredSize(new Dimension(100, 20));
		spinnerHeight.setMaximumSize(new Dimension(100, 20));
		horizontalBox_1.add(spinnerHeight);
		
		Component verticalStrut_3 = Box.createVerticalStrut(5);
		add(verticalStrut_3);
		
		Box horizontalBox_2 = Box.createHorizontalBox();
		add(horizontalBox_2);
		
		Component horizontalGlue_2 = Box.createHorizontalGlue();
		horizontalBox_2.add(horizontalGlue_2);
		
		Component rigidArea_11 = Box.createRigidArea(new Dimension(70, 20));
		horizontalBox_2.add(rigidArea_11);
		
		JLabel lblCardSide = new JLabel("Card Side:");
		horizontalBox_2.add(lblCardSide);
		
		Component rigidArea_8 = Box.createRigidArea(new Dimension(5, 20));
		horizontalBox_2.add(rigidArea_8);
		cbCardSide = new JComboBox<>(sides);
		cbCardSide.setSelectedItem(field.getSide());
		horizontalBox_2.add(cbCardSide);
		cbCardSide.setPreferredSize(new Dimension(120, 20));
		cbCardSide.setMaximumSize(new Dimension(120, 20));
		
		Component verticalStrut = Box.createVerticalStrut(5);
		add(verticalStrut);
		
		Box horizontalBox_3 = Box.createHorizontalBox();
		add(horizontalBox_3);
		
		Component horizontalGlue_3 = Box.createHorizontalGlue();
		horizontalBox_3.add(horizontalGlue_3);
		
		Component rigidArea_13 = Box.createRigidArea(new Dimension(70, 20));
		horizontalBox_3.add(rigidArea_13);
		
		JLabel lblContent = new JLabel("Content:");
		horizontalBox_3.add(lblContent);
		
		Component rigidArea_9 = Box.createRigidArea(new Dimension(12, 20));
		horizontalBox_3.add(rigidArea_9);
		Field.FieldType[] types = Field.FieldType.values();
		cbFieldType = new JComboBox<>(types);
		cbFieldType.setSelectedItem(field.getType());
		cbFieldType.setAlignmentX(Component.LEFT_ALIGNMENT);
		cbFieldType.setPreferredSize(new Dimension(120, 20));
		cbFieldType.setMaximumSize(new Dimension(120, 20));
		horizontalBox_3.add(cbFieldType);
		
		Component verticalStrut_1 = Box.createVerticalStrut(5);
		add(verticalStrut_1);
		
		Box horizontalBox_4 = Box.createHorizontalBox();
		add(horizontalBox_4);
		
		Component horizontalGlue_4 = Box.createHorizontalGlue();
		horizontalBox_4.add(horizontalGlue_4);
		
		Component rigidArea_14 = Box.createRigidArea(new Dimension(70, 20));
		horizontalBox_4.add(rigidArea_14);
		
		JLabel lblFont = new JLabel("Font:");
		horizontalBox_4.add(lblFont);
		
		Component rigidArea_10 = Box.createRigidArea(new Dimension(29, 20));
		horizontalBox_4.add(rigidArea_10);
		String[] fonts = getFonts();
		cbFont = new JComboBox<>(fonts);
		cbFont.setPreferredSize(new Dimension(120, 20));
		cbFont.setMaximumSize(new Dimension(120, 20));
		cbFont.setAlignmentX(0.0f);
		setFont(field.getFont());
		horizontalBox_4.add(cbFont);
		
		Component verticalStrut_2 = Box.createVerticalStrut(5);
		add(verticalStrut_2);
		
		Box horizontalBox_5 = Box.createHorizontalBox();
		horizontalBox_5.setAlignmentY(0.5f);
		add(horizontalBox_5);
		
		Component horizontalGlue_5 = Box.createHorizontalGlue();
		horizontalBox_5.add(horizontalGlue_5);
		
		Component rigidArea_15 = Box.createRigidArea(new Dimension(70, 20));
		horizontalBox_5.add(rigidArea_15);
		
		JLabel lblFontSize = new JLabel("Font Size:");
		horizontalBox_5.add(lblFontSize);
		
		Component rigidArea_12 = Box.createRigidArea(new Dimension(7, 20));
		horizontalBox_5.add(rigidArea_12);
		
		spinnerFontSize = new JSpinner();
		spinnerFontSize.setModel(new SpinnerNumberModel(20, 5, 100, 1));
		spinnerFontSize.setPreferredSize(new Dimension(40, 20));
		spinnerFontSize.setMaximumSize(new Dimension(40, 20));
		spinnerFontSize.setValue(field.getFontSize());
		horizontalBox_5.add(spinnerFontSize);
		
		Component rigidArea_16 = Box.createRigidArea(new Dimension(80, 20));
		horizontalBox_5.add(rigidArea_16);
		
		Box horizontalBox_7 = Box.createHorizontalBox();
		horizontalBox_7.setAlignmentY(0.5f);
		add(horizontalBox_7);
		
		Component horizontalGlue_7 = Box.createHorizontalGlue();
		horizontalBox_7.add(horizontalGlue_7);
		
		Component rigidArea_17 = Box.createRigidArea(new Dimension(70, 20));
		horizontalBox_7.add(rigidArea_17);
		
		Component rigidArea_18 = Box.createRigidArea(new Dimension(7, 20));
		horizontalBox_7.add(rigidArea_18);
		
		chbxIndented = new JCheckBox("Indented paragraphs");
		chbxIndented.setSelected(field.isIndented());
		chbxIndented.setPreferredSize(new Dimension(130, 20));
		chbxIndented.setMaximumSize(new Dimension(130, 20));
		horizontalBox_7.add(chbxIndented);
		
		Component rigidArea_19 = Box.createRigidArea(new Dimension(45, 20));
		horizontalBox_7.add(rigidArea_19);
		
		Box horizontalBox_8 = Box.createHorizontalBox();
		add(horizontalBox_8);
		
		Component horizontalGlue_8 = Box.createHorizontalGlue();
		horizontalBox_8.add(horizontalGlue_8);
		
		Component rigidArea_20 = Box.createRigidArea(new Dimension(70, 20));
		horizontalBox_8.add(rigidArea_20);

		chbxResize = new JCheckBox("Resize text to fit field");
		chbxResize.setSelected(field.isIndented());
		chbxResize.setPreferredSize(new Dimension(130, 20));
		chbxResize.setMaximumSize(new Dimension(130, 20));
		chbxResize.setSelected(field.resizeText());
		horizontalBox_8.add(chbxResize);
		
		Component rigidArea_21 = Box.createRigidArea(new Dimension(45, 20));
		horizontalBox_8.add(rigidArea_21);
		
		Component verticalGlue = Box.createVerticalGlue();
		add(verticalGlue);

	}
	
	private void setFont(String font) {
		DefaultComboBoxModel<String> model = ((DefaultComboBoxModel<String>)cbFont.getModel());
		int s = model.getSize();
		int selectedIdx = -1;
		if (font != null)
			for(int i=0;i<s&&selectedIdx<0;i++) {
				if (font.equals(model.getElementAt(i))) {
					selectedIdx = i;
				}
			}
		
		cbFont.setSelectedIndex(selectedIdx);
	}

	public Field getField() {
		// Retrieve values
		int x = (Integer) spinnerPointX.getValue();
		int y = (Integer) spinnerPointY.getValue();
		int width = (Integer) spinnerWidth.getValue();
		int height = (Integer) spinnerHeight.getValue();
		String name = tfName.getText();
		String fontName = (String)cbFont.getSelectedItem();
		System.out.println("Font: " + fontName);
		int fontSize = (Integer) spinnerFontSize.getValue();
		CardSide side = (CardSide) cbCardSide.getSelectedItem();
		FieldType type = (FieldType) cbFieldType.getSelectedItem();
		boolean indeted = chbxIndented.isSelected();
		boolean resize = chbxResize.isSelected();
		// Set values
		field.setName(name);
		field.setFont(fontName);
		field.setFontSize(fontSize);
		field.setType(type);
		field.setSide(side);
		field.setX(x);
		field.setY(y);
		field.setWidth(width);
		field.setHeight(height);
		field.setIndented(indeted);
		field.setResizeText(resize);
		return field;
	}
	
	public void setField(Field field) {
		this.field = field;
		tfName.setText(field.getName());
		spinnerPointX.setValue(field.getRect().x);
		spinnerPointY.setValue(field.getRect().y);
		spinnerWidth.setValue(field.getRect().width);
		spinnerHeight.setValue(field.getRect().height);
		setFont(field.getFont());
		spinnerFontSize.setValue(field.getFontSize());
		cbCardSide.setSelectedItem(field.getSide());
		cbFieldType.setSelectedItem(field.getType());
		chbxIndented.setSelected(field.isIndented());
		chbxResize.setSelected(field.resizeText());
	}

	private String[] getFonts() {
		String[] systemFonts = ProjectManager.getSystemFonts();
		List<String> projectFonts = new LinkedList<>(p.getFonts().keySet());
		String[] fontList = new String[projectFonts.size() + systemFonts.length];
		for (int i = 0; i < fontList.length; i++) {
			if (i < projectFonts.size()) 
				fontList[i] = projectFonts.get(i);
			else
				fontList[i] = systemFonts[i-projectFonts.size()];
		}
		return fontList;
	}

	public void resetField() {
		setField(this.field);
	}

	public String getFieldName() {
		return this.field.getName();
	}

}
