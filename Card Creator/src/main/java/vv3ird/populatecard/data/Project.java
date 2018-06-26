package vv3ird.populatecard.data;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import vv3ird.populatecard.StatusListener;

/**
 * Project that contains all necessary information to create cards<br>
 * Project structure (As created by {@link ProjectManager}:<br><br>
 * <code>projects/{@link #name}/{@link #name}.cmp<br>
 * projects/{@link #name}/csv/<br>
 * projects/{@link #name}/fonts/<br></code>
 * @author VV3IRD
 *
 */
public class Project {
	
	/**
	 * Draws the cards of a given project. Prerequisites are that a csv exists, thats the fields are mapped to csv columns.
	 * @param p				Project the cards should be created for 
	 * @param listener		Listener for updates on creation process
	 * @throws IOException	whenever an IO error occures, the creation will be aborted
	 */
	public static void drawCards(Project p, StatusListener listener) throws IOException {
		Path output = p.getProjectRoot().resolve("output");
		if (!Files.exists(output)) {
			Files.createDirectories(output);
		}
		FieldPackage fPackage = p.getFp();
		List<String> mappedFields = p.getMappedFields();
		String[][] csvData = p.getCsvData();
		int cardNo = 1;
		listener.setText("Drawing cards (0/" + csvData.length +")");
		for (String[] csvEntry : csvData) {
			String filenameFront = new String(p.fileNameTemplate);
			String filenameRear = new String(p.fileNameTemplate);
			BufferedImage front = fPackage.getFrontImageCopy();
			BufferedImage rear = fPackage.getRearImageCopy();
			Graphics2D gFront = front.createGraphics();
			gFront.setColor(Color.BLACK);
			Graphics2D gRear = rear.createGraphics();
			gRear.setColor(Color.BLACK);
			for (String fieldName : mappedFields) {
				listener.setText("Drawing cards (" + cardNo + "/" + csvData.length +")");
				Field field = fPackage.getFieldByName(fieldName);
				int columnIndex = p.getCsvColumnIndex(fieldName);
				if(field != null && columnIndex >= 0 && columnIndex < csvEntry.length) {
					Font font = ProjectManager.getFont(p, field.getFont());
					String content = csvEntry[columnIndex];
					field.drawContent(gFront, gRear, content, font);
					filenameFront = filenameFront.replace("{" + p.getCsvColumn(fieldName) + "}", content.replace("/", "_").replace("*", "_"));
					filenameRear = filenameRear.replace("{" + p.getCsvColumn(fieldName) + "}", content.replace("/", "_").replace("*", "_"));
				}
			}
			gFront.dispose();
			gRear.dispose();
			filenameFront = filenameFront.replace("{no}", String.valueOf(cardNo));
			if (filenameFront.contains("{side}")) {
				filenameFront = filenameFront.replace("{side}", "front");
				filenameRear = filenameRear.replace("{no}", String.valueOf(cardNo));
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
	 * Loads a project from a given path
	 * @param path	Project file to be loaded
	 * @return The project object
	 * @throws IOException	Whenever the project file cannot be loaded from the given file.
	 */
	public static Project load(Path path) throws IOException {
		Project project = null;
		byte[] projectBytes = Files.readAllBytes(path);
		String projectString = new String(projectBytes, "UTF-8");
		Gson gson = new Gson();
		project = gson.fromJson(projectString, Project.class);
		if (project.fp != null)
			project.fp.decodeImages();
		project.init();
		project.setProjectRoot(path.getParent());
		return project;
	}
	
	/**
	 * Save a project to the given file. Project is transformed to a Json String and then saved to disk.
	 * @param project	Project that should be saved
	 * @param path		Project file
	 * @throws IOException	Whenever an IO Error occurs while writing data to disk.
	 */
	public static void save(Project project, Path path) throws IOException {
		if (project.fp != null)
			project.fp.encodeImages();
		String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(project);
		try {
			byte[] utf8JsonString = jsonString.getBytes("UTF-8");
			Files.write(path, utf8JsonString, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (UnsupportedEncodingException e) {
		}
	}
	
	/**
	 * CSV Data, will be loaded by {@link ProjectManager} when CSV exists in project folder
	 */
	private transient String[][] csvData = null;
	
	/**
	 * Mappings {@link Field} names to CSV columns 
	 */
	private Map<String, String> csvFieldMapping = null;
	
	/**
	 * Map with column header names to column index, will be loaded by {@link ProjectManager} when CSV exists in project folder
	 */
	private transient Map<String, Integer> csvHeader = null;
	
	/**
	 * Template for generated cards.<br>
	 * {no} - Number of card created<br>
	 * {side} - side of the card (front or rear)<br>
	 * + Column headers. 
	 */
	private String fileNameTemplate = "{no}.png";
	
	/**
	 * Available custom fonts for this Project
	 */
	private transient Map<String, Font> fonts = null;
	
	/**
	 * Field package that contains image data and {@link Field}s
	 */
	private FieldPackage fp = null;
	
	/**
	 * Name of the project
	 */
	private String name = null;
	
	/**
	 * Root folder that will be populated when the project is loaded from file by {@link ProjectManager}
	 */
	private transient Path projectRoot = null;
	
	/**
	 * Creates a new project with only a name.
	 * @param name
	 */
	public Project(String name) {
		this.name = name;
		this.fp = new FieldPackage();
		this.csvFieldMapping = new HashMap<>();
		this.fonts = new HashMap<>();
	}
	
	/**
	 * Adds a font to the project object
	 * @param fontName	Name of the font.
	 * @param font		Font object
	 */
	public void addFont(String fontName, Font font) {
		this.fonts.put(fontName, font);
	}
	
	/**
	 * Maps a certain {@link Field} to a CSV column.
	 * @param field		Fieldname to be mapped
	 * @param csvColumn	CVS-Columnname to be mapped
	 */
	public void addMapping(String field, String csvColumn) {
		this.csvFieldMapping.put(field, csvColumn);
	}
	
	/**
	 * Returns the columnname mapped to the {@link Field}name
	 * @param field	{@link Field}name the column should be retrieved for
	 * @return	Columnname or null, if no mapping exists.
	 */
	public String getCsvColumn(String field) {
		return this.csvFieldMapping.get(field);
	}
	
	public int getCsvColumnIndex(String field) {
		return this.csvHeader.get(this.csvFieldMapping.get(field));
	}
	
	public String[][] getCsvData() {
		return csvData;
	}
	
	public Map<String, Integer>  getCsvHeader() {
		return new TreeMap<>(csvHeader);
	}
	
	public String getFileNameTemplate() {
		return fileNameTemplate;
	}
	
	public Map<String, Font> getFonts() {
		return fonts;
	}

	public FieldPackage getFp() {
		return fp;
	}

	public List<String> getMappedFields () {
		return new ArrayList<>(this.csvFieldMapping.keySet());
	}
	
	public String getName() {
		return name;
	}

	public Path getProjectRoot() {
		return projectRoot;
	}

	private void init() {
		this.fonts = new HashMap<>();
		if(this.csvFieldMapping == null) {
			csvFieldMapping = new HashMap<>();
		}
	}

	public void removeFont(String fontName) {
		this.fonts.remove(fontName);
	}
	
	public void setCsvData(String[][] csvData) {
		this.csvData = csvData;
	}

	public void setCsvHeader(Map<String, Integer> csvHeader) {
		this.csvHeader = new TreeMap<>(csvHeader);
	}
	
	public void setFileNameTemplate(String fileNameTemplate) {
		this.fileNameTemplate = fileNameTemplate;
	}
	
	public void setFp(FieldPackage fp) {
		this.fp = fp;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	private void setProjectRoot(Path projectRoot) {
		this.projectRoot = projectRoot;
	}
	
	/**
	 * Verifies and removes invalid Field to CSV column mappings
	 * @param remove <code>true</code> if invalid mappings should be removed, <code>false</code> if only the number of invalid mappings should be calculated.
	 * @return Number of mappings that were invalid and got removed.
	 */
	public int verifyFieldMapping(boolean remove) {
		List<String> mtbr = new LinkedList<>();
		for (String fieldName : csvFieldMapping.keySet()) {
			if (this.fp.getFieldByName(fieldName) == null) {
				mtbr.add(fieldName);
			}
			else if (csvHeader != null && csvHeader.get(csvFieldMapping.get(fieldName)) == null) {
				mtbr.add(fieldName);
			}
		}
		if(remove) {
			for (String fn : mtbr) {
				this.csvFieldMapping.remove(fn);
			}
		}
		return mtbr.size();
	}

}