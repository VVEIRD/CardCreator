package de.rcblum.populatecard.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.rcblum.populatecard.data.Field;
import de.rcblum.populatecard.data.FieldPackage;
import de.rcblum.populatecard.data.Project;
import de.rcblum.populatecard.data.ProjectManager;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.border.BevelBorder;
import javax.swing.BoxLayout;
import javax.swing.JTextField;

public class Main extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8050980040476237624L;
	private JPanel contentPane;
	private JMenu mnRecentProjects;
	protected Project currentProject = null;
	private JMenu mnDeleteFont;
	private JMenu mnFonts;
	
	BufferedImage frontImage;
	BufferedImage rearImage;
	private JButton btnLoadFrontSide;
	private JButton btnLoadBackSide;
	private JLabel pnFrontPreview;
	private JLabel pnRearPreview;
	private JPanel pnStatus;
	private JStatusLabel lblStatus;
	private JMenuItem mntmMapFields;
	private JMenuItem mntmImportCmFile;
	private JMenuItem mntmImportFont;
	private JTextField tfFileNameTemplate;
	private JButton btnI;
	private JButton btnCreateCards;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
            // Set cross-platform Java L&F (also called "Metal")
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
    } 
    catch (UnsupportedLookAndFeelException e) {
       // handle exception
    }
    catch (ClassNotFoundException e) {
       // handle exception
    }
    catch (InstantiationException e) {
       // handle exception
    }
    catch (IllegalAccessException e) {
       // handle exception
    }
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		setTitle("Card Creator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 325, 436);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmNewProject = new JMenuItem("New project");
		mntmNewProject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean ex = false;
				String name = null;
				do {
					name = JOptionPane.showInputDialog(Main.this, ex ? "Name already used, enter a new name" : "Enter a name", "New Project...", JOptionPane.INFORMATION_MESSAGE);
					if (name == null)
						return;
					ex = ProjectManager.checkForDuplicates(name);
				} while (ex);
				try {
					Main.this.currentProject  = ProjectManager.createEmptyProject(name);
					Main.this.setTitle("Create Cards: " + Main.this.currentProject.getName());
					Main.this.btnLoadFrontSide.setEnabled(true);
					Main.this.btnLoadBackSide.setEnabled(true);
					populateRecentProjects();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		mnFile.add(mntmNewProject);
		
		JMenuItem mntmOpenProject = new JMenuItem("Open project");
		mntmOpenProject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String cmDir = System.getProperty("user.dir") + File.separator + "projects";
				try {
					if (!Files.exists(Paths.get(cmDir)))
						Files.createDirectories(Paths.get(cmDir));

					JFileChooser chooser = new JFileChooser(cmDir);
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					chooser.setAcceptAllFileFilterUsed(false);
					chooser.setDialogTitle("Choose a filename");
					chooser.setFileFilter(new FileNameExtensionFilter("CardMapperProject Files", new String[] { "cmp" }));
					int res = chooser.showOpenDialog(Main.this);
					if (res == JFileChooser.APPROVE_OPTION) {
						Path selectedProject  = chooser.getSelectedFile().toPath();
						openProject(selectedProject.toString());
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		mnFile.add(mntmOpenProject);
		
		JMenuItem mntmSaveProject = new JMenuItem("Save project");
		mntmSaveProject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (Main.this.currentProject != null)
					Main.this.currentProject.setFileNameTemplate(Main.this.tfFileNameTemplate.getText().trim());
					try {
						lblStatus.setText("Saving project file...");
						ProjectManager.saveProject(Main.this.currentProject);
						lblStatus.setText("Project file saved");
						populateRecentProjects();
					} catch (IOException e) {
						System.out.println("Error saving project file");
						lblStatus.setText("Error: Unable to save project file: " + e.getMessage());
						e.printStackTrace();
					}
			}
		});
		mnFile.add(mntmSaveProject);
		
		mnRecentProjects = new JMenu("Recent projects");
		mnFile.add(mnRecentProjects);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		mntmMapFields = new JMenuItem("Field mappings");
		mntmMapFields.setEnabled(false);
		mntmMapFields.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardMapper cm = new CardMapper(Main.this.currentProject);
				boolean ok = JOptionPane.showConfirmDialog(Main.this,
                        cm,
                        "Map Fields",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
				if(ok) {
					List<Field> fields = cm.getFields();
					if (fields != null) {
						Main.this.currentProject.getFp().clearFields();
						Main.this.currentProject.getFp().addFields(fields);
					}
				}
			}
		});
		mnEdit.add(mntmMapFields);
		mnEdit.addSeparator();
		mntmImportCmFile = new JMenuItem("Import CM File");
		mntmImportCmFile.setEnabled(false);
		mntmImportCmFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String cmDir = System.getProperty("user.dir") + File.separator + "projects";
				try {
					if (!Files.exists(Paths.get(cmDir)))
						Files.createDirectories(Paths.get(cmDir));

					JFileChooser chooser = new JFileChooser(cmDir);
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					chooser.setAcceptAllFileFilterUsed(false);
					chooser.setDialogTitle("import CM File");
					chooser.setFileFilter(new FileNameExtensionFilter("CardMapper Files", new String[] { "cm" }));
					int res = chooser.showOpenDialog(Main.this);
					if (res == JFileChooser.APPROVE_OPTION) {
						chooser.getSelectedFile();
						FieldPackage fp = FieldPackage.load(chooser.getSelectedFile().toPath());
						boolean overwrite = JOptionPane.showConfirmDialog(Main.this, "OVerwrite existing fields and images?" , "Import CM File", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
						if(overwrite) {
							Main.this.currentProject.setFp(fp);
							loadFrontImage(fp.getFrontImage());
							loadRearImage(fp.getRearImage());
							revalidate();
						}
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		mnEdit.add(mntmImportCmFile);
		
		mnFonts = new JMenu("Fonts");
		menuBar.add(mnFonts);
		
		mntmImportFont = new JMenuItem("Import Font");
		mntmImportFont.setEnabled(false);
		mntmImportFont.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String cmDir = System.getProperty("user.dir") + File.separator + "fonts";
				try {
					if (!Files.exists(Paths.get(cmDir)))
						Files.createDirectories(Paths.get(cmDir));

					JFileChooser chooser = new JFileChooser(cmDir);
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					chooser.setAcceptAllFileFilterUsed(false);
					chooser.setDialogTitle("Choose a filename");
					chooser.setFileFilter(new FileNameExtensionFilter("CardMapper Files", new String[] { "ttf" }));
					int res = chooser.showOpenDialog(Main.this);
					if (res == JFileChooser.APPROVE_OPTION) {
						Path selectedProject  = chooser.getSelectedFile().toPath();
						String fontName = importFont(selectedProject.toString());
						lblStatus.setText("Successfully imported font " + fontName);
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		mnFonts.add(mntmImportFont);
		
		mnDeleteFont = new JMenu("Delete Font");
		mnFonts.add(mnDeleteFont);
		mnFonts.addSeparator();
		
		JMenu mnCsv = new JMenu("CSV");
		menuBar.add(mnCsv);
		
		JMenuItem mntmImport = new JMenuItem("Import CSV");
		mntmImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String cmDir = System.getProperty("user.dir") + File.separator + "csv";
				try {
					if (!Files.exists(Paths.get(cmDir)))
						Files.createDirectories(Paths.get(cmDir));

					JFileChooser chooser = new JFileChooser(cmDir);
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					chooser.setAcceptAllFileFilterUsed(false);
					chooser.setDialogTitle("Choose a filename");
					chooser.setFileFilter(new FileNameExtensionFilter("CSV file", new String[] { "csv" }));
					int res = chooser.showOpenDialog(Main.this);
					if (res == JFileChooser.APPROVE_OPTION) {
						Path selectedCSV  = chooser.getSelectedFile().toPath();
						ProjectManager.importCsv(currentProject, selectedCSV);
						lblStatus.setText("Successfully imported csv-file " + selectedCSV.getFileName().toString());
						CSVMapper cm = new CSVMapper(Main.this.currentProject);
						boolean ok = JOptionPane.showConfirmDialog(Main.this,
		                        cm,
		                        "Map CSV to Fields",
		                        JOptionPane.OK_CANCEL_OPTION,
		                        JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
						if(ok) {
							Map<String, String> mapping = cm.getMappings();
							for (String field : mapping.keySet()) {
								currentProject.addMapping(field, mapping.get(field));
							}
						}
						btnCreateCards.setEnabled(true);
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		mnCsv.add(mntmImport);
		
		JMenuItem mntmMap = new JMenuItem("Map CSV to fields");
		mnCsv.add(mntmMap);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JLabel lblFrontSide = new JLabel("Front side");
		lblFrontSide.setHorizontalAlignment(SwingConstants.CENTER);
		lblFrontSide.setBounds(10, 11, 133, 14);
		panel.add(lblFrontSide);
		frontImage = new BufferedImage(133, 199, BufferedImage.TYPE_INT_ARGB);
		pnFrontPreview = new JLabel(new ImageIcon(frontImage));
		pnFrontPreview.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnFrontPreview.setBounds(10, 36, 133, 199);
		panel.add(pnFrontPreview);
		
		btnLoadFrontSide = new JButton("Load image");
		btnLoadFrontSide.setEnabled(false);
		btnLoadFrontSide.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BufferedImage img = chooseImage();
				if (img != null) {
					boolean delete = JOptionPane.showConfirmDialog(Main.this, "Delete existing fields for the front side?" , "Delete Fields", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
					Main.this.currentProject.getFp().setFrontImage(img, delete);
					loadFrontImage(img);
					System.out.println("Image Loaded");
				}
			}
		});
		btnLoadFrontSide.setBounds(10, 246, 133, 23);
		panel.add(btnLoadFrontSide);
		
		JLabel lblBackSide = new JLabel("Back side");
		lblBackSide.setHorizontalAlignment(SwingConstants.CENTER);
		lblBackSide.setBounds(153, 11, 133, 14);
		panel.add(lblBackSide);
		rearImage = new BufferedImage(133, 199, BufferedImage.TYPE_INT_ARGB);
		pnRearPreview = new JLabel(new ImageIcon(rearImage));
		pnRearPreview.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnRearPreview.setBounds(163, 36, 133, 199);
		panel.add(pnRearPreview);
		
		btnLoadBackSide = new JButton("Load back image");
		btnLoadBackSide.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BufferedImage img = chooseImage();
				if (img != null) {
					boolean delete = JOptionPane.showConfirmDialog(Main.this, "Delete existing fields for the rear side?" , "Delete Fields", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
					Main.this.currentProject.getFp().setRearImage(img, delete);
					loadRearImage(img);
					System.out.println("Back Image Loaded");
				}
			}
		});
		btnLoadBackSide.setEnabled(false);
		btnLoadBackSide.setBounds(163, 246, 133, 23);
		panel.add(btnLoadBackSide);
		
		btnCreateCards = new JButton("Create Cards");
		btnCreateCards.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Main.this.currentProject != null) {
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Project.drawCards(Main.this.currentProject, lblStatus);
								lblStatus.setText("Drawing cards finished");
							} catch (IOException e1) {
								lblStatus.setText("Error drawing cards: " + e1.getMessage());
								e1.printStackTrace();
							}
						}
					});
					t.start();
				}
			}
		});
		btnCreateCards.setToolTipText("Import CSV first");
		btnCreateCards.setEnabled(false);
		btnCreateCards.setBounds(10, 280, 286, 23);
		panel.add(btnCreateCards);
		
		tfFileNameTemplate = new JTextField();
		tfFileNameTemplate.setEnabled(false);
		tfFileNameTemplate.setBounds(10, 328, 256, 20);
		panel.add(tfFileNameTemplate);
		tfFileNameTemplate.setColumns(10);
		
		btnI = new JButton("i");
		btnI.setEnabled(false);
		btnI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<String> fileOptions = new LinkedList<>();
				fileOptions.add("Internal Variables");
				fileOptions.add(" {no} - card number");
				fileOptions.add(" {side} - card side (front|back)");
				if (Main.this.currentProject.getCsvHeader() != null) {
					fileOptions.add("Fields from the CSV:");
					fileOptions.addAll(Main.this.currentProject.getCsvHeader().keySet().stream().map(s -> " {" + s + "}").collect(Collectors.toList()));
				}
				JPanel info = new JPanel();
				info.setLayout(null);
				int c = 0;
				for (String option : fileOptions) {
					JLabel l = new JLabel(option);
					l.setBounds(0, c++ * 24, 200, 20);
					info.add(l);
				}
				info.setBounds(0, 0, 200, c*24);
				info.setSize(new Dimension(200, c*24));
				info.setPreferredSize(new Dimension(200, c*24));
				info.setMinimumSize(new Dimension(200, c*24));
				info.setMaximumSize(new Dimension(200, c*24));
//				JOptionPane pane = new JOptionPane("Placeholder for card filenames", JOptionPane.INFORMATION_MESSAGE);
				JDialog dia = new JDialog(Main.this, "Placeholder for card filenames", false);
				dia.getContentPane().add(info, BorderLayout.CENTER);
				dia.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				Point p = Main.this.getLocation();
				p = new Point((int)p.getX()+250, (int)p.getY()+100);
				dia.setLocation(p);
				dia.pack();
				dia.setVisible(true);
//				JOptionPane.showConfirmDialog(Main.this,
//                        info,
//                        "Placeholder for card filenames",
//                        JOptionPane.OK_OPTION,
//                        JOptionPane.PLAIN_MESSAGE);
			}
		});
		btnI.setBounds(271, 327, 25, 23);
		panel.add(btnI);
		
		JLabel lblFilenameTemplate = new JLabel("Filename template");
		lblFilenameTemplate.setBounds(10, 314, 133, 14);
		panel.add(lblFilenameTemplate);
		
		pnStatus = new JPanel();
		pnStatus.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnStatus.setPreferredSize(new Dimension(this.getWidth(), 16));
		contentPane.add(pnStatus, BorderLayout.SOUTH);
		pnStatus.setLayout(new BoxLayout(pnStatus, BoxLayout.X_AXIS));
		
		lblStatus = new JStatusLabel("Status");
		lblStatus.setHorizontalAlignment(SwingConstants.LEFT);
		pnStatus.add(lblStatus);
		populateRecentProjects();
	}
	

	private void loadFrontImage(BufferedImage img) {
		if (img != null) {
			Image smal = img.getScaledInstance(133, 199, BufferedImage.SCALE_SMOOTH);
			Graphics2D g = frontImage.createGraphics();
			g.drawImage(smal, 0, 0, null);
			g.dispose();
			pnFrontPreview.repaint();
		}
		else {
			Graphics2D g = frontImage.createGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, frontImage.getWidth(), frontImage.getHeight());
			g.dispose();
			pnFrontPreview.repaint();
		}
	}
	
	private void loadRearImage(BufferedImage img) {
		if (img != null) {
			Image smal = img.getScaledInstance(133, 199, BufferedImage.SCALE_SMOOTH);
			Graphics2D g = rearImage.createGraphics();
			g.drawImage(smal, 0, 0, null);
			g.dispose();
			pnRearPreview.repaint();
		}
		else {
			Graphics2D g = rearImage.createGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, rearImage.getWidth(), rearImage.getHeight());
			g.dispose();
			pnRearPreview.repaint();
		}
	}

	private BufferedImage chooseImage() {
		JFileChooser chooser = new JFileChooser(
				System.getProperty("user.home") + System.getProperty("file.separator") + "Pictures");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setDialogTitle("Select the front image, all rectangles will be discarded");
		chooser.setFileFilter(new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes()));
		int res = chooser.showOpenDialog(null);
		if (res == JFileChooser.APPROVE_OPTION) {
			System.out.println("Load Image...");
			File file = chooser.getSelectedFile();
			String path = chooser.getSelectedFile().toString();
			try {
				return ImageIO.read(new File(path));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private void populateRecentProjects() {
		//mnRecentProjects;
		List<String> recent = new ArrayList<>(ProjectManager.getRecentProjects());
		Collections.reverse(recent);
		mnRecentProjects.removeAll();
		for (String pro : recent) {
			JMenuItem jmit = new JMenuItem(pro);
			jmit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						openProject(((JMenuItem)arg0.getSource()).getText());
					} catch (IOException e) {
						System.out.println("Error opening project");
						lblStatus.setText("Error: Project could not be opened");
						e.printStackTrace();
					}
				}
			});
			mnRecentProjects.add(jmit);
		}
		mnRecentProjects.revalidate();
	}

	private void openProject(String selectedProject) throws IOException {
		Main.this.currentProject = ProjectManager.openProject(selectedProject);
		Main.this.setTitle("Create Cards: " + Main.this.currentProject.getName());
		Main.this.loadFrontImage(Main.this.currentProject.getFp().getFrontImage());
		Main.this.loadRearImage(Main.this.currentProject.getFp().getRearImage());
		Main.this.tfFileNameTemplate.setText(Main.this.currentProject.getFileNameTemplate());
		Main.this.btnLoadFrontSide.setEnabled(true);
		Main.this.btnLoadBackSide.setEnabled(true);
		Main.this.mntmImportCmFile.setEnabled(true);
		Main.this.mntmMapFields.setEnabled(true);
		Main.this.mntmImportFont.setEnabled(true);
		Main.this.tfFileNameTemplate.setEnabled(true);
		Main.this.btnI.setEnabled(true);
		if(Main.this.currentProject.getCsvHeader() != null)
			Main.this.btnCreateCards.setEnabled(true);
		populateDeleteFontMenu();
		lblStatus.setText("Project opened");
		revalidate();
		populateRecentProjects();
	}

	private String importFont(String selectedFont) throws IOException, FontFormatException {
		String fontName = ProjectManager.importFont(Main.this.currentProject, Paths.get(selectedFont));
		populateDeleteFontMenu(fontName);
		return fontName;
	}
	
	private void populateDeleteFontMenu() {
		mnDeleteFont.removeAll();
		if(Main.this.currentProject != null) {
			Map<String, Font> fonts = Main.this.currentProject.getFonts();
			for (String fontName : fonts.keySet()) {
				populateDeleteFontMenu(fontName);
			}
		}
	}
	
	/**
	 * Adds a menu entry in the delete font submenu for the given font name
	 * @param fontName Name of the font that can be deleted with the added entry
	 */
	private void populateDeleteFontMenu(String fontName) {
		JMenuItem dFont = new JMenuItem(fontName);
		dFont.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int answer = JOptionPane.showConfirmDialog(Main.this, "Delete font " + ((JMenuItem)e.getSource()).getText() + "?" , "Delete Font", JOptionPane.YES_NO_OPTION);
				if(answer == JOptionPane.YES_OPTION) {
					try {
						ProjectManager.deleteFont(Main.this.currentProject, ((JMenuItem)e.getSource()).getText());
						lblStatus.setText("Font " + ((JMenuItem)e.getSource()).getText() + " successfully deleted");
						mnDeleteFont.remove(((JMenuItem)e.getSource()));
					} catch (IOException e1) {
						lblStatus.setText("Error deleting font " + ((JMenuItem)e.getSource()).getText() + ": " + e1.getMessage());
						e1.printStackTrace();
					}
				}
			}
		});
		mnDeleteFont.add(dFont);
		revalidate();
	}
}
