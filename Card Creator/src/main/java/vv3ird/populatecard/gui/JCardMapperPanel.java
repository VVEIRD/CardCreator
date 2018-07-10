package vv3ird.populatecard.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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

import vv3ird.populatecard.control.FieldEditor;
import vv3ird.populatecard.control.FieldEditor.Corner;
import vv3ird.populatecard.control.ProjectManager;
import vv3ird.populatecard.data.Field;
import vv3ird.populatecard.data.Field.CardSide;
import vv3ird.populatecard.data.FieldPackage;
import vv3ird.populatecard.data.Project;

public class JCardMapperPanel extends JPanel {

	private static final long serialVersionUID = 6755160427559608684L;

	private JPanel contentPane;

	private JScrollPane spFront;

	private JLabel pnFront;

	private JLabel pnBack;

	private DefaultMutableTreeNode fieldRoot = null;

	private BufferedImage orgFront = null;
	private BufferedImage b1Front = null;
	private BufferedImage b2Front = null;

	private BufferedImage orgRear = null;
	private BufferedImage b1Rear = null;
	private BufferedImage b2Rear = null;

	Dimension frontPos1 = null;
	Dimension backPos1 = null;
	
	private Project p;

	private FieldPackage fp = new FieldPackage();
	
	private List<Field> fields = new LinkedList<>();
	
	private FieldEditor editor = null;
	
	private Corner editCorner = Corner.NONE;

	private Color rectColor = Color.GREEN;
	private JScrollPane spBack;
	private JTree treeFields;
	private JFieldEditorPanel fieldEditior;


	/**
	 * Create the frame.
	 * 
	 * @throws IOException
	 */
	public JCardMapperPanel(Project p) {
		this.p = p;
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
		this.fp = p.getFp();
		this.fields.addAll(fp.getFields());
		this.editor = new FieldEditor(this.fields);
		contentPane.setLayout(new BorderLayout(0, 0));
		// Front image
		this.orgFront = frontImage;
		this.b1Front = new BufferedImage(frontImage.getWidth(), frontImage.getHeight(), frontImage.getType());
		this.b2Front = new BufferedImage(frontImage.getWidth(), frontImage.getHeight(), frontImage.getType());
		Graphics g = b1Front.getGraphics();
		Graphics g2 = b2Front.getGraphics();
		g.drawImage(frontImage, 0, 0, null);
		g.dispose();
		g2.drawImage(frontImage, 0, 0, null);
		g2.dispose();
		// Back image
		this.orgRear = rearImage;
		this.b1Rear = new BufferedImage(rearImage.getWidth(), rearImage.getHeight(), rearImage.getType());
		this.b2Rear = new BufferedImage(rearImage.getWidth(), rearImage.getHeight(), rearImage.getType());
		g = b1Front.getGraphics();
		g2 = b2Front.getGraphics();
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

					JFileChooser chooser = new JFileChooser(cmDir) {
						private static final long serialVersionUID = 1L;

						@Override
						public void approveSelection() {
							File f = getSelectedFile();
							if (f.exists() && getDialogType() == SAVE_DIALOG) {
								int result = JOptionPane.showConfirmDialog(this, "The file exists, overwrite?",
										"Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
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
					};
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					chooser.setAcceptAllFileFilterUsed(false);

					chooser.setDialogTitle("Choose a filename");
					chooser.setFileFilter(new FileNameExtensionFilter("CardMapper Files", new String[] { "cm" }));
					int res = chooser.showSaveDialog(JCardMapperPanel.this);
					if (res == JFileChooser.APPROVE_OPTION) {
						File f = chooser.getSelectedFile();
						FieldPackage fPackage = new FieldPackage(orgFront, orgRear);
						fPackage.addFields(fields);
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
				JCardMapperPanel.this.editor.saveField();
				JCardMapperPanel.this.setImage(orgFront, orgRear);
				JCardMapperPanel.this.repaintImage(CardSide.FRONT);
				JCardMapperPanel.this.repaintImage(CardSide.REAR);
				btnSaveResize.setEnabled(false);
			}
		});
		btnSaveResize.setEnabled(false);
		btnSaveResize.setAlignmentY(Component.TOP_ALIGNMENT);
		panel_1.add(btnSaveResize);
		
		JLabel label_2 = new JLabel(" ");
		panel_1.add(label_2);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		panel_1.add(horizontalGlue);
		
		Box horizontalBox = Box.createHorizontalBox();
		horizontalBox.setAlignmentX(Component.RIGHT_ALIGNMENT);
		panel_1.add(horizontalBox);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		pnFront = new JLabel(new ImageIcon(b2Front));
		pnFront.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				JCardMapperPanel.this.editCorner = Corner.NONE;
				if (JCardMapperPanel.this.editor.isEditMode()) {
					if (!JCardMapperPanel.this.editor.editFieldContains(e.getPoint())) {
						JCardMapperPanel.this.editor.discardEdits();
						JCardMapperPanel.this.setImage(orgFront, orgRear);
						JCardMapperPanel.this.repaintImage(CardSide.FRONT);
						btnSaveResize.setEnabled(false);
					}
				} else if (!JCardMapperPanel.this.editor.isEditMode()) {
					if (JCardMapperPanel.this.editor.editFieldContaining(e.getPoint(), CardSide.FRONT)) {
						JCardMapperPanel.this.setImage(orgFront, orgRear);
						JCardMapperPanel.this.repaintImage(CardSide.FRONT);
						btnSaveResize.setEnabled(true);
					} else
						handleFieldCreation(e.getX(), e.getY(), Field.CardSide.FRONT);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (JCardMapperPanel.this.editor.isEditMode()) {
					editCorner = JCardMapperPanel.this.editor.getCornerOnEditField(e.getPoint());
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
		pnBack = new JLabel(new ImageIcon(b2Rear));
		pnBack.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				JCardMapperPanel.this.editCorner = Corner.NONE;
				if (JCardMapperPanel.this.editor.isEditMode()) {
					if (!JCardMapperPanel.this.editor.editFieldContains(e.getPoint())) {
						JCardMapperPanel.this.editor.discardEdits();
						JCardMapperPanel.this.setImage(orgFront, orgRear);
						JCardMapperPanel.this.repaintImage(CardSide.REAR);
						btnSaveResize.setEnabled(false);
					}
				} else if (!JCardMapperPanel.this.editor.isEditMode()) {
					if (JCardMapperPanel.this.editor.editFieldContaining(e.getPoint(), CardSide.REAR)) {
						JCardMapperPanel.this.setImage(orgFront, orgRear);
						JCardMapperPanel.this.repaintImage(CardSide.REAR);
						btnSaveResize.setEnabled(true);
					} else
						handleFieldCreation(e.getX(), e.getY(), Field.CardSide.REAR);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (JCardMapperPanel.this.editor.isEditMode()) {
					editCorner = JCardMapperPanel.this.editor.getCornerOnEditField(e.getPoint());
					if(editCorner != Corner.NONE) {
						System.out.println("Corner selected: " + editCorner);
					}
				}
			}

			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		pnBack.addMouseMotionListener(new MouseMotionListener() {
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
		spFront.setMaximumSize(new Dimension(b1Front.getWidth(), b1Front.getHeight() < 600 ? b1Front.getHeight() : 600));

		spBack = new JScrollPane(pnBack);
		spBack.setSize(new Dimension(b1Rear.getWidth(), b1Rear.getHeight() < 600 ? b1Rear.getHeight() : 600));
		spBack.setMaximumSize(new Dimension(b1Rear.getWidth(), b1Rear.getHeight() < 600 ? b1Rear.getHeight() : 600));
		spBack.getVerticalScrollBar().setUnitIncrement(16);
		tabbedPane.addTab("Rear", null, spBack, null);

		JPanel pnFields = new JPanel();
		tabbedPane.addTab("Fields", null, pnFields, null);
		pnFields.setLayout(new BorderLayout(0, 0));

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
		
		
		fieldEditior = new JFieldEditorPanel(new Field("<none>", new Dimension(0, 0), new Dimension(10, 10), rectColor), this.p);
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
				setImage(orgFront, orgRear);
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
				boolean approve = JOptionPane.showConfirmDialog(JCardMapperPanel.this,
						"Delete field " + fieldEditior.getFieldName() + "?", "Delete field",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
				if(approve) {
					fieldEditior.resetField();
					JCardMapperPanel.this.fields.remove(getFieldByName(fieldEditior.getFieldName()));
					setImage(frontImage, rearImage);
					populateFieldTree();
				}
			}
		});
		horizontalBox_2.add(btnDeleteField);

		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
				if (JCardMapperPanel.this.getWidth() > (b1Front.getWidth() < b1Rear.getWidth() ? b1Front.getWidth() : b1Rear.getWidth()))
					JCardMapperPanel.this.setSize(new Dimension(
							(b1Front.getWidth() < b1Rear.getWidth() ? b1Front.getWidth() : b1Rear.getWidth()),
							JCardMapperPanel.this.getHeight()));
				if (JCardMapperPanel.this.getHeight() > (b1Front.getHeight() < b1Rear.getHeight() ? b1Front.getHeight() : b1Rear.getHeight()))
					JCardMapperPanel.this.setSize(new Dimension(JCardMapperPanel.this.getWidth(),
							(b1Front.getHeight() < b1Rear.getHeight() ? b1Front.getHeight() : b1Rear.getHeight())));
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
			String name = JOptionPane.showInputDialog(JCardMapperPanel.this, "Enter a name");
			// Aborted
			if (name == null)
				return;
			name = name.trim(); 
			boolean link = false;
			while (checkNameForDuplicate(name) && !link || name.isEmpty()) {
				if (name.isEmpty())
					name = JOptionPane.showInputDialog(JCardMapperPanel.this, "Enter a valid name");
				else {
					int approve = JOptionPane.showConfirmDialog(JCardMapperPanel.this,
							"Name already taken, link this field to the existing?", "Choose an option",
							JOptionPane.YES_NO_OPTION);
					if (approve == JOptionPane.YES_OPTION)
						link = true;
					else
						name = JOptionPane.showInputDialog(JCardMapperPanel.this, "Name already taken, enter a new name")
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
			JFieldEditorPanel fe = new JFieldEditorPanel(newField, p);
			boolean ok = JOptionPane.showConfirmDialog(JCardMapperPanel.this,
                    fe,
                    "Create Field",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
			if(ok) {
				fe.getField();
				if (link) {
					Field field = JCardMapperPanel.this.getFieldByName(name);
					if (field != null)
						field.setLinkedField(newField);
					else
						JCardMapperPanel.this.fields.add(newField);
				} else
					JCardMapperPanel.this.fields.add(newField);
				Graphics2D g = side == Field.CardSide.FRONT ? b1Front.createGraphics() : b1Rear.createGraphics();
				g.setColor(rectColor);
				newField.drawRect(g, ProjectManager.getFont(JCardMapperPanel.this.p, newField.getFont()));
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
			this.orgFront = frontImage;
			this.b1Front = new BufferedImage(frontImage.getWidth(), frontImage.getHeight(), frontImage.getType());
			this.b2Front = new BufferedImage(frontImage.getWidth(), frontImage.getHeight(), frontImage.getType());
			Graphics g = b1Front.getGraphics();
			Graphics g2 = b2Front.getGraphics();
			g.drawImage(frontImage, 0, 0, null);
//			for (Field f : frontFields) {
//				g.setColor(f.getColor());
//				f.drawRect(g, ProjectManager.getFont(JCardMapperPanel.this.p, f.getFont()));
//			}
			g.dispose();
			this.editor.drawFields(b1Front, CardSide.FRONT, true, false);
			g2.drawImage(b1Front, 0, 0, null);
			g2.dispose();
			this.editor.drawFields(b2Front, CardSide.FRONT, false, true);
			this.pnFront.setIcon(new ImageIcon(b2Front));
			pnFront.setPreferredSize(new Dimension(b2Front.getWidth(), b2Front.getHeight()));
			pnFront.setMaximumSize(new Dimension(b2Front.getWidth(), b2Front.getHeight()));
			pnFront.setSize(new Dimension(b2Front.getWidth(), b2Front.getHeight()));
			this.setMaximumSize(new Dimension(b2Front.getWidth(), b2Front.getHeight()));
//			this.setSize(new Dimension(b2Front.getWidth(), b2Front.getHeight()));
			spFront.setMaximumSize(new Dimension(b1Front.getWidth(), b2Front.getHeight() < 600 ? b2Front.getHeight() : 600));
			spFront.setSize(new Dimension(b1Front.getWidth(), b2Front.getHeight() < 600 ? b2Front.getHeight() : 600));
			pnFront.setSize(new Dimension(b2Front.getWidth(), b2Front.getHeight()));
			// Adjust size
			if (JCardMapperPanel.this.getWidth() > b1Front.getWidth())
				JCardMapperPanel.this.setSize(new Dimension(b1Front.getWidth(), JCardMapperPanel.this.getHeight()));
			if (JCardMapperPanel.this.getHeight() > b1Front.getHeight())
				JCardMapperPanel.this.setSize(new Dimension(JCardMapperPanel.this.getWidth(), b1Front.getHeight()));
		}
		// Back
		if (backImage != null) {
			this.orgRear = backImage;
			this.b1Rear = new BufferedImage(backImage.getWidth(), backImage.getHeight(), backImage.getType());
			this.b2Rear = new BufferedImage(backImage.getWidth(), backImage.getHeight(), backImage.getType());
			Graphics g = b1Rear.getGraphics();
			Graphics g2 = b2Rear.getGraphics();
			g.drawImage(backImage, 0, 0, null);
//			for (Field f : backFields) {
//				g.setColor(f.getColor());
//				f.drawRect(g, ProjectManager.getFont(JCardMapperPanel.this.p, f.getFont()));
//			}
			g.dispose();
			this.editor.drawFields(b1Rear, CardSide.REAR, true, false);
			g2.drawImage(b1Rear, 0, 0, null);
			g2.dispose();
			this.pnBack.setIcon(new ImageIcon(b2Rear));
			pnBack.setPreferredSize(new Dimension(b2Rear.getWidth(), b2Rear.getHeight()));
			pnBack.setMaximumSize(new Dimension(b2Rear.getWidth(), b2Rear.getHeight()));
			pnBack.setSize(new Dimension(b2Rear.getWidth(), b2Rear.getHeight()));
			this.setMaximumSize(new Dimension(b2Rear.getWidth(), b2Rear.getHeight()));
//			this.setSize(new Dimension(b2Rear.getWidth(), b2Rear.getHeight()));
			spBack.setMaximumSize(new Dimension(b1Rear.getWidth(), b2Rear.getHeight() < 600 ? b2Rear.getHeight() : 600));
			spBack.setSize(new Dimension(b1Rear.getWidth(), b2Rear.getHeight() < 600 ? b2Rear.getHeight() : 600));
			pnBack.setSize(new Dimension(b2Rear.getWidth(), b2Rear.getHeight()));
			if (JCardMapperPanel.this.getWidth() > b1Rear.getWidth())
				JCardMapperPanel.this.setSize(new Dimension(b1Rear.getWidth(), JCardMapperPanel.this.getHeight()));
			if (JCardMapperPanel.this.getHeight() > b1Rear.getHeight())
				JCardMapperPanel.this.setSize(new Dimension(JCardMapperPanel.this.getWidth(), b1Rear.getHeight()));
		}
		
		revalidate();
	}

	private void repaintImage(CardSide side) {
		boolean front = side == CardSide.FRONT;
		Point m = front ? pnFront.getMousePosition() : pnBack.getMousePosition();
		Graphics g = front ? this.b2Front.createGraphics() : this.b2Rear.createGraphics();
		g.drawImage(front ? this.b1Front : this.b1Rear, 0, 0, null);
		if (m != null) {
			g.setColor(rectColor);
			g.drawLine(0, m.y, front ? pnFront.getWidth() : pnBack.getWidth(), m.y);
			g.drawLine(m.x, 0, m.x, front ? pnFront.getHeight() : pnBack.getHeight());
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
		this.editor.drawFields(front ? this.b2Front : this.b2Rear, side, false, true);
		if (front)
			pnFront.repaint();
		else
			pnBack.repaint();
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
}
