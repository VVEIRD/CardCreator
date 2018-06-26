package de.rcblum.populatecard.data;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.swing.JLabel;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.google.gson.reflect.TypeToken;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ProjectManager {

	private static List<String> recentProjects = new LinkedList<>();

	static {
		if (!Files.exists(Paths.get("config")))
			try {
				Files.createDirectories(Paths.get("config"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		Path recentProjectsFile = Paths.get("config", "recent.json");
		if (Files.exists(recentProjectsFile)) {
			try {
				loadRecent() ;
			} catch (IOException e) {
				System.out.println("Unable to load the recent project list (" + recentProjectsFile.toString() + "):");
				e.printStackTrace();
			}
		}
	}
	
	public static List<String> getRecentProjects() {
		return recentProjects;
	}
	
	
	public static void saveRecent() throws IOException {
		String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(recentProjects);
		try {
			byte[] utf8JsonString = jsonString.getBytes("UTF-8");
			Files.write(Paths.get("config", "recent.json"), utf8JsonString, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (UnsupportedEncodingException e) {
		}
	}
	

	public static void loadRecent() throws IOException {
		List<String> recent = null;
		byte[] fieldPackBytes = Files.readAllBytes(Paths.get("config", "recent.json"));
		String recentString = new String(fieldPackBytes, "UTF-8");
		Gson gson = new Gson();
		recent = gson.fromJson(recentString, new TypeToken<List<String>>(){}.getType());
		recentProjects.addAll(recent);
	}
	
	public static Project openProject(String projectFilePath) throws IOException {
		Path projectFile = Paths.get(projectFilePath);
		Path projectRoot = Paths.get(projectFilePath).getParent();
		Project p = Project.load(projectFile);
		String rootFolderName = projectFile.getParent().getFileName().toString();
		if(!rootFolderName.equals(p.getName()))
			p.setName(rootFolderName);
		// Load fonts
		Path projectFonts = Paths.get(projectRoot.toString(), "fonts");
		Stream<Path> fontFiles = Files.list(projectFonts);
		fontFiles.forEach(pth -> loadFont(p, pth));
		// Load CSV
		Path projectCsv = Paths.get(projectRoot.toString(), "csv", rootFolderName + ".csv");
		if (Files.exists(projectCsv)) {
			CSVFormat format = CSVFormat.newFormat(';').withQuote('\"').withRecordSeparator('\n').withFirstRecordAsHeader();
			try (InputStream is = Files.newInputStream(projectCsv); Reader in = new InputStreamReader(is); CSVParser parser = new CSVParser(in, format);) {
				List<CSVRecord> records = parser.getRecords();
				Map<String, Integer> headerMap = parser.getHeaderMap();
				String[][] csvData = new String[(int) parser.getRecordNumber()][];
				int row = 0;
				for (CSVRecord record : records) {
					csvData[row] = new String[record.size()];
					for (int i = 0; i < csvData[row].length; i++) {
						csvData[row][i] = record.get(i);
					}
					row++;
				}
				p.setCsvHeader(headerMap);
				p.setCsvData(csvData);
			}
		}
		addRecentProject(projectFile.toString());
		saveRecent();
		return p;
	}
	
	private static void addRecentProject(String string) {
		recentProjects.remove(string);
		recentProjects.add(string);
		if(recentProjects.size() > 15) {
			Collections.reverse(recentProjects);
			for(int i=recentProjects.size()-1; i>14;i--)
				recentProjects.remove(i);
			Collections.reverse(recentProjects);
		}
		try {
			saveRecent();
		} catch (IOException e) {
			System.out.println("Recent project list could not be saved");
			e.printStackTrace();
		}
	}


	/**
	 * Loads a font from the project resources
	 * @param p		Project the font is loaded for
	 * @param path	Path of the font
	 * @return		Returns the Font object loaded
	 */
	private static Font loadFont(Project p, Path path) {
		Font font = null;
		String name = path.getFileName().toString();
		name = name.substring(0, name.lastIndexOf("."));
		if(Files.exists(path)) {
			try (InputStream fIn = Files.newInputStream(path)) {
				font = Font.createFont(Font.TRUETYPE_FONT, fIn);
				p.addFont(name, font);
			} catch (IOException|FontFormatException e) {
				e.printStackTrace();
			}
		}
		return font;
	}


	/**
	 * Creates a new project
	 * @param projectName	Name of the Project
	 * @return	The newly created Project
	 * @throws IOException 
	 */
	public static Project createEmptyProject(String projectName) throws IOException {
		Path projectRoot = Paths.get("projects", projectName);
		Path projectFilePath = Paths.get("projects", projectName, projectName + ".cmp");
		Path projectFonts = Paths.get("projects", projectName, "fonts");
		Path projectCsv = Paths.get("projects", projectName, "csv");
		Path projectOutput = Paths.get("projects", projectName, "output");
		Files.createDirectories(projectFonts);
		Files.createDirectories(projectCsv);
		Files.createDirectories(projectOutput);
		Project pNew = new Project(projectName);
		Project.save(pNew, projectFilePath);
		addRecentProject(projectFilePath.toString());
		saveRecent();
		return pNew;
	}


	/**
	 * Save the current Project file
	 * @param project	Project to be serialized
	 * @throws IOException
	 */
	public static void saveProject(Project project) throws IOException {
		Path projectRoot = project.getProjectRoot();
		Path projectFilePath = Paths.get(projectRoot.toString(), project.getName() + ".cmp");
		Path projectFonts = Paths.get(projectRoot.toString().toString(), "fonts");
		Path projectCsv = Paths.get(projectRoot.toString(), "csv");
		Path projectOutput = Paths.get(projectRoot.toString(), "output");
		Files.createDirectories(projectFonts);
		Files.createDirectories(projectCsv);
		Files.createDirectories(projectOutput);
		Project.save(project, projectFilePath);
	}
	
	/**
	 * Imports a font into the project. Only true type fonts supported
	 * @param project
	 * @param fontPath
	 * @return
	 * @throws IOException
	 * @throws FontFormatException
	 */
	public static String importFont(Project project, Path fontPath) throws IOException, FontFormatException {
		Font font = null;
		String name = fontPath.getFileName().toString();
		name = name.substring(0, name.lastIndexOf("."));
		if(Files.exists(fontPath)) {
			try (InputStream fIn = Files.newInputStream(fontPath)) {
				font = Font.createFont(Font.TRUETYPE_FONT, fIn);
				Path projectFont = Paths.get(project.getProjectRoot().toString(), "fonts", fontPath.getFileName().toString());
				Files.copy(fontPath, projectFont, StandardCopyOption.REPLACE_EXISTING);
				project.addFont(name, font);
			}
		}
		return name;
	}

	public static void deleteFont(Project project, String fontName) throws IOException {
		Path projectFont = Paths.get(project.getProjectRoot().toString(), "fonts", fontName + ".ttf");
		Files.delete(projectFont);
		project.removeFont(fontName);
	}
	
	public static void importCsv(Project project, Path csvPath) throws IOException {
		boolean csvValid=false;
		if(Files.exists(csvPath)) {
			CSVFormat format = CSVFormat.newFormat(';').withQuote('\"').withRecordSeparator('\n').withFirstRecordAsHeader();
			try (InputStream is = Files.newInputStream(csvPath);
					Reader in = new InputStreamReader(is);
					CSVParser parser = new CSVParser(in, format);) {
				Map<String, Integer> headerMap = parser.getHeaderMap();
				System.out.println("Columns:");
				for (String column : headerMap.keySet()) {
					System.out.println("\t" + column);
				}
				List<CSVRecord> records = parser.getRecords();
				System.out.println("CSV Rows: " + parser.getRecordNumber());
				String[][] csvData = new String[(int) parser.getRecordNumber()][];
				int row = 0;
				for (CSVRecord record : records) {
					System.out.println("Row: " + (row+1) + " CSV columns: " + record.size());
					csvData[row] = new String[record.size()];
					for (int i = 0; i < csvData[row].length; i++) {
						csvData[row][i] = record.get(i);
						System.out.println("\tColumn " + i + ": " + record.get(i) + ", ");
					}
					System.out.println();
					row++;
				}
				project.setCsvHeader(headerMap);
				project.setCsvData(csvData);
				csvValid=true;
			}
			if(csvValid) {
				try (InputStream fIn = Files.newInputStream(csvPath)) {
					Path projectCsv = Paths.get(project.getProjectRoot().toString(), "csv", project.getName() + ".csv");
					Files.copy(csvPath, projectCsv, StandardCopyOption.REPLACE_EXISTING);
				}
			}
		}
	}

	public static boolean checkForDuplicates(String projectName) {
		Path projectFilePath = Paths.get("projects", projectName, projectName + ".cmp");
		return Files.exists(projectFilePath);
	}

	public static String[] getSystemFonts() {
		String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		return fonts;
	}
	
	public static Font getFont(Project p, String fontName) {
		Font font = p.getFonts().get(fontName);
		if (font == null) {
			font = Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()).stream().filter(f -> f.getFamily().equalsIgnoreCase(fontName)).findFirst().orElse(null);
		}
		return font != null ? font : new JLabel().getFont();
	}

}
