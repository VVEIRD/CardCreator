package vv3ird.populatecard.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import vv3ird.populatecard.CardCreator;
import vv3ird.populatecard.control.FieldEditor;
import vv3ird.populatecard.control.FieldEditor.Corner;
import vv3ird.populatecard.data.Field;
import vv3ird.populatecard.data.Field.CardSide;
import vv3ird.populatecard.data.FieldPackage;
import vv3ird.populatecard.data.Project;
import javax.swing.JCheckBox;

public class JFieldMappingPanel extends JPanel {

	private static final long serialVersionUID = 6755160427559608684L;

	private JPanel contentPane;

	private JScrollPane spFront;

	private JLabel pnFront;

	private JLabel pnRear;

	private DefaultMutableTreeNode fieldRoot = null;

	private BufferedImage front = null;
	private BufferedImage frontBuffer1 = null;
	private BufferedImage frontBuffer2 = null;
	private BufferedImage frontPreview = null;

	private BufferedImage rear = null;
	private BufferedImage rearBuffer1 = null;
	private BufferedImage rearBuffer2 = null;
	private BufferedImage rearPreview = null;
	
	private boolean previewMode = false;

	Dimension frontPos1 = null;
	Dimension backPos1 = null;
	
	private List<Field> fields = new LinkedList<>();
	
	private FieldEditor editor = null;
	
	private Corner editCorner = Corner.NONE;

	private Color rectColor = Color.GREEN;
	private JScrollPane spBack;
	private JTree treeFields;
	private JFieldEditorPanel fieldEditior;
	private JCheckBox chckbxPreview;


	/**
	 * @wbp.parser.constructor
	 */
	public JFieldMappingPanel(Project p) {
		this(p, true);
	}
	
	public JFieldMappingPanel(Project p, boolean frontPanel) {
		BufferedImage frontImage = p.getFp().getFrontImage();
		BufferedImage rearImage = p.getFp().getRearImage();
		
		int preferredWidth = frontImage.getWidth() < rearImage.getWidth() ? frontImage.getWidth() : rearImage.getWidth();
		preferredWidth = preferredWidth < 1000 ? preferredWidth : 1000;
		int preferredHeight = frontImage.getHeight() < rearImage.getHeight() ? frontImage.getHeight() : rearImage.getHeight();
		preferredHeight = preferredHeight < 1000 ? preferredHeight : 800;
		setBounds(100, 100, 600, 400);
		setMinimumSize(new Dimension(400, 400));
		setSize(new Dimension(400, 400));
		setPreferredSize(new Dimension(preferredWidth, preferredHeight));
		setMaximumSize(new Dimension(1000, 800));
		contentPane = this;
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.fields.addAll(p.getFp().getFields().stream().map(f -> f.clone()).collect(Collectors.toList()));
		this.editor = new FieldEditor(this.fields);
		contentPane.setLayout(new BorderLayout(0, 0));
		// Front image
		this.front = frontImage;
		this.frontBuffer1 = new BufferedImage(frontImage.getWidth(), frontImage.getHeight(), frontImage.getType());
		this.frontBuffer2 = new BufferedImage(frontImage.getWidth(), frontImage.getHeight(), frontImage.getType());
		Graphics g = frontBuffer1.getGraphics();
		Graphics g2 = frontBuffer2.getGraphics();
		g.drawImage(frontImage, 0, 0, null);
		g.dispose();
		g2.drawImage(frontImage, 0, 0, null);
		g2.dispose();
		// Back image
		this.rear = rearImage;
		this.rearBuffer1 = new BufferedImage(rearImage.getWidth(), rearImage.getHeight(), rearImage.getType());
		this.rearBuffer2 = new BufferedImage(rearImage.getWidth(), rearImage.getHeight(), rearImage.getType());
		g = frontBuffer1.getGraphics();
		g2 = frontBuffer2.getGraphics();
		g.drawImage(rearImage, 0, 0, null);
		g.dispose();
		g2.drawImage(rearImage, 0, 0, null);
		g2.dispose();

		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);

		JButton btnSaveDimensions = new JButton("Export CM File");
		btnSaveDimensions.setAlignmentY(Component.TOP_ALIGNMENT);
		btnSaveDimensions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String cmDir = System.getProperty("user.dir") + File.separator + "projects";
				try {
					if (!Files.exists(Paths.get(cmDir)))
						Files.createDirectories(Paths.get(cmDir));

					JFileChooser chooser = new JFileChooserCheckExisting(cmDir);
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					chooser.setAcceptAllFileFilterUsed(false);

					chooser.setDialogTitle("Choose a filename");
					chooser.setFileFilter(new FileNameExtensionFilter("CardMapper Files", new String[] { "cm" }));
					int res = chooser.showSaveDialog(JFieldMappingPanel.this);
					if (res == JFileChooser.APPROVE_OPTION) {
						File f = chooser.getSelectedFile();
						FieldPackage fPackage = new FieldPackage(front, rear);
						fPackage.addFields(fields);
						fPackage.setAlternateRearImage(p.getFp().getAlternateRearImage());
						if (!f.getAbsolutePath().endsWith(".cm"))
							f = new File(f.getAbsolutePath() + ".cm");
						FieldPackage.save(fPackage, f.toPath());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));
		
		Component horizontalGlue_1 = Box.createHorizontalGlue();
		panel_1.add(horizontalGlue_1);
		panel_1.add(btnSaveDimensions);

		JLabel label = new JLabel("");
		panel_1.add(label);

		JButton btnChangeRectColor = new JButton("Change rect color");
		btnChangeRectColor.setAlignmentY(Component.TOP_ALIGNMENT);
		btnChangeRectColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Color nColor = JColorChooser.showDialog(null, "Change Button Background", rectColor);
				if (nColor != null) {
					rectColor = nColor;
				}
			}
		});
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(5);
		panel_1.add(horizontalStrut_1);
		panel_1.add(btnChangeRectColor);
		
		Component horizontalStrut = Box.createHorizontalStrut(15);
		panel_1.add(horizontalStrut);
		
		JButton btnSaveResize = new JButton("Save resize");
		btnSaveResize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFieldMappingPanel.this.editor.saveField();
				JFieldMappingPanel.this.setImage(front, rear);
				JFieldMappingPanel.this.repaintImage(CardSide.FRONT);
				JFieldMappingPanel.this.repaintImage(CardSide.REAR);
				btnSaveResize.setEnabled(false);
			}
		});
		btnSaveResize.setEnabled(false);
		btnSaveResize.setAlignmentY(Component.TOP_ALIGNMENT);
		panel_1.add(btnSaveResize);
		
		JLabel label_2 = new JLabel(" ");
		panel_1.add(label_2);
		
		Component horizontalStrut_2 = Box.createHorizontalStrut(15);
		panel_1.add(horizontalStrut_2);
		
		chckbxPreview = new JCheckBox("Preview");
		chckbxPreview.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				previewMode = chckbxPreview.isSelected();
				updatePreviewMode();
			}
		});
		chckbxPreview.setAlignmentY(Component.TOP_ALIGNMENT);
		panel_1.add(chckbxPreview);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		panel_1.add(horizontalGlue);
		
		Box horizontalBox = Box.createHorizontalBox();
		horizontalBox.setAlignmentX(Component.RIGHT_ALIGNMENT);
		panel_1.add(horizontalBox);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		pnFront = new JLabel(new ImageIcon(frontBuffer2));
		pnFront.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				JFieldMappingPanel.this.editCorner = Corner.NONE;
				if (JFieldMappingPanel.this.editor.isEditMode()) {
					if (!JFieldMappingPanel.this.editor.editFieldContains(e.getPoint())) {
						JFieldMappingPanel.this.editor.discardEdits();
						JFieldMappingPanel.this.setImage(front, rear);
						JFieldMappingPanel.this.repaintImage(CardSide.FRONT);
						btnSaveResize.setEnabled(false);
					}
				} else if (!JFieldMappingPanel.this.editor.isEditMode()) {
					if (JFieldMappingPanel.this.editor.editFieldContaining(e.getPoint(), CardSide.FRONT)) {
						JFieldMappingPanel.this.setImage(front, rear);
						JFieldMappingPanel.this.repaintImage(CardSide.FRONT);
						btnSaveResize.setEnabled(true);
					} else
						handleFieldCreation(e.getX(), e.getY(), Field.CardSide.FRONT);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (JFieldMappingPanel.this.editor.isEditMode()) {
					editCorner = JFieldMappingPanel.this.editor.getCornerOnEditField(e.getPoint());
					if(editCorner != Corner.NONE) {
						System.out.println("Corner selected: " + editCorner);
					}
				}
			}

			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		pnFront.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent arg0) {
				repaintImage(CardSide.FRONT);
			}

			@Override
			public void mouseDragged(MouseEvent arg0) {
				repaintImage(CardSide.FRONT);
			}
		});
		pnRear = new JLabel(new ImageIcon(rearBuffer2));
		pnRear.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				JFieldMappingPanel.this.editCorner = Corner.NONE;
				if (JFieldMappingPanel.this.editor.isEditMode()) {
					if (!JFieldMappingPanel.this.editor.editFieldContains(e.getPoint())) {
						JFieldMappingPanel.this.editor.discardEdits();
						JFieldMappingPanel.this.setImage(front, rear);
						JFieldMappingPanel.this.repaintImage(CardSide.REAR);
						btnSaveResize.setEnabled(false);
					}
				} else if (!JFieldMappingPanel.this.editor.isEditMode()) {
					if (JFieldMappingPanel.this.editor.editFieldContaining(e.getPoint(), CardSide.REAR)) {
						JFieldMappingPanel.this.setImage(front, rear);
						JFieldMappingPanel.this.repaintImage(CardSide.REAR);
						btnSaveResize.setEnabled(true);
					} else
						handleFieldCreation(e.getX(), e.getY(), Field.CardSide.REAR);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (JFieldMappingPanel.this.editor.isEditMode()) {
					editCorner = JFieldMappingPanel.this.editor.getCornerOnEditField(e.getPoint());
					if(editCorner != Corner.NONE) {
						System.out.println("Corner selected: " + editCorner);
					}
				}
			}

			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		pnRear.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent arg0) {
				repaintImage(CardSide.REAR);
			}

			@Override
			public void mouseDragged(MouseEvent arg0) {
				repaintImage(CardSide.REAR);
			}
		});
		// imgPanel.setLayout(null);
		// panel.setSize(new Dimension(860, 1800));
		spFront = new JScrollPane(pnFront);
		tabbedPane.addTab("Front", null, spFront, null);
		spFront.getVerticalScrollBar().setUnitIncrement(16);
		spFront.setMaximumSize(new Dimension(frontBuffer1.getWidth(), frontBuffer1.getHeight() < 600 ? frontBuffer1.getHeight() : 600));

		spBack = new JScrollPane(pnRear);
		spBack.setSize(new Dimension(rearBuffer1.getWidth(), rearBuffer1.getHeight() < 600 ? rearBuffer1.getHeight() : 600));
		spBack.setMaximumSize(new Dimension(rearBuffer1.getWidth(), rearBuffer1.getHeight() < 600 ? rearBuffer1.getHeight() : 600));
		spBack.getVerticalScrollBar().setUnitIncrement(16);
		tabbedPane.addTab("Rear", null, spBack, null);

		JPanel pnFields = new JPanel();
		tabbedPane.addTab("Fields", null, pnFields, null);
		pnFields.setLayout(new BorderLayout(0, 0));
		
		tabbedPane.setSelectedIndex(frontPanel ? 0 : 1);

		fieldRoot = new DefaultMutableTreeNode("root");
		JScrollPane spTree = new JScrollPane();
		spTree.getVerticalScrollBar().setUnitIncrement(16);
		pnFields.add(spTree, BorderLayout.WEST);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.3);
		pnFields.add(splitPane, BorderLayout.CENTER);
		treeFields = new JTree(fieldRoot);
		treeFields.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent tse) {
				Object obj = ((DefaultMutableTreeNode) tse.getPath().getLastPathComponent()).getUserObject();
				if (obj instanceof Field) {
					fieldEditior.setField((Field) obj);
				}
			}
		});
		treeFields.setRootVisible(false);
		treeFields.setShowsRootHandles(true);
		treeFields.expandRow(0);
		splitPane.setLeftComponent(treeFields);
		
		JPanel panel = new JPanel();
		splitPane.setRightComponent(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		Component verticalGlue_1 = Box.createVerticalGlue();
		panel.add(verticalGlue_1);
		
		Box horizontalBox_1 = Box.createHorizontalBox();
		panel.add(horizontalBox_1);
		
		
		fieldEditior = new JFieldEditorPanel(new Field("<none>", new Dimension(0, 0), new Dimension(10, 10), rectColor));
		horizontalBox_1.add(fieldEditior);
		
		Component horizontalGlue_2 = Box.createHorizontalGlue();
		horizontalBox_1.add(horizontalGlue_2);
		
		Component verticalGlue = Box.createVerticalGlue();
		panel.add(verticalGlue);
		
		Box horizontalBox_2 = Box.createHorizontalBox();
		panel.add(horizontalBox_2);
		
		JButton btnOkEditField = new JButton("Ok");
		btnOkEditField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fieldEditior.getField();
				treeFields.repaint();
				setImage(front, rear);
			}
		});
		
		Component horizontalGlue_6 = Box.createHorizontalGlue();
		horizontalBox_2.add(horizontalGlue_6);
		
		Component horizontalGlue_3 = Box.createHorizontalGlue();
		horizontalBox_2.add(horizontalGlue_3);
		horizontalBox_2.add(btnOkEditField);
		
		Component rigidArea = Box.createRigidArea(new Dimension(10, 20));
		horizontalBox_2.add(rigidArea);
		
		JButton btnDiscardChanges = new JButton("Discard changes");
		btnDiscardChanges.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fieldEditior.resetField();
			}
		});
		horizontalBox_2.add(btnDiscardChanges);
		
		Component horizontalGlue_4 = Box.createHorizontalGlue();
		horizontalBox_2.add(horizontalGlue_4);
		
		Component horizontalGlue_5 = Box.createHorizontalGlue();
		horizontalBox_2.add(horizontalGlue_5);
		
		JButton btnDeleteField = new JButton("Delete field");
		btnDeleteField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean approve = JOptionPane.showConfirmDialog(JFieldMappingPanel.this,
						"Delete field " + fieldEditior.getFieldName() + "?", "Delete field",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
				if(approve) {
					fieldEditior.resetField();
					JFieldMappingPanel.this.fields.remove(getFieldByName(fieldEditior.getFieldName()));
					setImage(frontImage, rearImage);
					populateFieldTree();
				}
			}
		});
		horizontalBox_2.add(btnDeleteField);

		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
				if (JFieldMappingPanel.this.getWidth() > (frontBuffer1.getWidth() < rearBuffer1.getWidth() ? frontBuffer1.getWidth() : rearBuffer1.getWidth()))
					JFieldMappingPanel.this.setSize(new Dimension(
							(frontBuffer1.getWidth() < rearBuffer1.getWidth() ? frontBuffer1.getWidth() : rearBuffer1.getWidth()),
							JFieldMappingPanel.this.getHeight()));
				if (JFieldMappingPanel.this.getHeight() > (frontBuffer1.getHeight() < rearBuffer1.getHeight() ? frontBuffer1.getHeight() : rearBuffer1.getHeight()))
					JFieldMappingPanel.this.setSize(new Dimension(JFieldMappingPanel.this.getWidth(),
							(frontBuffer1.getHeight() < rearBuffer1.getHeight() ? frontBuffer1.getHeight() : rearBuffer1.getHeight())));
			}
		});
		setImage(frontImage, rearImage);
		populateFieldTree();
		revalidate();
	}

	protected void handleFieldCreation(int x, int y, Field.CardSide side) {
		Dimension pos1 = (side == Field.CardSide.FRONT ? this.frontPos1 : this.backPos1);
		if (pos1 == null) {
			pos1 = new Dimension(x, y);
			if (side == Field.CardSide.FRONT)
				frontPos1 = pos1;
			else
				backPos1 = pos1;
			System.out.println(side + " X|Y Point 1: " + x + "|" + y);
		} else {
			System.out.println(side + " X|Y Point 2: " + x + "|" + y);
			System.out.println();
			String name = JOptionPane.showInputDialog(JFieldMappingPanel.this, "Enter a name");
			// Aborted
			if (name == null)
				return;
			name = name.trim(); 
			boolean link = false;
			while (checkNameForDuplicate(name) && !link || name.isEmpty()) {
				if (name.isEmpty())
					name = JOptionPane.showInputDialog(JFieldMappingPanel.this, "Enter a valid name");
				else {
					int approve = JOptionPane.showConfirmDialog(JFieldMappingPanel.this,
							"Name already taken, link this field to the existing?", "Choose an option",
							JOptionPane.YES_NO_OPTION);
					if (approve == JOptionPane.YES_OPTION)
						link = true;
					else
						name = JOptionPane.showInputDialog(JFieldMappingPanel.this, "Name already taken, enter a new name")
								.trim();
				}
				if (name == null)
					return;
				name = name.trim();
			}
//			FieldType type = (FieldType) JOptionPane.showInputDialog(CardMapper.this, "Choose the field type",
//					"Field type", JOptionPane.QUESTION_MESSAGE, null, // Use
//																		// default
//																		// icon
//					Field.FieldType.values(), // Array of choices
//					Field.FieldType.values()[0]); // Initial choice
			Field newField = new Field(name, pos1, new Dimension(x, y), rectColor, side);
			JFieldEditorPanel fe = new JFieldEditorPanel(newField);
			boolean ok = JOptionPane.showConfirmDialog(JFieldMappingPanel.this,
                    fe,
                    "Create Field",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
			if(ok) {
				fe.getField();
				if (link) {
					Field field = JFieldMappingPanel.this.getFieldByName(name);
					if (field != null)
						field.setLinkedField(newField);
					else
						JFieldMappingPanel.this.fields.add(newField);
				} else
					JFieldMappingPanel.this.fields.add(newField);
				Graphics2D g = side == Field.CardSide.FRONT ? frontBuffer1.createGraphics() : rearBuffer1.createGraphics();
				g.setColor(rectColor);
				newField.drawRect(g, CardCreator.getFont(newField.getFont()));
				g.dispose();
				repaintImage(side);
			}
			// imgPanel.setIcon(new ImageIcon(PositionGeneratorMagicCards.this.cPicture));
			if (side == Field.CardSide.FRONT)
				frontPos1 = null;
			else
				backPos1 = null;
			populateFieldTree();
			revalidate();
		}
	}

	private void setImage(BufferedImage frontImage, BufferedImage backImage) {
//		List<Field> frontFields = new ArrayList<>(getFrontFields());
//		List<Field> backFields = new ArrayList<>(getRearFields());
//		List<Field> frontAdditions = new ArrayList<>();
//		List<Field> backAdditions = new ArrayList<>();
//		
//		for (Field field : frontFields) {
//			Field linked = field;
//			while((linked = linked.getLinkedField()) != null) {
//				if (linked.getSide() == Field.CardSide.FRONT)
//					frontAdditions.add(linked);
//				else
//					backAdditions.add(linked);
//			}
//		}
//		for (Field field : backFields) {
//			Field linked = field;
//			while((linked = linked.getLinkedField()) != null) {
//				if (linked.getSide() == Field.CardSide.FRONT)
//					frontAdditions.add(linked);
//				else
//					backAdditions.add(linked);
//			}
//		}
//		frontFields.addAll(frontAdditions);
//		backFields.addAll(backAdditions);
		
		// Front
		if (frontImage != null) {
			this.front = frontImage;
			this.frontBuffer1 = new BufferedImage(frontImage.getWidth(), frontImage.getHeight(), frontImage.getType());
			this.frontBuffer2 = new BufferedImage(frontImage.getWidth(), frontImage.getHeight(), frontImage.getType());
			Graphics g = frontBuffer1.getGraphics();
			Graphics g2 = frontBuffer2.getGraphics();
			g.drawImage(frontImage, 0, 0, null);
//			for (Field f : frontFields) {
//				g.setColor(f.getColor());
//				f.drawRect(g, ProjectManager.getFont(JCardMapperPanel.this.p, f.getFont()));
//			}
			g.dispose();
			this.editor.drawFields(frontBuffer1, CardSide.FRONT, true, false);
			g2.drawImage(frontBuffer1, 0, 0, null);
			g2.dispose();
			this.editor.drawFields(frontBuffer2, CardSide.FRONT, false, true);
			this.pnFront.setIcon(new ImageIcon(frontBuffer2));
			pnFront.setPreferredSize(new Dimension(frontBuffer2.getWidth(), frontBuffer2.getHeight()));
			pnFront.setMaximumSize(new Dimension(frontBuffer2.getWidth(), frontBuffer2.getHeight()));
			pnFront.setSize(new Dimension(frontBuffer2.getWidth(), frontBuffer2.getHeight()));
			this.setMaximumSize(new Dimension(frontBuffer2.getWidth(), frontBuffer2.getHeight()));
//			this.setSize(new Dimension(b2Front.getWidth(), b2Front.getHeight()));
			spFront.setMaximumSize(new Dimension(frontBuffer1.getWidth(), frontBuffer2.getHeight() < 600 ? frontBuffer2.getHeight() : 600));
			spFront.setSize(new Dimension(frontBuffer1.getWidth(), frontBuffer2.getHeight() < 600 ? frontBuffer2.getHeight() : 600));
			pnFront.setSize(new Dimension(frontBuffer2.getWidth(), frontBuffer2.getHeight()));
			// Adjust size
			if (JFieldMappingPanel.this.getWidth() > frontBuffer1.getWidth())
				JFieldMappingPanel.this.setSize(new Dimension(frontBuffer1.getWidth(), JFieldMappingPanel.this.getHeight()));
			if (JFieldMappingPanel.this.getHeight() > frontBuffer1.getHeight())
				JFieldMappingPanel.this.setSize(new Dimension(JFieldMappingPanel.this.getWidth(), frontBuffer1.getHeight()));
		}
		// Back
		if (backImage != null) {
			this.rear = backImage;
			this.rearBuffer1 = new BufferedImage(backImage.getWidth(), backImage.getHeight(), backImage.getType());
			this.rearBuffer2 = new BufferedImage(backImage.getWidth(), backImage.getHeight(), backImage.getType());
			Graphics g = rearBuffer1.getGraphics();
			Graphics g2 = rearBuffer2.getGraphics();
			g.drawImage(backImage, 0, 0, null);
//			for (Field f : backFields) {
//				g.setColor(f.getColor());
//				f.drawRect(g, ProjectManager.getFont(JCardMapperPanel.this.p, f.getFont()));
//			}
			g.dispose();
			this.editor.drawFields(rearBuffer1, CardSide.REAR, true, false);
			g2.drawImage(rearBuffer1, 0, 0, null);
			g2.dispose();
			this.pnRear.setIcon(new ImageIcon(rearBuffer2));
			pnRear.setPreferredSize(new Dimension(rearBuffer2.getWidth(), rearBuffer2.getHeight()));
			pnRear.setMaximumSize(new Dimension(rearBuffer2.getWidth(), rearBuffer2.getHeight()));
			pnRear.setSize(new Dimension(rearBuffer2.getWidth(), rearBuffer2.getHeight()));
			this.setMaximumSize(new Dimension(rearBuffer2.getWidth(), rearBuffer2.getHeight()));
//			this.setSize(new Dimension(b2Rear.getWidth(), b2Rear.getHeight()));
			spBack.setMaximumSize(new Dimension(rearBuffer1.getWidth(), rearBuffer2.getHeight() < 600 ? rearBuffer2.getHeight() : 600));
			spBack.setSize(new Dimension(rearBuffer1.getWidth(), rearBuffer2.getHeight() < 600 ? rearBuffer2.getHeight() : 600));
			pnRear.setSize(new Dimension(rearBuffer2.getWidth(), rearBuffer2.getHeight()));
			if (JFieldMappingPanel.this.getWidth() > rearBuffer1.getWidth())
				JFieldMappingPanel.this.setSize(new Dimension(rearBuffer1.getWidth(), JFieldMappingPanel.this.getHeight()));
			if (JFieldMappingPanel.this.getHeight() > rearBuffer1.getHeight())
				JFieldMappingPanel.this.setSize(new Dimension(JFieldMappingPanel.this.getWidth(), rearBuffer1.getHeight()));
		}
		
		revalidate();
	}

	private void repaintImage(CardSide side) {
		if (previewMode)
			return;
		boolean front = side == CardSide.FRONT;
		Point m = front ? pnFront.getMousePosition() : pnRear.getMousePosition();
		Graphics g = front ? this.frontBuffer2.createGraphics() : this.rearBuffer2.createGraphics();
		g.drawImage(front ? this.frontBuffer1 : this.rearBuffer1, 0, 0, null);
		if (m != null) {
			g.setColor(rectColor);
			g.drawLine(0, m.y, front ? pnFront.getWidth() : pnRear.getWidth(), m.y);
			g.drawLine(m.x, 0, m.x, front ? pnFront.getHeight() : pnRear.getHeight());
			g.dispose();
			if (editor.isEditMode()) {
				int width = editor.getWidth();
				int height = editor.getHeight();
				int x = editor.getX();
				int y = editor.getY();
				if (width != -1 && height != -1 && x != -1 && y != -1) {
					switch (editCorner) {
					case TOP_LEFT:
						width = width + (x - m.x);
						height = height + (y - m.y);
						x = m.x;
						y = m.y;
						editor.setPos(x, y);
						editor.setWidth(width);
						editor.setHeight(height);
						break;
					case TOP_RIGHT:
						width = m.x - x;
						height = (height + y) - m.y;
						y = m.y;
						editor.setPos(x, y);
						editor.setWidth(width);
						editor.setHeight(height);
						break;
					case BOTTOM_LEFT:
						System.out.println("Old Rect: Point (" + x + "|" + y + "), Dimension ("+width+"x"+height+")");
						width = (width + x) - m.x;
						height = (m.y-y);
						x = m.x;
						System.out.println("New Rect: Point (" + x + "|" + y + "), Dimension ("+width+"x"+height+")");
						System.out.println();
						editor.setPos(x, y);
						editor.setWidth(width);
						editor.setHeight(height);
						break;
					case BOTTOM_RIGHT:
						width = m.x - x;
						height = m.y - y;
						editor.setWidth(width);
						editor.setHeight(height);
						break;
					default:
						break;
					}
				}
			}
			// System.out.println("Edit mode: " + this.editor.isEditMode());
			// System.out.println("X|Y: " + m.x + "|" + m.y);
		}
		this.editor.drawFields(front ? this.frontBuffer2 : this.rearBuffer2, side, false, true);
		if (front)
			pnFront.repaint();
		else
			pnRear.repaint();
	}

	private void populateFieldTree() {
		treeFields.setRootVisible(true);
		this.fieldRoot.removeAllChildren();
		DefaultMutableTreeNode frontNode = new DefaultMutableTreeNode("Front");
		DefaultMutableTreeNode backNode = new DefaultMutableTreeNode("Back");
		
		List<Field> frontFields = this.getFrontFields();
		List<Field> backFields = this.getRearFields();

		for (Field field : frontFields) {
			System.out.println(field);
			frontNode.add(createFieldNode(field));
		}
		for (Field field : backFields) {
			backNode.add(createFieldNode(field));
		}
		
		this.fieldRoot.add(frontNode);
		this.fieldRoot.add(backNode);
		((DefaultTreeModel)this.treeFields.getModel()).setRoot(fieldRoot);
		treeFields.expandRow(100);
		this.treeFields.setRootVisible(false);
	}

	private MutableTreeNode createFieldNode(Field field) {
		DefaultMutableTreeNode fieldNode = new DefaultMutableTreeNode(field);
		if (field.hasLinkedField()) {
			fieldNode.add(createFieldNode(field.getLinkedField()));
		}
		return fieldNode;
	}

	public List<Field> getFrontFields() {
		return this.fields.stream().filter(f -> f.getSide() == CardSide.FRONT).collect(Collectors.toList());
	}

	public List<Field> getRearFields() {
		return this.fields.stream().filter(f -> f.getSide() == CardSide.REAR).collect(Collectors.toList());
	}

	public boolean checkNameForDuplicate(final String name) {
		return fields.stream().anyMatch(f -> f.getName().equalsIgnoreCase(name));
	}

	public Field getFieldByName(final String name) {
		return fields.stream().filter(f -> f.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	public List<Field> getFields() {
		return fields;
	}

	private void updatePreviewMode() {
		if (previewMode) {
			BufferedImage[] images = CardCreator.createCard(0, null, this.fields);
			if (images != null) {
				this.frontPreview = images[0];
				this.rearPreview = images[1];
			}
			else {
				this.frontPreview = null;
				this.rearPreview = null;
				this.previewMode = false;
				this.chckbxPreview.setSelected(false);
			}
			if (this.frontPreview != null) {
				pnFront.setIcon(new ImageIcon(this.frontPreview));
				pnRear.setIcon(new ImageIcon(this.rearPreview));
			}
		}
		else {
			pnFront.setIcon(new ImageIcon(this.frontBuffer2));
			pnRear.setIcon(new ImageIcon(this.rearBuffer2));
		}
	}
}
