package vv3ird.populatecard.data;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import vv3ird.populatecard.control.ProjectManager;

public class Field {
	
	private static final String TAB_SPACING = ".....";

	private String name = null;

	private Color color;

	private Rectangle rect = null;
	
	private CardSide side = CardSide.FRONT;
	
	private FieldType type = FieldType.TEXT_LEFT;
	
	private VerticalTextAlignment verticalAlignment = VerticalTextAlignment.TEXT_TOP;
	
	private String font = null;
	
	private int fontSize = 20;

	private Field linkedField = null;
	
	private int linkDepth = 0;
	
	private boolean indented = true;
	
	private int style = Font.PLAIN;
	
	private boolean resizeText = false;

	public Field(String name, Dimension pos1, Dimension pos2, Color outlineColor) {
		this(name, pos1, pos2, outlineColor, CardSide.FRONT);
	}

	public Field(String name, Dimension pos1, Dimension pos2, Color outlineColor, CardSide side) {
		this(name, pos1, pos2, outlineColor, side, FieldType.TEXT_LEFT);
	}

	public Field(String name, Dimension pos1, Dimension pos2, Color outlineColor, FieldType type) {
		this(name, pos1, pos2, outlineColor, CardSide.FRONT, type);
	}
	public Field(String name, Dimension pos1, Dimension pos2, Color outlineColor, CardSide side, FieldType type) {
		this(name, pos1, pos2, outlineColor, side, type, null, 20);
	}
	
	public Field(String name, Dimension pos1, Dimension pos2, Color outlineColor, CardSide side, FieldType type, String fontName, int fontSize) {
		this(name, pos1, pos2, outlineColor, side, type, fontName, fontSize, null);
	}

	public Field(String name, Dimension pos1, Dimension pos2, Color outlineColor, CardSide side, FieldType type, String fontName, int fontSize, Field linkedField) {
		this.name = name;
		this.side = side;
		this.type = type;
		this.linkedField = linkedField;
		int x1 = pos1.width < pos2.width ? pos1.width : pos2.width;
		int x2 = pos1.width < pos2.width ? pos2.width : pos1.width;
		int y1 = pos1.height < pos2.height ? pos1.height : pos2.height;
		int y2 = pos1.height < pos2.height ? pos2.height : pos1.height;
		this.rect = new Rectangle(x1, y1, x2 - x1, y2 - y1);
		this.color = outlineColor;
		this.font = fontName;
		this.fontSize = fontSize;
	}

	public void drawRect(Graphics g, Font font) {
		Font f = font.deriveFont(style, 12);
		String name = this.name + (linkedField != null || linkDepth > 0 ? " " + linkDepth : "");
		Color old = g.getColor();
		Font oldFont = g.getFont();
		FontMetrics fm = g.getFontMetrics(f);
		int lineHeight = fm.getHeight();
		int ascent = fm.getAscent();
		int namePlateWidth = fm.stringWidth(name) + 4;
		int namePlateheight = fm.getHeight() + 4;
		g.setColor(Color.DARK_GRAY);
		g.setFont(f);
		g.fillRect(this.rect.x, this.rect.y, namePlateWidth, namePlateheight);
		g.setColor(color);
		g.drawRect(this.rect.x, this.rect.y, this.rect.width, this.rect.height);
		g.drawChars(name.toCharArray(), 0, name.toCharArray().length, this.rect.x+2, this.rect.y+ascent);
		g.setColor(Color.GRAY);
		fm = g.getFontMetrics(font.deriveFont(style, fontSize));
		ascent = fm.getAscent();
		lineHeight = fm.getHeight();
		int xstart = rect.x + 10;
		int xend = rect.width + rect.x - 10;
		int steps = rect.height/lineHeight > 0 ? rect.height/lineHeight : 1;
		for(int i = 0; i < steps;i++) {
			g.drawLine(xstart, rect.y + i*lineHeight + ascent, xend, rect.y + i*lineHeight + ascent);
		}
		g.setColor(old);
		g.setFont(oldFont);
	}
	
	public int getLinkDepth() {
		return linkDepth;
	}
	
	public void setLinkDepth(int linkDepth) {
		this.linkDepth = linkDepth;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Rectangle getRect() {
		return rect;
	}

	public void setRect(Rectangle rect) {
		this.rect = rect;
	}
	
	public CardSide getSide() {
		return side;
	}
	
	public boolean hasLinkedField() {
		return this.linkedField != null;
	}
	
	public Field getLinkedField() {
		return this.linkedField ;
	}
	
	public void setLinkedField(Field linkedField) {
		linkedField = Objects.requireNonNull(linkedField);
		if (this.linkedField != null)
			this.linkedField.setLinkedField(linkedField);
		else {
			this.linkedField = linkedField;
			this.linkedField.setLinkDepth(this.linkDepth+1);
		}
	}
	
	public void removeLinkedField() {
		this.linkedField = null;
	}
	
	public void setType(FieldType type) {
		this.type = type;
	}
	
	public FieldType getType() {
		return type;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name + " " + linkDepth + " (" + this.side + ")";
	}
	
	public String getFont() {
		return font;
	}
	
	public int getFontSize() {
		return fontSize;
	}
	
	public void setFont(String font) {
		this.font = font;
	}
	
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
	
	public void setSide(CardSide side) {
		this.side = side;
	}
	
	public void setX(int x) {
		this.rect.x = x;
	}
	
	public void setY(int y) {
		this.rect.y = y;
	}

	public void setWidth(int width) {
		this.rect.width = width;
	}

	public void setHeight(int height) {
		this.rect.height = height;
	}
	
	public int getX() {
		return this.rect.x;
	}
	
	public int getY() {
		return this.rect.y;
	}
	
	public int getWidth() {
		return this.rect.width;
	}

	public int getHeight() {
		return this.rect.height;
	}
	
	public void setIndented(boolean indented) {
		this.indented = indented;
	}
	
	public boolean isIndented() {
		return indented;
	}
	
	public boolean resizeText() {
		return resizeText;
	}
	
	public void setResizeText(boolean resizeText) {
		this.resizeText = resizeText;
	}

	public void drawContent(Graphics2D gFront, Graphics2D gRear, String text, Font font) {
		if(this.getType() == FieldType.IMAGE)  {
			if(!ProjectManager.containsImageLink(text))
				text = "<img>" + text + "</img>";
			drawImage(gFront, gRear, text, font);
		}
		else {
			List<String> paragraphs = splitIntoParagraphs(text, "-n-");
			drawParagraphs(gFront, gRear, paragraphs, font);
		}
	}
	
	
	protected void drawParagraphs(Graphics2D gFront, Graphics2D gRear, List<String> paragraphs, Font font) {
		Graphics2D g = this.getSide() == CardSide.FRONT ? gFront : gRear;
		font = font.deriveFont(this.style, fontSize);
		g.setFont(font);
		g.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		FontMetrics metrics = g.getFontMetrics(font);
		int width = this.rect.width;
		int lineHeight = metrics.getHeight();
		List<String> drawnLines = new LinkedList<>();
		List<String> drawnParagraphs = new LinkedList<>();
		int imageHeightOffset = 0;
		for (String paragraph : paragraphs) {
			// Prepare paragraph to be drawn on surface
			String textToDraw = paragraph;
			if (resizeText) {
				while (metrics.stringWidth(textToDraw.replace("\t", TAB_SPACING)) > width) {
					font = font.deriveFont(style, font.getSize()-1);
					g.setFont(font);
					metrics = g.getFontMetrics(font);
					lineHeight = metrics.getHeight();
				}
					
			}
			List<String> lines = new LinkedList<>();
			String[] arr = textToDraw.split(" ");
			String line = "";
			int tabLength = metrics.stringWidth(TAB_SPACING);
			// Split paragraph into lines depending on Field width
			for (int i = 0; i < arr.length; i++) {
				if (metrics.stringWidth((line + (!line.isEmpty() ? " " : "" )  + arr[i]).replace("\t", TAB_SPACING).replace("-$-", "")) < width) {
					line += (!line.isEmpty() ? " " : "" ) + arr[i];
				} else {
					if(!line.isEmpty())
						lines.add(line.charAt(0) == ' ' ? line.substring(1) : line);
					line = (!line.isEmpty() ? " " : "" ) + arr[i];
				}
			}
			if (!line.isEmpty()) {
				lines.add(line.charAt(0) == ' ' ? line.substring(1) : line);
			}
			drawnParagraphs.add(paragraph);
			// Draw lines on surface, continue overspilling text onto linked fields if possible.
			int y = metrics.getAscent() + 2 + (lineHeight * drawnLines.size());
			if (this.verticalAlignment == VerticalTextAlignment.TEXT_CENTER) {
				y = ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent() + 2;
				if (drawnLines.size() > 1)
					y = y - ((lineHeight * (drawnLines.size() - 1) / 2));
				if (y<0)
					y = rect.y;
			}
			// Determine the X coordinate for the text
			// Determine the Y coordinate for the text (note we add the ascent, as in java
			// 2d 0 is top of the screen)
			// int y = ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
			for (String l : lines) {
				String orgLine = l;
				boolean endOfParagraph = l.endsWith("-$-");
				if(endOfParagraph)
					l = l.substring(0, l.lastIndexOf("-$-"));
				String[] words = l.split(" ");
				// set linestart depending on set text alignment, default is left aligned
				int lineStart = 0;
				if (this.type == Field.FieldType.TEXT_CENTER)
					lineStart = (rect.width - metrics.stringWidth(l)) / 2;
				else if (this.type == Field.FieldType.TEXT_RIGHT)
					lineStart = (rect.width - metrics.stringWidth(l));
				int spacing = metrics.stringWidth(" ");
				if (this.type == Field.FieldType.TEXT_BLOCK) {
					int wordsLength = metrics.stringWidth(Arrays.asList(words).stream().collect(Collectors.joining()).replace("\t", TAB_SPACING));
					spacing = (rect.width-wordsLength)/(words.length >1 ? words.length-1 : words.length);
					if (wordsLength + metrics.stringWidth(" ")*words.length < rect.width*0.60f || endOfParagraph)
						spacing = metrics.stringWidth(" ");
				}
				if(ProjectManager.isBase64Image(l)) {
					BufferedImage bimg = ProjectManager.decodeImageFromBase64(l.substring(6, l.lastIndexOf("</imgb>")));
					if(bimg != null) {
						int height = (int) ((((float)rect.getWidth())/bimg.getWidth())* bimg.getHeight());
						Image img =  bimg.getScaledInstance((int)rect.getWidth(), height, BufferedImage.SCALE_SMOOTH);
						if (height+y+imageHeightOffset <= rect.height)
							g.drawImage(img, rect.x, rect.y + y, null);
						imageHeightOffset += height;
					}
					if (imageHeightOffset+y <= rect.height) {
						drawnLines.add(orgLine);
						imageHeightOffset += lineHeight;
					}
				}
				else {
					// Draw word for word with the correct spacing
					int wordsWidth = 0;
					for (int i = 0; i < words.length; i++) {
						g.drawString(words[i].replace("\t", ""), (words[i].contains("\t") ? tabLength : 0) + rect.x + lineStart + wordsWidth + (i*spacing), rect.y + y + imageHeightOffset);
						wordsWidth += metrics.stringWidth(words[i].replace("\t", "")) + (words[i].contains("\t") ? tabLength : 0);
					}
					drawnLines.add(orgLine);
					y += lineHeight;
				}
				// If end of Field is reached, the rest of the text is either transfered to the
				// linked field or not drawn at all
				if ((y+imageHeightOffset) > rect.height && this.hasLinkedField()) {
					List<String> linesToCopyOver = new LinkedList<>();
					linesToCopyOver.addAll(lines);
					linesToCopyOver.removeAll(drawnLines);
					String restParagraph = linesToCopyOver.stream().collect(Collectors.joining(" "));
					List<String> paragraphsToCopyOver = new LinkedList<>();
					List<String> restParagraphs = new LinkedList<>();
					paragraphsToCopyOver.add(restParagraph);
					restParagraphs.addAll(paragraphs);
					restParagraphs.removeAll(drawnParagraphs);
					paragraphsToCopyOver.addAll(restParagraphs);
					this.getLinkedField().drawParagraphs(gFront, gRear, paragraphsToCopyOver, font);
					return;
				}
				else if ((y+imageHeightOffset) > rect.height) {
					return;
				}
			}
		}
		
	}
	
	public void drawImage(Graphics2D gFront, Graphics2D gRear, String content, Font font) {
		Graphics2D g = this.getSide() == CardSide.FRONT ? gFront : gRear;
		if(ProjectManager.containsImageLink(content))
			content = ProjectManager.processMediaEntry(content);
		if(ProjectManager.isBase64Image(content)) {
			BufferedImage bimg = ProjectManager.decodeImageFromBase64(content.substring(6, content.lastIndexOf("</imgb>")));
			if(bimg != null) {
				int height = (int) ((((float)rect.getWidth())/bimg.getWidth())* bimg.getHeight());
				int width = (int)rect.getWidth();
				if (height > rect.getHeight()) {
					height = (int)rect.getHeight();
					width = (int) ((((float)rect.getHeight())/bimg.getHeight())* bimg.getWidth());
				}
				Image img =  bimg.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
				g.drawImage(img, rect.x, rect.y, null);
			}
		}
	}

	/**
	 * Splits a given text into seperate paragraphs. Puts "-$-" at the end of every paragraph, so it can be identified as such.
	 * @param text	Give text
	 * @param splitter	String to split the text by
	 * @return	Paragraphs with a \t if indented is set to true
	 */
	private List<String> splitIntoParagraphs(String text, String splitter) {
		if (this.indented)
			text = text.replaceAll(splitter, splitter + "\t");
		List<String> paragraphsZ = Arrays.asList(text.split(splitter));
		List<String> paragraphs  = new LinkedList<>();
		for (int i = 0; i < paragraphsZ.size(); i++) {
			String p = paragraphsZ.get(i) + "-$-";
			if (i == 0 && this.indented)
				p = "\t" + p;
			if (ProjectManager.containsBase64Image(p)) {
				while (ProjectManager.containsBase64Image(p)) {
					String prevP = p.substring(0, p.indexOf("<imgb>")) + "-$-";
					String imgP = p.substring(p.indexOf("<imgb>"), p.indexOf("</imgb>")+7);
					p = "\t" + p.substring(p.indexOf("</imgb>")+7);
					if(!prevP.isEmpty() && !prevP.equals("-$-"))
						paragraphs.add(prevP);
					paragraphs.add(imgP);
				}
				if(!p.isEmpty())
					paragraphs.add(p);
			}
			else
				paragraphs.add(p);
		}
		return paragraphs;
	}
	
	public Field clone() {
		return new Field(name, new Dimension(rect.x, rect.y), new Dimension(rect.x+rect.width, rect.y + rect.height), color, side, type, font, fontSize, linkedField);
	}

	public static enum CardSide {
		
		FRONT("Front"), REAR("Back");
		
		private String display = null;
		
		private CardSide(String display) { this.display = display;}
		@Override
		public String toString() {
			return display;
		}
	}
	
	public static enum FieldType {
		
		TEXT_LEFT("Text left-aligned"), TEXT_RIGHT("Text right-aligned"), TEXT_CENTER("Text centered"), TEXT_BLOCK("Text block"), IMAGE("Image");

		private String display = null;
		
		private FieldType(String display) { this.display = display;}
		
		@Override
		public String toString() {
			return display;
		}
	}
	
	public static enum VerticalTextAlignment {
		
		TEXT_CENTER("Text centered"), TEXT_TOP("Text top-aligned"), TEXT_BOTTOM("Text bottom-aligned");
		
		private String display = null;
		
		private VerticalTextAlignment(String display) { this.display = display;}
		
		@Override
		public String toString() {
			return display;
		}
	}
}
