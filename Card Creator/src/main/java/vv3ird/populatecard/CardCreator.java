package vv3ird.populatecard;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import vv3ird.populatecard.control.ProjectManager;
import vv3ird.populatecard.control.TaskScheduler;
import vv3ird.populatecard.control.postprocessing.ReplaceImage;
import vv3ird.populatecard.data.Field;
import vv3ird.populatecard.data.FieldPackage;
import vv3ird.populatecard.data.ParallelProcessing;
import vv3ird.populatecard.data.Project;
import vv3ird.populatecard.gui.JMain;
import vv3ird.populatecard.gui.StatusListener;

public class CardCreator {
	
	private static Project currentProject = null;
	
	public static void openProject(Path projectFile) throws IOException {
		openProject(projectFile.toString());
	}

	
	public static void openProject(String projectFile) throws IOException {
		if (Paths.get(projectFile).toAbsolutePath().toString().startsWith(CardCreator.getBaseFolder().toAbsolutePath().toString()))
			projectFile = CardCreator.getBaseFolder().relativize(Paths.get(projectFile).toAbsolutePath()).toString();
		currentProject = ProjectManager.openProject(projectFile.toString());
	}
	
	public static Project getCurrentProject() {
		return currentProject;
	}
	
	public static String addFont(Path fontPath) throws IOException, FontFormatException {
		return ProjectManager.importFont(currentProject, fontPath);
	}
	
	public static void removeFont(String fontName) throws IOException {
		ProjectManager.deleteFont(currentProject, fontName);
	}
	
	public static void save() throws IOException {
		ProjectManager.saveProject(currentProject);
	}
	
	public static void createNewProject(String projectName) throws IOException {
		currentProject = ProjectManager.createEmptyProject(projectName);
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			// Set system-platform Java L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JMain frame = new JMain();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static String getCurrentProjecttName() {
		return currentProject != null ? currentProject.getName() : null;
	}

	public static boolean hasCurrentProject() {
		return currentProject != null;
	}

	public static void setFileNameTemplate(String nameTemplate) {
		currentProject.setFileNameTemplate(nameTemplate);
	}
	/**
	 * Sets the given {@link Field}s to the current project. Clears the currently set {@link Field}s. Throws {@link NullPointerException} when no project is opened
	 * @param fields List of {@link Field}s
	 */
	public static void setFields(List<Field> fields) {
		currentProject.getFp().clearFields();
		currentProject.getFp().addFields(fields);		
	}

	/**
	 * Replaces the current {@link FieldPackage} with the given {@link FieldPackage}.
	 * @param fp {@link FieldPackage} that replaces the currently used {@link FieldPackage}.
	 */
	public static void setFieldPackage(FieldPackage fp) {
		currentProject.setFp(fp);
	}

	/**
	 * Imports a csv file into the project.
	 * @param csvPath	Path to csv file
	 * @throws IOException	THrown if the csv file does not exists, or is no valid csv file.
	 */
	public static void importCsv(Path csvPath, boolean processMediaEntriesOnImport) throws IOException {
		ProjectManager.importCsv(currentProject, csvPath, processMediaEntriesOnImport);
	}

	public static void addMapping(String field, String csvColumnName) {
		currentProject.addMapping(field, csvColumnName);
	}

	public static void setFrontImage(BufferedImage img, boolean deleteFields) {
		currentProject.getFp().setFrontImage(img, deleteFields);
	}

	public static void setRearImage(BufferedImage img, boolean deleteFields) {
		currentProject.getFp().setRearImage(img, deleteFields);
	}
	public static void setAlternateRearImage(BufferedImage img) {
		currentProject.getFp().setAlternateRearImage(img);
	}
	

	/**
	 * Draws the cards of a given project. Prerequisites are that a csv exists, thats the fields are mapped to csv columns.
	 * @param p				Project the cards should be created for 
	 * @param listener		Listener for updates on creation process
	 * @throws IOException	whenever an IO error occures, the creation will be aborted
	 */
	public static void drawCards(StatusListener listener) throws IOException {
		Path output = currentProject.getProjectRoot().resolve("output");
		if (!Files.exists(output)) {
			Files.createDirectories(output);
		}
		FieldPackage fPackage = currentProject.getFp();
		List<String> mappedFields = currentProject.getMappedFields();
		String[][] csvData = currentProject.getCsvData();
		String zeroes = "%0" + String.valueOf(csvData.length).length() + "d";
		int card = 1;
		for (String[] csvEntry : csvData) {
			final int cardNo = card++;
			TaskScheduler		.addTask("Drawing card (" + cardNo + "/" + csvData.length +")", 
				new Runnable() {
					@Override
					public void run() {
						String filenameFront = new String(currentProject.getFileNameTemplate());
						String filenameRear = new String(currentProject.getFileNameTemplate());
						BufferedImage front = fPackage.getFrontImageCopy();
						BufferedImage rear = fPackage.getRearImageCopy();
						Graphics2D gFront = front.createGraphics();
						gFront.setColor(Color.BLACK);
						Graphics2D gRear = rear.createGraphics();
						gRear.setColor(Color.BLACK);
						listener.setText("Drawing card (" + cardNo + "/" + csvData.length +")");
						for (String fieldName : mappedFields) {
							Field field = fPackage.getFieldByName(fieldName);
							int columnIndex = currentProject.getCsvColumnIndex(fieldName);
							if(field != null && columnIndex >= 0 && columnIndex < csvEntry.length) {
								try {
								Font font = ProjectManager.getFont(currentProject, field.getFont());
								String content = csvEntry[columnIndex];
								filenameFront = filenameFront.replace("{" + currentProject.getCsvColumn(fieldName) + "}", content.replace("/", "_").replace("*", "_"));
								filenameRear = filenameRear.replace("{" + currentProject.getCsvColumn(fieldName) + "}", content.replace("/", "_").replace("*", "_"));
								field.drawContent(gFront, gRear, content, font);
								} catch (Exception e) {
									listener.setText("Error drawing on field " +field.getName() + ": " +e.getMessage());
									e.printStackTrace();
								}
							}
						}
						gFront.dispose();
						gRear.dispose();
						filenameFront = filenameFront.replace("{no}", String.format(zeroes, cardNo));
						if (filenameFront.contains("{side}")) {
							filenameFront = filenameFront.replace("{side}", "front");
							filenameRear = filenameRear.replace("{no}", String.format(zeroes, cardNo));
							filenameRear = filenameRear.replace("{side}", "rear");
						}
						else {
							filenameRear = filenameRear.replace("{no}", String.valueOf(cardNo));
						}
						if (!filenameRear.toLowerCase().endsWith(".png"))
							filenameRear = filenameRear + ".png";
						if (!filenameFront.toLowerCase().endsWith(".png"))
							filenameFront = filenameFront + ".png";
						try {
							ImageIO.write(front, "PNG", output.resolve(filenameFront).toFile());
							ImageIO.write(rear, "PNG", output.resolve(filenameRear).toFile());
						} catch (IOException e) {
							listener.setText("Error drawing card (" + cardNo + "/" + csvData.length + ")");
							e.printStackTrace();
						}
						
					}
				}, 
				listener
			);
		}
		if (CardCreator.getAlternateRearImage() != null)
			TaskScheduler.addTask("Swap empty rear image with alternate", new ReplaceImage(CardCreator.getOutputFolder(),
							CardCreator.getRearImage(), CardCreator.getAlternateRearImage(), listener),
							listener);
		TaskScheduler.addTask("Open output folder", new Runnable() {
			public void run() {
				try {
					listener.setText("Open output folder " + CardCreator.getOutputFolder().toAbsolutePath().toString());
					Desktop.getDesktop().open(CardCreator.getOutputFolder().toFile());
				} catch (IOException e) {
					listener.setText("Error opening output folder: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}, listener);
		
	}
	
	/**
	 * Draws a card of a given project. Prerequisites are that a csv exists,
	 * thats the fields are mapped to csv columns.
	 * 
	 * @param cardNo 	Number of the card that should be created
	 * @param listener	Listener for updates on creation process
	 * @return Returns an array with the front image at index 0 and the rear image
	 *         at index 1.
	 * @throws IndexOutOfBoundsException
	 *             Throws {@link IndexOutOfBoundsException} if the parameter cardNo
	 *             is smaller than 0 or greater than the number of csv rows.
	 */
	public static BufferedImage[] createCard(int cardNo, StatusListener listener, List<Field> overrides) {
		if(!currentProject.hasCsvData())
			return null;
		Map<String, Field> fields = (overrides != null ? overrides : currentProject.getFp().getFields()).stream().collect(Collectors.toMap(Field::getName, Function.identity()));
		List<String> mappedFields = currentProject.getMappedFields();
		String[][] csvData = currentProject.getCsvData();
		if(listener != null) listener.setText("Drawing card (0/" + csvData.length +")");
		String[] csvEntry = csvData[cardNo];
		BufferedImage front = currentProject.getFp().getFrontImageCopy();
		BufferedImage rear = currentProject.getFp().getRearImageCopy();
		Graphics2D gFront = front.createGraphics();
		gFront.setColor(Color.BLACK);
		Graphics2D gRear = rear.createGraphics();
		gRear.setColor(Color.BLACK);
		if(listener != null) listener.setText("Drawing cards (" + cardNo + "/" + csvData.length +")");
		for (String fieldName : mappedFields) {
			Field field = fields.get(fieldName);
			int columnIndex = currentProject.getCsvColumnIndex(fieldName);
			if(field != null && columnIndex >= 0 && columnIndex < csvEntry.length) {
				try {
					Font font = ProjectManager.getFont(currentProject, field.getFont());
					String content = csvEntry[columnIndex];
					field.drawContent(gFront, gRear, content, font);
				} catch (Exception e) {
					if(listener != null) listener.setText("Error drawing on field " +field.getName() + ": " +e.getMessage());
					e.printStackTrace();
				}
			}
		}
		gFront.dispose();
		gRear.dispose();
		
		BufferedImage[] imgs = new BufferedImage[2];
		
		imgs[0] = front;
		imgs[1] = rear;
		
		return imgs;
	}

	public static Map<String, Integer> getCsvHeader() {
		return currentProject != null ? currentProject.getCsvHeader() : null;
	}


	public static String getProjectName() {
		return currentProject != null ? currentProject.getName() : null;
	}

	public static BufferedImage getFrontImage() {
		return currentProject != null && currentProject.getFp() != null ? currentProject.getFp().getFrontImage() : null;
	}

	public static BufferedImage getRearImage() {
		return currentProject != null && currentProject.getFp() != null ? currentProject.getFp().getRearImage() : null;
	}

	public static BufferedImage getAlternateRearImage() {
		return currentProject != null && currentProject.getFp() != null ? currentProject.getFp().getAlternateRearImage() : null;
	}

	public static String getFileNameTemplate() {
		return currentProject != null ? currentProject.getFileNameTemplate() : null;
	}

	public static Map<String, Font> getFonts() {
		Map<String, Font> fonts = getSystemFonts();
		if (currentProject != null) {
			Map<String, Font> pfonts  = new HashMap<>(currentProject.getFonts());
			for (String sysFontName : fonts.keySet()) {
				pfonts.put(sysFontName, fonts.get(sysFontName));
			}
			fonts = pfonts;
		}
		return fonts;
	}

	public static String[] getFontNames() {
		String[] sfonts = ProjectManager.getSystemFonts();
		String[] pfonts = new String[0];
		if (currentProject != null) {
			pfonts  = currentProject.getFonts().keySet().toArray(new String[currentProject.getFonts().keySet().size()]);
			System.out.println("Project Fonts: " + pfonts.length);
		}
		String[] fonts = new String[pfonts.length+sfonts.length];
		for (int i = 0; i < pfonts.length; i++) {
			fonts[i] = pfonts[i];
		}
		for (int i = pfonts.length; i < sfonts.length+pfonts.length; i++) {
			fonts[i] = sfonts[i-pfonts.length];
		}
		return fonts;
	}

	public static Map<String, Font> getSystemFonts() {
		String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		Map<String, Font> fonts = new HashMap<>();
		for (int i = 0; i < fontNames.length; i++) {
			fonts.put(fontNames[i], Font.getFont(fontNames[i]));
		}
		return fonts;
	}
	
	public static Path getOutputFolder() {
		return currentProject != null ? currentProject.getProjectRoot().resolve("output") : null;
	}


	public static List<Field> getFields() {
		return currentProject != null ? currentProject.getFp().getFields() : null;
	}


	public static Map<String, String> getFieldMappings() {
		// TODO Auto-generated method stub
		return currentProject != null ? currentProject.getFieldMappings() : null;
	}


	public static Font getFont(String font) {
		return currentProject != null ? currentProject.getFont(font) : ProjectManager.getDefaultFont();
	}


	public static Path exportProject(Path exportFile) {
		return ProjectManager.exportProject(currentProject, exportFile);
	}


	public static Map<String, Font> getProjectFonts() {
		return currentProject != null ? currentProject.getFonts() : null;
	}


	public static Path getBaseFolder() {
		return Paths.get(System.getProperty("user.dir"));
	}
	
	public static void setParallelProcessing(ParallelProcessing pp, int value) {
		System.out.println("PP:  " + pp);
		System.out.println("Val: " + value);
		value = value > 0 ? value : 1;
		if (hasCurrentProject()) {
			currentProject.setCustomParallelProcessingThreads(value);
			currentProject.setProcessingMode(pp);
		}
		TaskScheduler.changeThreadCount(pp == ParallelProcessing.CPU_MINUS_ONE ? Runtime.getRuntime().availableProcessors()-1 : pp == ParallelProcessing.SINGLE_THREAD ? 1 : value);
	}
}
