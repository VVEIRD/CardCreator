package vv3ird.populatecard.data;

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

public class FieldPackage {

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

	public static void save(FieldPackage fieldPack, Path path) throws IOException {
		fieldPack.encodeImages();
		String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(fieldPack);
		byte[] utf8JsonString = jsonString.getBytes(StandardCharsets.UTF_8);
		Files.write(path, utf8JsonString, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}

	private transient BufferedImage alternateRear = null;

	private String alternateRearBase64 = null;

	private List<Field> fields = new LinkedList<>();

	private transient BufferedImage front = null;

	private String frontBase64 = null;

	private transient BufferedImage rear = null;

	private String rearBase64 = null;

	public FieldPackage() {
		this(null, null);
	}

	public FieldPackage(BufferedImage front, BufferedImage rear) {
		this(front, rear, null);
	}

	public FieldPackage(BufferedImage front, BufferedImage rear, BufferedImage alternativeRear) {
		this.front = front;
		this.rear = rear;
		this.alternateRear = alternativeRear;
		this.frontBase64 = null;
		this.rearBase64 = null;
		this.alternateRearBase64 = null;
	}
	
	public void addField(Field field) {
		fields = Objects.requireNonNull(fields, "Parameter field cannot be null");
		this.fields.add(field);
	}

	public void addFields(List<Field> fields) {
		fields = Objects.requireNonNull(fields, "Parameter fields cannot be null");
		this.fields.addAll(fields);
	}

	public boolean checkNameForDuplicate(final String name) {
		return fields.stream().anyMatch(f -> f.getName().equalsIgnoreCase(name));
	}
	
	public void clearFields() {
		this.fields.clear();
	}

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

	public BufferedImage getAlternateRearImage() {
		return alternateRear;
	}

	public Field getFieldByName(final String name) {
		return fields.stream().filter(f -> f.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	/**
	 * @return A copy of the Field List
	 */
	public List<Field> getFields() {
		return new ArrayList<>(fields);
	}

	public List<Field> getFrontFields() {
		return this.fields.stream().filter(f -> f.getSide() == CardSide.FRONT).collect(Collectors.toList());
	}

	public BufferedImage getFrontImage() {
		return front;
	}

	public BufferedImage getFrontImageCopy() {
		return this.getImageCopy(this.front);
	}

	public String getImageBase64() {
		return frontBase64;
	}

	public BufferedImage getImageCopy(BufferedImage img) {
		if (this.front != null) {
			BufferedImage bImg = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
			Graphics2D g = bImg.createGraphics();
			g.drawImage(img, 0, 0, null);
			g.dispose();
			return bImg;
		}
		return null;
	}

	public List<Field> getRearFields() {
		return this.fields.stream().filter(f -> f.getSide() == CardSide.REAR).collect(Collectors.toList());
	}

	public BufferedImage getRearImage() {
		return rear;
	}

	public BufferedImage getRearImageCopy() {
		return this.getImageCopy(this.rear);
	}
	
	public void removeField(Field field) {
		this.fields.remove(field);
	}
	
	public void setAlternateRearImage(BufferedImage image) {
		this.alternateRear = image;
		this.alternateRearBase64 = null;
	}
	
	public void setFrontImage(BufferedImage image, boolean deleteFrontFields) {
		this.front = image;
		this.frontBase64 = null;
		if(deleteFrontFields) 
			deleteFields(CardSide.FRONT);
	}

	public void setImageBase64(String imageBase64) {
		this.frontBase64 = imageBase64;
	}

	public void setRearImage(BufferedImage image, boolean deleteRearFields) {
		this.rear = image;
		this.rearBase64 = null;
		if(deleteRearFields) 
			deleteFields(CardSide.REAR);
	}
}
