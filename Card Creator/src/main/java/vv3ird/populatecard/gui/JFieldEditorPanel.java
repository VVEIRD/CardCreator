package vv3ird.populatecard.gui;

import java.awt.Component;
import java.awt.Dimension;

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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import vv3ird.populatecard.CardCreator;
import vv3ird.populatecard.data.Field;
import vv3ird.populatecard.data.Field.CardSide;
import vv3ird.populatecard.data.Field.FieldType;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class JFieldEditorPanel extends JPanel {
	
	private static final long serialVersionUID = 2838584356620026755L;
	
	private JSpinner spinnerPointX;
	private JSpinner spinnerPointY;
	private JSpinner spinnerWidth;
	private JSpinner spinnerHeight;
	private Field field;
	private JSpinner spinnerFontSize;
	private JComboBox<String> cbFont;
	private JComboBox<Field.FieldType> cbFieldType;
	private JComboBox<Field.CardSide> cbCardSide;
	private JTextField tfName;
	private JCheckBox chbxIndented;
	private JCheckBox chbxResize;
	private JCheckBox chbxJS;
	private JTextArea txtJavaScript;

	/**
	 * Panel to edit the attributes of an Field.
	 */
	public JFieldEditorPanel(Field field) {
		this.field = field;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setPreferredSize(new Dimension(360, 400));
		setMaximumSize(new Dimension(360, 400));
		
		Box horizontalBox_6 = Box.createHorizontalBox();
		add(horizontalBox_6);
		
		JLabel lblName = new JLabel("Name:");
		horizontalBox_6.add(lblName);
		
		tfName = new JTextField(field != null ? field.getName() : "");
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
		spinnerPointX.setValue((int)field.getRect().getX());
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
		spinnerPointY.setValue((int)field.getRect().getY());
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
		spinnerWidth.setValue((int) field.getRect().getWidth());
		horizontalBox_1.add(spinnerWidth);
		
		Component rigidArea_6 = Box.createRigidArea(new Dimension(20, 20));
		horizontalBox_1.add(rigidArea_6);
		
		JLabel label_2 = new JLabel("Height:");
		horizontalBox_1.add(label_2);
		
		Component rigidArea_7 = Box.createRigidArea(new Dimension(5, 20));
		horizontalBox_1.add(rigidArea_7);
		spinnerHeight = new JSpinner();
		spinnerHeight.setModel(new SpinnerNumberModel(0, null, 9999, 1));
		spinnerHeight.setValue((int) field.getRect().getHeight());
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
		cbCardSide = new JComboBox(sides);
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
		cbFieldType = new JComboBox(types);
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
		String[] fonts = CardCreator.getFontNames();//getFonts();
		cbFont = new JComboBox(fonts);
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
		
		Box horizontalBox_9 = Box.createHorizontalBox();
		add(horizontalBox_9);
		
		Component horizontalGlue_9 = Box.createHorizontalGlue();
		horizontalBox_9.add(horizontalGlue_9);
		
		Component rigidArea_22 = Box.createRigidArea(new Dimension(70, 20));
		horizontalBox_9.add(rigidArea_22);

		chbxJS = new JCheckBox("Calculate Value via JS");
		chbxJS.setSelected(field.isIndented());
		chbxJS.setPreferredSize(new Dimension(130, 20));
		chbxJS.setMaximumSize(new Dimension(130, 20));
		chbxJS.setSelected(field.resizeText());
		
		chbxJS.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				txtJavaScript.setEnabled(chbxJS.isSelected());
			}
		});
		chbxJS.setSelected(this.field.isCalculated());
		horizontalBox_9.add(chbxJS);
		
		Component rigidArea_23 = Box.createRigidArea(new Dimension(45, 20));
		horizontalBox_9.add(rigidArea_23);
		
		txtJavaScript = new JTextArea();
		
		JScrollPane scrollPane = new JScrollPane(txtJavaScript);
		add(scrollPane);
		
		txtJavaScript.setRows(4);
		if (this.field != null && this.field.isCalculated())
			txtJavaScript.setText(this.field.getJavaScript());
		else
			txtJavaScript.setText( "// Variables are taken from the csv file\n"
									+ "// calculate() will be called to retrive\n// the calculated value\n"
									+ "// Use parseInt() to convert variables to integers etc"
									+ "\n"
									+ "function calculate(value) {\n"
									+ "    // add your calculation here\n"
									+ "    return 0; // return your calculated value\n"
									+ "}");
		txtJavaScript.setEnabled(chbxJS.isSelected());
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

	/**
	 * Copies the values from the UI to the field and returns it
	 * @return Field edited by this panel or null, if no Field was given to the editor
	 */
	public Field getField() {
		if (this.field == null)
			return null;
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
		field.setJavaScript(chbxJS.isSelected() ? txtJavaScript.getText() : null);
		return field;
	}
	
	public void setField(Field field) {
		this.field = field;
		tfName.setText(this.field != null ? field.getName() : "");
		spinnerPointX.setValue(this.field != null ? field.getRect().x : 0);
		spinnerPointY.setValue(this.field != null ? field.getRect().y : 0);
		spinnerWidth.setValue(this.field != null ? field.getRect().width : 0);
		spinnerHeight.setValue(this.field != null ? field.getRect().height : 0);
		spinnerFontSize.setValue(this.field != null ? field.getFontSize() : 18);
		cbCardSide.setSelectedItem(this.field != null ? field.getSide() : CardSide.FRONT);
		cbFieldType.setSelectedItem(this.field != null ? field.getType() : FieldType.TEXT_LEFT);
		chbxIndented.setSelected(this.field != null ? field.isIndented() : true);
		chbxResize.setSelected(this.field != null ? field.resizeText() : false);
		chbxJS.setSelected(this.field != null && this.field.isCalculated());
		if (this.field != null && this.field.isCalculated())
			txtJavaScript.setText(this.field.getJavaScript());
		else
			txtJavaScript.setText( "// asdfVariables are taken from the csv file\n"
									+ "// calculate() will be called to retrive\n// the calculated value\n"
									+ "// Use parseInt() to convert variables to integers etc"
									+ "\n"
									+ "function calculate(value) {\n"
									+ "    // add your calculation here\n"
									+ "    return 0; // return your calculated value\n"
									+ "}");
		txtJavaScript.setEnabled(chbxJS.isSelected());
		setFont(this.field != null ? field.getFont() : null);
	}
//
//	private String[] getFontsa() {
//		String[] fonts = CardCreator.getFonts().keySet().toArray(new String[0]);
////		List<String> projectFonts = new LinkedList<>(p.getFonts().keySet());
////		String[] fontList = new String[projectFonts.size() + systemFonts.length];
////		for (int i = 0; i < fontList.length; i++) {
////			if (i < projectFonts.size()) 
////				fontList[i] = projectFonts.get(i);
////			else
////				fontList[i] = systemFonts[i-projectFonts.size()];
////		}
//		return fonts;
//	}

	public void resetField() {
		setField(this.field);
	}

	public String getFieldName() {
		return this.field != null ? this.field.getName() : null;
	}

}
