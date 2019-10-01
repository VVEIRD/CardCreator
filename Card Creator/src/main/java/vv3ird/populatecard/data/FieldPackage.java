package vv3ird.populatecard.data;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import vv3ird.populatecard.data.Field.CardSide;

/**
 * FieldPackage is a relict of the early version of this program. It contains
 * the images and fields for an project. Those can be exported seperatly through
 * the Field Mapping panel. Saving and loading creates or loads a file with the
 * json construct of this class, images are encoded to base64 before saving and
 * decoded after loading the json-file.
 * 
 * @author VV3IRD
 *
 */
public class FieldPackage {

	/**
	 * Loads the Fieldpackage from a json-file.
	 * 
	 * @param path
	 *            Path to the json-file
	 * @return returns a {@link FieldPackage} with decoded images, if applicable.
	 * @throws IOException
	 *             Throws an IO Exception when an error occures while opening the
	 *             given path
	 */
	public static FieldPackage load(Path path) throws IOException {
		FieldPackage fieldPack = null;
		byte[] fieldPackBytes = Files.readAllBytes(path);
		String fieldPackString = new String(fieldPackBytes, StandardCharsets.UTF_8);
		Gson gson = new Gson();
		fieldPack = gson.fromJson(fieldPackString, FieldPackage.class);
		if (fieldPack.getImageBase64() != null) {
			fieldPack.decodeImages();
		}
		return fieldPack;
	}

	/**
	 * Saves a FieldPackage to a json-file.
	 * 
	 * @param fieldPack Given FieldPackage to save
	 * @param path File to save the FieldPackage to.
	 * @throws IOException
	 *             Error thrown when an io error occures while saving the
	 *             FieldPackage to file.
	 */
	public static void save(FieldPackage fieldPack, Path path) throws IOException {
		fieldPack.encodeImages();
		String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(fieldPack);
		byte[] utf8JsonString = jsonString.getBytes(StandardCharsets.UTF_8);
		Files.write(path, utf8JsonString, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}

	/**
	 * Fields for the front and rear image of the card
	 */
	private List<Field> fields = new LinkedList<>();

	/**
	 * Front image template for the cards
	 */
	private transient BufferedImage front = null;

	/**
	 * Base64 encoded front image
	 */
	private String frontBase64 = null;

	/**
	 * rear image template for the cards
	 */
	private transient BufferedImage rear = null;

	/**
	 * Base64 encoded rear image
	 */
	private String rearBase64 = null;

	/**
	 * Alternate rear image to be used when the normal rear image was not modified
	 */
	private transient BufferedImage alternateRear = null;

	/**
	 * Base64 encoded alternate rear image
	 */
	private String alternateRearBase64 = null;

	/**
	 * Creates an empty FieldPackage
	 */
	public FieldPackage() {
		this(null, null);
	}

	/**
	 * Create a FieldPackage with an already existing front and rear image
	 * 
	 * @param front Front image, can be null
	 * @param rear Rear image, can be null
	 */
	public FieldPackage(BufferedImage front, BufferedImage rear) {
		this(front, rear, null);
	}


	/**
	 * Create a FieldPackage with an already existing front, rear and alternate rear
	 * image.
	 * 
	 * @param front Front image, can be null
	 * @param rear Rear image, can be null
	 * @param alternativeRear Alternate rear image, can be null
	 */
	public FieldPackage(BufferedImage front, BufferedImage rear, BufferedImage alternativeRear) {
		this.front = front;
		this.rear = rear;
		this.alternateRear = alternativeRear;
		this.frontBase64 = null;
		this.rearBase64 = null;
		this.alternateRearBase64 = null;
	}
	
	/**
	 * Adds a field to the package
	 * @param field Field to be added
	 */
	public void addField(Field field) {
		fields = Objects.requireNonNull(fields, "Parameter field cannot be null");
		this.fields.add(field);
	}

	/**
	 * Adds a list of Fields to the package
	 * @param fields Fields to be added.
	 */
	public void addFields(List<Field> fields) {
		fields = Objects.requireNonNull(fields, "Parameter fields cannot be null");
		for (Field field : fields) {
			System.out.println(field);
		}
		this.fields.addAll(fields);
	}

	/**
	 * Removes all fields from the package
	 */
	public void clearFields() {
		this.fields.clear();
	}

	/**
	 * Decodes the images in base64 format to Images
	 */
	public void decodeImages() {
		try {
			// Front
			if (this.frontBase64 != null) {
				byte[] bFrontImg = Base64.getDecoder().decode(this.frontBase64);
				ByteArrayInputStream isFront = new ByteArrayInputStream(bFrontImg);
				this.front = ImageIO.read(isFront);
			}
			// Rear
			if (this.rearBase64 != null) {
				byte[] bRearImg = Base64.getDecoder().decode(this.rearBase64);
				ByteArrayInputStream isRear = new ByteArrayInputStream(bRearImg);
				this.rear = ImageIO.read(isRear);
			}
			// Alternate Rear
			if (this.alternateRearBase64 != null) {
				byte[] bRearImg = Base64.getDecoder().decode(this.alternateRearBase64);
				ByteArrayInputStream isRear = new ByteArrayInputStream(bRearImg);
				this.alternateRear = ImageIO.read(isRear);
			}
		} catch (final IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
	}

	/**
	 * Deletes a field for a specific card side
	 * 
	 * @param side Side of the card for which the fields should be removed.
	 */
	private void deleteFields(CardSide side) {
		List<Field> tbr = new ArrayList<Field>(
				this.fields.stream().filter(f -> f.getSide() == side).collect(Collectors.toList()));
		for (Field field : this.fields) {
			Field parent = field;
			Field linked = null;
			while ((linked = parent.getLinkedField()) != null) {
				if (linked.getSide() == side) {
					parent.removeLinkedField();
					if (linked.hasLinkedField())
						parent.setLinkedField(linked.getLinkedField());
				}
				if (!parent.hasLinkedField())
					parent = linked;
			}
		}
		this.fields.removeAll(tbr);
	}

	/**
	 * Encodes all images to bas64 for saving as json-string.
	 */
	public void encodeImages() {
		try {
			if (this.front != null) {
				final ByteArrayOutputStream osFront = new ByteArrayOutputStream();
				ImageIO.write(this.front, "PNG", osFront);
				this.frontBase64 = Base64.getEncoder().encodeToString(osFront.toByteArray());
			}
			if (this.rear != null) {
				final ByteArrayOutputStream osRear = new ByteArrayOutputStream();
				ImageIO.write(this.rear, "PNG", osRear);
				this.rearBase64 = Base64.getEncoder().encodeToString(osRear.toByteArray());
			}
			if (this.alternateRear != null) {
				final ByteArrayOutputStream osAlternateRear = new ByteArrayOutputStream();
				ImageIO.write(this.alternateRear, "PNG", osAlternateRear);
				this.alternateRearBase64 = Base64.getEncoder().encodeToString(osAlternateRear.toByteArray());
			}
		} catch (final IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
	}

	/**
	 * Returns the alternate rear image
	 * 
	 * @return Alternate rear image, or null, if no image exists
	 */
	public BufferedImage getAlternateRearImage() {
		return alternateRear;
	}

	/**
	 * Returns a field by its name.
	 * 
	 * @param name Name of the field
	 * @return The field with the given name, or null, in no field with the given
	 *         name exists.
	 */
	public Field getFieldByName(final String name) {
		return fields.stream().filter(f -> f.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	/**
	 * Returns all fields that are calculated.
	 * 
	 * @return The fields that should be calculated
	 */
	public List<Field> getCalculatedFields() {
		return fields.stream().filter(f -> f.isCalculated()).collect(Collectors.toList());
	}

	/**
	 * @return A copy of the Field List
	 */
	public List<Field> getFields() {
		return new ArrayList<>(fields);
	}

	/**
	 * Returns all fields for the front card side
	 * @return List with all Fields for the front side card template
	 */
	public List<Field> getFrontFields() {
		return this.fields.stream().filter(f -> f.getSide() == CardSide.FRONT).collect(Collectors.toList());
	}

	/**
	 * Returns the front image
	 * 
	 * @return Front image, or null, if no image exists
	 */
	public BufferedImage getFrontImage() {
		return front;
	}
	/**
	 * Returns a copy of the front image.
	 * 
	 * @return Copy of front image, or null, if no image exists
	 */
	public BufferedImage getFrontImageCopy() {
		return this.getImageCopy(this.front);
	}

	/**
	 * Returns the front image encoded in base64.
	 * 
	 * @return Front images encoded in base64, or null, if non exists
	 */
	public String getImageBase64() {
		return frontBase64;
	}

	/**
	 * Returns a copy of the given image.
	 * 
	 * @param img Image to be copied
	 * @return Copy of the given image, or null if given image was also null
	 */
	public BufferedImage getImageCopy(BufferedImage img) {
		if (img != null) {
			BufferedImage bImg = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
			Graphics2D g = bImg.createGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, img.getWidth(), img.getHeight());
			g.drawImage(img, 0, 0, null);
			g.dispose();
			return bImg;
		}
		return null;
	}

	/**
	 * Returns a List with the Fields for the rear side of the card
	 * @return List with rear Fields
	 */
	public List<Field> getRearFields() {
		return this.fields.stream().filter(f -> f.getSide() == CardSide.REAR).collect(Collectors.toList());
	}

	/**
	 * Returns the rear image
	 * 
	 * @return Rear image, or null, if no image exists
	 */
	public BufferedImage getRearImage() {
		return rear;
	}

	/**
	 * Returns a copy of the rear image
	 * 
	 * @return Copy of rear image, or null, if no image exists
	 */
	public BufferedImage getRearImageCopy() {
		return this.getImageCopy(this.rear);
	}
	
	/**
	 * Removes a given Field
	 * 
	 * @param field Field to be removed.
	 * @return true if the field was removed, false if the given field was not in
	 *         the list.
	 */
	public boolean removeField(Field field) {
		return this.fields.remove(field);
	}
	
	/**
	 * Sets the alternate rear image
	 * 
	 * @param image Alternate rear image.
	 */
	public void setAlternateRearImage(BufferedImage image) {
		this.alternateRear = image;
		this.alternateRearBase64 = null;
	}
	
	/**
	 * Sets the front image
	 * 
	 * @param image Front image
	 * @param deleteFrontFields Flag if all Fields for the front image should be removed (true) or
	 *            not (false).
	 */
	public void setFrontImage(BufferedImage image, boolean deleteFrontFields) {
		this.front = image;
		this.frontBase64 = null;
		if(deleteFrontFields) 
			deleteFields(CardSide.FRONT);
	}

	/**
	 * Sets the base64 encoded front image.
	 * @param imageBase64 Front image encoded in base64
	 */
	public void setImageBase64(String imageBase64) {
		this.frontBase64 = imageBase64;
	}
	
	/**
	 * Sets the rear image
	 * 
	 * @param image Rear image
	 * @param deleterearFields Flag if all Fields for the reat image should be removed (true) or
	 *            not (false).
	 */
	public void setRearImage(BufferedImage image, boolean deleteRearFields) {
		this.rear = image;
		this.rearBase64 = null;
		if(deleteRearFields) 
			deleteFields(CardSide.REAR);
	}
}
