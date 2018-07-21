package vv3ird.populatecard;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import vv3ird.populatecard.control.ProjectManager;
import vv3ird.populatecard.data.Field;
import vv3ird.populatecard.data.FieldPackage;
import vv3ird.populatecard.data.Project;
import vv3ird.populatecard.gui.JMain;
import vv3ird.populatecard.gui.StatusListener;

public class CardCreator {
	
	private static Project currentProject = null;
	
	public static void openProject(Path projectFile) throws IOException {
		openProject(projectFile.toString());
	}

	
	public static void openProject(String projectFile) throws IOException {
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
		int cardNo = 1;
		listener.setText("Drawing cards (0/" + csvData.length +")");
		for (String[] csvEntry : csvData) {
			String filenameFront = new String(currentProject.getFileNameTemplate());
			String filenameRear = new String(currentProject.getFileNameTemplate());
			BufferedImage front = fPackage.getFrontImageCopy();
			BufferedImage rear = fPackage.getRearImageCopy();
			Graphics2D gFront = front.createGraphics();
			gFront.setColor(Color.BLACK);
			Graphics2D gRear = rear.createGraphics();
			gRear.setColor(Color.BLACK);
			listener.setText("Drawing cards (" + cardNo + "/" + csvData.length +")");
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
				filenameRear = filenameRear.replace("{no}", String.valueOf(++cardNo));
			}
			cardNo++;
			if (!filenameRear.toLowerCase().endsWith(".png"))
				filenameRear = filenameRear + ".png";
			if (!filenameFront.toLowerCase().endsWith(".png"))
				filenameFront = filenameFront + ".png";
			ImageIO.write(front, "PNG", output.resolve(filenameFront).toFile());
			ImageIO.write(rear, "PNG", output.resolve(filenameRear).toFile());
		}
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
	public static BufferedImage[] createCard(int cardNo, StatusListener listener) {
		FieldPackage fPackage = currentProject.getFp();
		List<String> mappedFields = currentProject.getMappedFields();
		String[][] csvData = currentProject.getCsvData();
		listener.setText("Drawing cards (0/" + csvData.length +")");
		String[] csvEntry = csvData[cardNo];
		BufferedImage front = fPackage.getFrontImageCopy();
		BufferedImage rear = fPackage.getRearImageCopy();
		Graphics2D gFront = front.createGraphics();
		gFront.setColor(Color.BLACK);
		Graphics2D gRear = rear.createGraphics();
		gRear.setColor(Color.BLACK);
		listener.setText("Drawing cards (" + cardNo + "/" + csvData.length +")");
		for (String fieldName : mappedFields) {
			Field field = fPackage.getFieldByName(fieldName);
			int columnIndex = currentProject.getCsvColumnIndex(fieldName);
			if(field != null && columnIndex >= 0 && columnIndex < csvEntry.length) {
				try {
				Font font = ProjectManager.getFont(currentProject, field.getFont());
				String content = csvEntry[columnIndex];
				field.drawContent(gFront, gRear, content, font);
				} catch (Exception e) {
					listener.setText("Error drawing on field " +field.getName() + ": " +e.getMessage());
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
}
