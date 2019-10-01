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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.csv.CSVFormat;

import vv3ird.populatecard.control.ProjectManager;
import vv3ird.populatecard.control.TaskScheduler;
import vv3ird.populatecard.control.postprocessing.ReplaceImage;
import vv3ird.populatecard.data.Field;
import vv3ird.populatecard.data.FieldPackage;
import vv3ird.populatecard.data.FieldStyle;
import vv3ird.populatecard.data.ParallelProcessing;
import vv3ird.populatecard.data.Project;
import vv3ird.populatecard.gui.JMain;
import vv3ird.populatecard.gui.StatusListener;

public class CardCreator {
	
	/**
	 * Currently opened project
	 */
	private static Project currentProject = null;
	
	/**
	 * Opens a project from a path object
	 * @param projectFile	Project file
	 * @throws IOException	when the project file does not exist or has an invalid format
	 */
	public static void openProject(Path projectFile) throws IOException {
		openProject(projectFile.toString());
	}

	
	/**
	 * Opens a project from a path object
	 * @param projectFile	Project file
	 * @throws IOException	when the project file does not exist or has an invalid format
	 */
	public static void openProject(String projectFile) throws IOException {
		if (Paths.get(projectFile).toAbsolutePath().toString().startsWith(CardCreator.getBaseFolder().toAbsolutePath().toString()))
			projectFile = CardCreator.getBaseFolder().relativize(Paths.get(projectFile).toAbsolutePath()).toString();
		currentProject = ProjectManager.openProject(projectFile.toString());
	}
	
	// public static Project getCurrentProject() {
	// 	return currentProject;
	// }
	
	/**
	 * Add a new font to the current project. The font file will be copied into the project folders.
	 * @param fontPath		Font to be added.
	 * @return				Name of the font 
	 * @throws IOException	IO error while reading the fontfile
	 * @throws FontFormatException	Font is in a not readable format.
	 */
	public static String addFont(Path fontPath) throws IOException, FontFormatException {
		return ProjectManager.importFont(currentProject, fontPath);
	}
	
	/**
	 * Removes a given font from the current project.
	 * @param fontName	Name of the font to be deleted.
	 * @throws IOException	Error while deleting the font file
	 */
	public static void removeFont(String fontName) throws IOException {
		ProjectManager.deleteFont(currentProject, fontName);
	}
	
	/**
	 * Saves the current project.
	 * @throws IOException Error while writing the project file.
	 */
	public static void save() throws IOException {
		ProjectManager.saveProject(currentProject);
	}
	
	/**
	 * Creates a new project
	 * @param projectName
	 * @throws IOException
	 */
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

	/**
	 * Returns the name of the current Project.
	 * 
	 * @return name of the project or null, if no project is opened
	 */
	public static String getCurrentProjecttName() {
		return currentProject != null ? currentProject.getName() : null;
	}

	/**
	 * returns if a project has been opened
	 * 
	 * @return <code>true</code> if a project was opened, <code>false</code> if no
	 *         project is opened.
	 */
	public static boolean hasCurrentProject() {
		return currentProject != null;
	}

	/**
	 * Sets the file template for the card files
	 * 
	 * @param nameTemplate
	 *            template for the filename for the cards.
	 */
	public static void setFileNameTemplate(String nameTemplate) {
		nameTemplate = Objects.requireNonNull(nameTemplate);
		currentProject.setFileNameTemplate(nameTemplate);
	}
	
	/**
	 * Sets the given {@link Field}s to the current project. Clears the currently
	 * set {@link Field}s. Throws {@link NullPointerException} when no project is
	 * opened
	 * 
	 * @param fields
	 *            List of {@link Field}s
	 */
	public static void setFields(List<Field> fields) {
		currentProject.getFp().clearFields();
		currentProject.getFp().addFields(fields);		
	}

	/**
	 * Replaces the current {@link FieldPackage} with the given
	 * {@link FieldPackage}.
	 * 
	 * @param fp  {@link FieldPackage} that replaces the currently used
	 *            {@link FieldPackage}.
	 */
	public static void setFieldPackage(FieldPackage fp) {
		currentProject.setFp(fp);
	}

	/**
	 * Imports a csv file into the project.
	 * 
	 * @param csvPath 		Path to csv file
	 * @throws IOException	Thrown if the csv file does not exists, or is no valid csv file.
	 */
	public static void importCsv(Path csvPath, boolean processMediaEntriesOnImport) throws IOException {
		ProjectManager.importCsv(currentProject, csvPath, processMediaEntriesOnImport);
	}

	/**
	 * Maps a {@link Field} to a CSV column.
	 * 
	 * @param field			Name of the field to be mapped
	 * @param csvColumnName	Name of the CSV Column the field should be 
	 * 						mapped to.
	 */
	public static void addMapping(String field, String csvColumnName) {
		currentProject.addMapping(field, csvColumnName);
	}

	/**
	 * Sets the front image for the cards.
	 * 
	 * @param img			Image that should replace the front image
	 * @param deleteFields	Flag if the current fields for the front image should be deleted (<code>true</code>) or kept (<code>false</code>)
	 */
	public static void setFrontImage(BufferedImage img, boolean deleteFields) {
		currentProject.getFp().setFrontImage(img, deleteFields);
	}

	/**
	 * Sets the rear image for the cards.
	 * 
	 * @param img			Image that should replace the rear image
	 * @param deleteFields	Flag if the current fields for the rear image should be deleted (<code>true</code>) or kept (<code>false</code>)
	 */
	public static void setRearImage(BufferedImage img, boolean deleteFields) {
		currentProject.getFp().setRearImage(img, deleteFields);
	}
	
	/**
	 * Sets an alternate image for the rear image. If set the rear image will be
	 * replaced with this image, if the rear images was not altered in any way.
	 * 
	 * @param img Alternate rear image or <code>null</code> to unset.
	 */
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
						BufferedImage front = CardCreator.getFrontImageCopy();
						BufferedImage rear = CardCreator.getRearImageCopy();
						Graphics2D gFront = front.createGraphics();
						gFront.setColor(Color.BLACK);
						Graphics2D gRear = rear.createGraphics();
						gRear.setColor(Color.BLACK);
						listener.setText("Drawing card (" + cardNo + "/" + csvData.length +")");
						Map<String, String> entry = new HashMap<>();
						for (String fieldName : currentProject.getCsvHeader().keySet()) {
							int columnIndex = currentProject.getCsvColumnIndex(fieldName);
							entry.put(fieldName, csvEntry[columnIndex]);
						}
						List<Field> calcFields = currentProject.getCalculatedFields();
						for (String fieldName : mappedFields) {
							Field field = currentProject.getFieldByName(fieldName);
							int columnIndex = currentProject.getMappedCsvColumnIndex(fieldName);
							if(field != null && columnIndex >= 0 && columnIndex < csvEntry.length) {
								try {
								Font font = ProjectManager.getFont(currentProject, field.getFont());
								String content = csvEntry[columnIndex];
								filenameFront = filenameFront.replace("{" + currentProject.getCsvColumn(fieldName) + "}", content.replace("/", "_").replace("*", "_"));
								filenameRear = filenameRear.replace("{" + currentProject.getCsvColumn(fieldName) + "}", content.replace("/", "_").replace("*", "_"));
								field.drawContent(gFront, gRear, content, entry);
								// Remove calculated field, if it already has been processed
								if(field.isCalculated())
									calcFields.remove(calcFields.indexOf(field));
								} catch (Exception e) {
									listener.setText("Error drawing on field " +field.getName() + ": " +e.getMessage());
									e.printStackTrace();
								}
							}
						}
						// Process all calculated fields, that have no CSV column mapped
						for (Field field : calcFields) {
							field.drawContent(gFront, gRear, "", entry);
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
							CardCreator.getRearImageCopy(), CardCreator.getAlternateRearImage(), listener),
							listener, true);
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
	 * Returns a copy of the rear image
	 * 
	 * @return Rear image or <code>null</code>, if rear image is not set
	 */
	public static BufferedImage getRearImageCopy() {
		return currentProject.getFp().getRearImageCopy();
	}

	/**
	 * Returns a copy of the front image
	 * 
	 * @return Front image or <code>null</code>, if front image is not set
	 */
	public static BufferedImage getFrontImageCopy() {
		return currentProject.getFp().getFrontImageCopy();
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
		if(currentProject == null || !currentProject.hasCsvData())
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
		Map<String, String> entry = new HashMap<>();
		for (String fieldName : currentProject.getCsvHeader().keySet()) {
			int columnIndex = currentProject.getCsvColumnIndex(fieldName);
			entry.put(fieldName, csvEntry[columnIndex]);
		}
		List<Field> drawnFields = new ArrayList<Field>(fields.size());
		for (String fieldName : mappedFields) {
			Field field = fields.get(fieldName);
			int columnIndex = currentProject.getMappedCsvColumnIndex(fieldName);
			if(field != null && columnIndex >= 0 && columnIndex < csvEntry.length) {
				try {
					Font font = ProjectManager.getFont(currentProject, field.getFont());
					String content = csvEntry[columnIndex];
					System.out.println();
					System.out.println("Drawing Content: " + content);
					field.drawContent(gFront, gRear, content, entry);
					// Remove calculated field, if it already has been processed
					drawnFields.add(field);
				} catch (Exception e) {
					if(listener != null) listener.setText("Error drawing on field " +field.getName() + ": " +e.getMessage());
					e.printStackTrace();
				}
			}
		}
		// Draw all calculated fields, that have no CSV comlumn mapped
		List<Field> calcFields = currentProject.getCalculatedFields();
		if(overrides != null)
			calcFields = overrides.stream().filter(f -> f.isCalculated()).collect(Collectors.toList());
		calcFields.removeAll(drawnFields);
		for (Field field : calcFields) {
			field.drawContent(gFront, gRear, "", entry);
		}
		gFront.dispose();
		gRear.dispose();
		
		BufferedImage[] imgs = new BufferedImage[2];
		
		imgs[0] = front;
		imgs[1] = rear;
		
		return imgs;
	}

	/**
	 * Returns the CSV headers of the project's csv file
	 * 
	 * @return Map with the csv header names and the corresponding column id or
	 *         null, if no csv exists.
	 */
	public static Map<String, Integer> getCsvHeader() {
		return currentProject != null ? currentProject.getCsvHeader() : null;
	}

	/**
	 * Returns the project name
	 * @return	Return the current projects name or null, if no project is opened. 
	 */
	public static String getProjectName() {
		return currentProject != null ? currentProject.getName() : null;
	}

	/**
	 * Returns the front image for the card template of the current project.
	 * 
	 * @return Image that contains the front side of the card template or
	 *         <code>null</code> if no front image exists.
	 */
	public static BufferedImage getFrontImage() {
		return currentProject != null && currentProject.getFp() != null ? currentProject.getFp().getFrontImage() : null;
	}

	/**
	 * Returns the rear image for the card template of the current project.
	 * 
	 * @return Image that contains the rear side of the card template or
	 *         <code>null</code> if no rear image exists.
	 */
	public static BufferedImage getRearImage() {
		return currentProject != null && currentProject.getFp() != null ? currentProject.getFp().getRearImage() : null;
	}

	/**
	 * Returns the alternate rear image for the card template of the current project.
	 * 
	 * @return Image that contains the alternate rear side of the card template or
	 *         <code>null</code> if no alternate rear image exists.
	 */
	public static BufferedImage getAlternateRearImage() {
		return currentProject != null && currentProject.getFp() != null ? currentProject.getFp().getAlternateRearImage() : null;
	}

	/**
	 * Returns the filename template for the cards to be created.
	 * 
	 * @return the template string for the card files or null if no project is
	 *         opened.
	 */
	public static String getFileNameTemplate() {
		return currentProject != null ? currentProject.getFileNameTemplate() : null;
	}

	/**
	 * Returns all fonts installed in the system aswell imported into the project.
	 * 
	 * @return Map of Fonts available for the project.
	 */
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

	/**
	 * Returns the font names of all availabe fonts (project fonts and system fonts)
	 * 
	 * @return Array with all available fonts
	 */
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

	/**
	 * Retunrs a Map-Object with only the system fonts.
	 *  
	 * @return Map with all fonts installed on the system.
	 */
	public static Map<String, Font> getSystemFonts() {
		String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		Map<String, Font> fonts = new HashMap<>();
		for (int i = 0; i < fontNames.length; i++) {
			fonts.put(fontNames[i], Font.getFont(fontNames[i]));
		}
		return fonts;
	}
	
	/**
	 * Returns the output folder of the current project.
	 * @return Path to the output folder of the current project.
	 */
	public static Path getOutputFolder() {
		return currentProject != null ? currentProject.getProjectRoot().resolve("output") : null;
	}

	/**
	 * Reutrns all fields created in the current project
	 * @return	List with all Fields of the current project.
	 */
	public static List<Field> getFields() {
		return currentProject != null ? currentProject.getFp().getFields() : null;
	}

	/**
	 * returns the Filed to CSV Mappings of the current project
	 * @return	Map with Field Names and CSV Column Header names or null if no project has been opened yet.
	 */
	public static Map<String, String> getFieldMappings() {
		// TODO Auto-generated method stub
		return currentProject != null ? currentProject.getFieldMappings() : null;
	}

	/**
	 * Returns the font associated with the given name
	 * 
	 * @param font	Font name for the font to be retrieved.
	 * @return Font associated with the font name or the default font, if no project
	 *         is opened.
	 */
	public static Font getFont(String font) {
		return currentProject != null ? currentProject.getFont(font) : ProjectManager.getDefaultFont();
	}

	/**
	 * Exports the currently opened project into an gzip-archive
	 * @param exportFile	File to export the current project to.
	 * @return	Path to the archive.
	 */
	public static Path exportProject(Path exportFile) {
		return ProjectManager.exportProject(currentProject, exportFile);
	}

	/**
	 * Returns all fonts imported into the project
	 * 
	 * @return	Map with fonts imported into the project or null, if no project is open.
	 */
	public static Map<String, Font> getProjectFonts() {
		return currentProject != null ? currentProject.getFonts() : null;
	}

	/**
	 * Reutrns the woring dir of the program
	 * @return	Path to the working dir of the program.
	 */
	public static Path getBaseFolder() {
		return Paths.get(System.getProperty("user.dir"));
	}
	
	/**
	 * Sets the parallel processing for the project.
	 * @param pp	Type of parallel processing
	 * @param value	amount of threads to be used if pp is set to {@link ParallelProcessing#CUSTOM}
	 */
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


	public static CSVFormat getCSVFormat() {
		return hasCurrentProject() ? currentProject.getCSVFormat() : null;
	}


	public static String getCsvRecordSeparator() {
		return hasCurrentProject() ? currentProject.getCsvRecordSeparator() : "\r\n";
	}


	public static char getCsvDelimiter() {
		// TODO Auto-generated method stub
		return hasCurrentProject() ? currentProject.getCsvDelimiter() : ';';
	}


	public static void setCsvDelimiter(char delim) {
		if (hasCurrentProject())
			currentProject.setCsvDelimiter(delim);
	}


	public static void setCsvQuote(char quote) {
		if (hasCurrentProject())
			currentProject.setCsvQuote(quote);
	}


	public static void setCsvRecordSeparator(String recordSep) {
		if (hasCurrentProject())
			currentProject.setCsvRecordSeparator(recordSep);
	}


	public static char getCsvQuote() {
		return hasCurrentProject() ? currentProject.getCsvQuote() : '"';
	}


	public static ParallelProcessing getProcessingMode() {
		return hasCurrentProject() ? currentProject.getProcessingMode() : ParallelProcessing.SINGLE_THREAD;
	}


	public static int getCustomParallelProcessingThreads() {
		return hasCurrentProject() ? currentProject.getCustomParallelProcessingThreads() : 1;
	}
}
