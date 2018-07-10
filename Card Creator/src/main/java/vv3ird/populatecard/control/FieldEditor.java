package vv3ird.populatecard.control;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import vv3ird.populatecard.CardCreator;
import vv3ird.populatecard.data.Field;
import vv3ird.populatecard.data.Field.CardSide;

public class FieldEditor {

	private List<Field> fields = null;
	
	private List<Field> frontAdditions = new ArrayList<>();
	private List<Field> rearAdditions = new ArrayList<>();
	
	private List<Field> fieldsOnEditMode = null;

	private Field orgField = null;
	
	private Field editedField = null;
	
	private boolean changeSwitch = false;
	
	public FieldEditor(List<Field> fields) {
		this.fields = new ArrayList<>(fields);
		
		for (Field field : fields.stream().filter(f -> f.getSide() == CardSide.FRONT).collect(Collectors.toList())) {
			Field linked = field;
			while((linked = linked.getLinkedField()) != null) {
				if (linked.getSide() == Field.CardSide.FRONT)
					frontAdditions.add(linked);
				else
					rearAdditions.add(linked);
			}
		}
		for (Field field : fields.stream().filter(f -> f.getSide() == CardSide.REAR).collect(Collectors.toList())) {
			Field linked = field;
			while((linked = linked.getLinkedField()) != null) {
				if (linked.getSide() == Field.CardSide.FRONT)
					frontAdditions.add(linked);
				else
					rearAdditions.add(linked);
			}
		}
	}
	
	public boolean isEditMode() {
		return editedField != null;
	}
	
	public boolean setPos(int x, int y) {
		if (isEditMode()) {
			this.changeSwitch = true;
			this.editedField.setX(x);
			this.editedField.setY(y);
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean setWidth(int width) {
		if (isEditMode() && width >= 20) {
			this.changeSwitch = true;
			this.editedField.setWidth(width);
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean setHeight(int height) {
		if (isEditMode() && height >= 20) {
			this.changeSwitch = true;
			this.editedField.setHeight(height);
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean editField(Field field) {
		if (this.fields.contains(field) || this.rearAdditions.contains(field) || this.frontAdditions.contains(field)) {
			this.fieldsOnEditMode = new LinkedList<>(fields);
			this.fieldsOnEditMode.addAll(this.rearAdditions);
			this.fieldsOnEditMode.addAll(this.frontAdditions);
			this.fieldsOnEditMode.remove(field);
			this.changeSwitch = false;
			this.orgField = field;
			this.editedField = field.clone();
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean saveField() {
		if (isEditMode()) {
			if (this.changeSwitch)  {
				this.orgField.setX(this.editedField.getX());
				this.orgField.setY(this.editedField.getY());
				this.orgField.setWidth(this.editedField.getWidth());
				this.orgField.setHeight(this.editedField.getHeight());
			}
			this.editedField = null;
			this.orgField = null;
			this.fieldsOnEditMode = null;
			this.changeSwitch = false;
			return true;
		}
		else {
			return false;
		}
	}

	public boolean discardEdits() {
		if (isEditMode()) {
			this.editedField = null;
			this.orgField = null;
			this.fieldsOnEditMode = null;
			this.changeSwitch = false;
			return true;
		}
		else {
			return false;
		}		
	}
	
	public void drawFields(BufferedImage img, Field.CardSide side, boolean fullDraw, boolean drawEdit) {
		List<Field> fields = isEditMode() ? this.fieldsOnEditMode : this.fields;
		Graphics2D g = img.createGraphics();
		if (fullDraw) {
			fields.stream().filter(f -> f.getSide() == side).forEach(f -> f.drawRect(g, CardCreator.getFont(f.getFont())));
			if(!isEditMode()) {
				frontAdditions.stream().filter(f -> f.getSide() == side).forEach(f -> f.drawRect(g, CardCreator.getFont(f.getFont())));
				rearAdditions.stream().filter(f -> f.getSide() == side).forEach(f -> f.drawRect(g, CardCreator.getFont(f.getFont())));
			}
		}
		if(isEditMode() && drawEdit && side == this.editedField.getSide()) {
			this.editedField.drawRect(g, CardCreator.getFont(this.editedField.getFont()));
			int x = this.editedField.getX();
			int y = this.editedField.getY();
			int width = this.editedField.getWidth();
			int height = this.editedField.getHeight();
			g.setColor(editedField.getColor());
			// Top left corner
			g.fillOval(x-4, y-4, 8, 8);
			// top right cornor
			g.fillOval(x-4+width, y-4, 8, 8);
			// Bottom left cornor
			g.fillOval(x-4, y-4+height, 8, 8);
			// Bottom right cornor
			g.fillOval(x-4+width, y-4+height, 8, 8);
		}
		g.dispose();
	}
	
	public boolean editFieldContaining(Point p, CardSide side) {
		Field field = this.fields.parallelStream().filter(f -> f.getRect().contains(p) && f.getSide() == side).findFirst().orElse(null);
		if (field == null)
			field = this.rearAdditions.parallelStream().filter(f -> f.getRect().contains(p) && f.getSide() == side).findFirst().orElse(null);
		if (field == null)
			field = this.frontAdditions.parallelStream().filter(f -> f.getRect().contains(p) && f.getSide() == side).findFirst().orElse(null);
		return this.editField(field);
	}

	public int getWidth() {
		return isEditMode() ? this.editedField.getWidth() : -1;
	}

	public int getHeight() {
		return isEditMode() ? this.editedField.getHeight() : -1;
	}

	public int getX() {
		return isEditMode() ? this.editedField.getX() : -1;
	}

	public int getY() {
		return isEditMode() ? this.editedField.getY() : -1;
	}

	public boolean editFieldContains(Point point) {
		if(isEditMode()) {
			Rectangle r = (Rectangle)this.editedField.getRect().clone();
			r.grow(4, 4);
			return r.contains(point);
			
		}
		return false;
	}
	
	public Corner getCornerOnEditField(Point p) {
		if(isEditMode()) {
			Rectangle r = this.editedField.getRect();
			Rectangle topLeft = new Rectangle(r.x-4, r.y-4, 8, 8);
			Rectangle topRight = new Rectangle(r.x-4+r.width, r.y-4, 8, 8);
			Rectangle bottomLeft = new Rectangle(r.x-4, r.y-4+r.height, 8, 8);
			Rectangle bottomRight = new Rectangle(r.x-4+r.width, r.y-4+r.height, 8, 8);
			if(topLeft.contains(p))
				return Corner.TOP_LEFT;
			if(topRight.contains(p))
				return Corner.TOP_RIGHT;
			if(bottomLeft.contains(p))
				return Corner.BOTTOM_LEFT;
			if(bottomRight.contains(p))
				return Corner.BOTTOM_RIGHT;
		}
		return Corner.NONE;
	}

	public static enum Corner {
		NONE, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
	}

}
