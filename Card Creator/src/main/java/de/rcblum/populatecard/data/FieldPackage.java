package de.rcblum.populatecard.data;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
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

import de.rcblum.populatecard.data.Field.CardSide;

public class FieldPackage {

	private transient BufferedImage front = null;

	private transient BufferedImage rear = null;

	private String frontBase64 = null;

	private String rearBase64 = null;

	private List<Field> fields = new LinkedList<>();

	public FieldPackage() {
		this(null, null);
	}

	public FieldPackage(BufferedImage front, BufferedImage rear) {
		this.front = front;
		this.rear = rear;
		this.frontBase64 = null;
		this.rearBase64 = null;
	}

	public BufferedImage getFrontImage() {
		return front;
	}

	public BufferedImage getRearImage() {
		return rear;
	}

	public void addField(Field field) {
		fields = Objects.requireNonNull(fields, "Parameter field cannot be null");
		this.fields.add(field);
	}

	public void addFields(List<Field> fields) {
		fields = Objects.requireNonNull(fields, "Parameter fields cannot be null");
		this.fields.addAll(fields);
	}
	
	public void clearFields() {
		this.fields.clear();
	}

	public void removeField(Field field) {
		this.fields.remove(field);
	}

	public String getImageBase64() {
		return frontBase64;
	}

	public void setImageBase64(String imageBase64) {
		this.frontBase64 = imageBase64;
	}

	/**
	 * @return A copy of the Field List
	 */
	public List<Field> getFields() {
		return new ArrayList<>(fields);
	}

	public static void save(FieldPackage fieldPack, Path path) throws IOException {
		fieldPack.encodeImages();
		String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(fieldPack);
		try {
			byte[] utf8JsonString = jsonString.getBytes("UTF-8");
			Files.write(path, utf8JsonString, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (UnsupportedEncodingException e) {
		}
	}

	public static FieldPackage load(Path path) throws IOException {
		FieldPackage fieldPack = null;
		byte[] fieldPackBytes = Files.readAllBytes(path);
		String fieldPackString = new String(fieldPackBytes, "UTF-8");
		Gson gson = new Gson();
		fieldPack = gson.fromJson(fieldPackString, FieldPackage.class);
		if (fieldPack.getImageBase64() != null) {
			fieldPack.decodeImages();
		}
		return fieldPack;
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
		} catch (final IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
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
		} catch (final IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
	}

	public boolean checkNameForDuplicate(final String name) {
		return fields.stream().anyMatch(f -> f.getName().equalsIgnoreCase(name));
	}

	public Field getFieldByName(final String name) {
		return fields.stream().filter(f -> f.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	public void setFrontImage(BufferedImage image, boolean deleteFrontFields) {
		this.front = image;
		this.frontBase64 = null;
		if(deleteFrontFields) {
			List<Field> tbr = new ArrayList<Field>(
					this.fields.stream().filter(f -> f.getSide() == CardSide.FRONT).collect(Collectors.toList()));
			this.fields.removeAll(tbr);
		}
	}

	public void setRearImage(BufferedImage image, boolean deleteRearFields) {
		this.rear = image;
		this.rearBase64 = null;
		List<Field> tbr = new ArrayList<Field>(
				this.fields.stream().filter(f -> f.getSide() == CardSide.REAR).collect(Collectors.toList()));
		this.fields.removeAll(tbr);
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
	
	public BufferedImage getFrontImageCopy() {
		return this.getImageCopy(this.front);
	}
	
	public BufferedImage getRearImageCopy() {
		return this.getImageCopy(this.rear);
	}

	public List<Field> getFrontFields() {
		return this.fields.stream().filter(f -> f.getSide() == CardSide.FRONT).collect(Collectors.toList());
	}

	public List<Field> getRearFields() {
		return this.fields.stream().filter(f -> f.getSide() == CardSide.REAR).collect(Collectors.toList());
	}
}
