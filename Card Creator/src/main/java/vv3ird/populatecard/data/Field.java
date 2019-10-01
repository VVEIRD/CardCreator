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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import vv3ird.populatecard.control.ProjectManager;

/**
 * A Field contains an area in which content should be drawn onto the cards
 * front or rear image. Content can be either text with images or only an image.
 * Text can be <b>bold </b> or <i>italic</i> and separated into paragraphs.
 * Paragraphs can be indented, Font and size can be set. Fontsize can be
 * automatically adjusted to fit the width of the field, but then only one line
 * is supported.<br>
 * Fields can be linked together, so if a given text is larger than the field,
 * the spillover is sent to the linked field.
 * 
 * @author VV3IRD
 *
 */
public class Field {
	
	private static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

	/**
	 * Spacing used for tabs (\t)
	 */
	private static final String TAB_SPACING = ".....";

	/**
	 * Name of the Field
	 */
	private String name = null;

	/**
	 * Color of the displayed rectangle
	 */
	private Color color;

	/**
	 * Rectangle describing the position and size of the field
	 */
	private Rectangle rect = null;

	/**
	 * Side of the card to which the Field should be apllied to
	 */
	private CardSide side = CardSide.FRONT;

	/**
	 * Field type, e.g. Text with alignment, image)
	 */
	private FieldType type = FieldType.TEXT_LEFT;

	/**
	 * Vertical aligment, eg if the text should glued to the tp, center or bottom
	 */
	private VerticalTextAlignment verticalAlignment = VerticalTextAlignment.TEXT_TOP;

	/**
	 * Name of the font to be used
	 */
	private String font = null;
	
	/**
	 * Script that will be calculate the content based on the cav data
	 */
	private String javaScript = null;

	/**
	 * Size of the font
	 */
	private int fontSize = 20;

	/**
	 * Linked field, spillover text from this Fieeld will be drawn on the linked field
	 */
	private Field linkedField = null;

	/**
	 * Index on how deep this Field is in the link chain
	 */
	private int linkDepth = 0;

	/**
	 * Flag if paragraphs should be indented or not.
	 */
	private boolean indented = true;

	/**
	 * Base style of the Field
	 */
	private int style = Font.PLAIN;

	/**
	 * Flag if the given text should be resized to fit the field width. only single line text supported
	 */
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

	public Field(String name, Dimension pos1, Dimension pos2, Color outlineColor, CardSide side, FieldType type,
			String fontName, int fontSize) {
		this(name, pos1, pos2, outlineColor, side, type, fontName, fontSize, null);
	}

	public Field(String name, Dimension pos1, Dimension pos2, Color outlineColor, CardSide side, FieldType type,
			String fontName, int fontSize, Field linkedField) {
		this(name, pos1, pos2, outlineColor, side, type, fontName, fontSize, null, true, null);
	}

	public Field(String name, Dimension pos1, Dimension pos2, Color outlineColor, CardSide side, FieldType type,
			String fontName, int fontSize, Field linkedField, boolean indented, String javaScript) {
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
		this.indented = indented;
		this.javaScript = javaScript;
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
		g.drawChars(name.toCharArray(), 0, name.toCharArray().length, this.rect.x + 2, this.rect.y + ascent);
		g.setColor(Color.GRAY);
		fm = g.getFontMetrics(font.deriveFont(style, fontSize));
		ascent = fm.getAscent();
		lineHeight = fm.getHeight();
		int xstart = rect.x + 10;
		int xend = rect.width + rect.x - 10;
		int steps = rect.height / lineHeight > 0 ? rect.height / lineHeight : 1;
		for (int i = 0; i < steps; i++) {
			g.drawLine(xstart, rect.y + i * lineHeight + ascent, xend, rect.y + i * lineHeight + ascent);
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
		return this.linkedField;
	}

	public void setLinkedField(Field linkedField) {
		linkedField = Objects.requireNonNull(linkedField);
		if (this.linkedField != null)
			this.linkedField.setLinkedField(linkedField);
		else {
			this.linkedField = linkedField;
			this.linkedField.setLinkDepth(this.linkDepth + 1);
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
	
	public boolean isBold() {
		return (style & Font.BOLD) == Font.BOLD;
	}
	
	public boolean isItalic() {
		return (style & Font.ITALIC) == Font.ITALIC;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String tos = "Name: " + name + "\r\n"
				   + "   Type: " + this.type + "\r\n"
				   + "   Side: " + this.side + "\r\n"
				   + "   Font: " + this.font + "\r\n"
				   + "   JS:   " + this.isCalculated() + "\r\n"
				   + "   Depth: " + this.linkDepth
				;
		return tos; //name + " " + linkDepth + " (" + this.side + ")";
	}

	public String getFont() {
		return font;
	}
	
	public String getJavaScript() {
		return javaScript;
	}
	
	public boolean isCalculated() {
		return javaScript != null;
	}
	
	public void setJavaScript(String javaScript) {
		this.javaScript = javaScript;
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
	
	public void drawContent(Graphics2D gFront, Graphics2D gRear, String text, Map<String, String> rowData) {
		this.drawContent(gFront, gRear, text, this.getFieldStyle(), rowData);
	}

	protected void drawContent(Graphics2D gFront, Graphics2D gRear, String text, FieldStyle fieldStyle, Map<String, String> rowData) {
		System.out.println(text);
		if (this.getType() == FieldType.IMAGE) {
			if (!ProjectManager.containsImageLink(text) && !ProjectManager.isBase64Image(text))
				text = "<img>" + text + "</img>";
			drawImage(gFront, gRear, text);
		} else {
			System.out.println();
			System.out.println("Name: " + name);
			System.out.println("Has JS: " + (this.javaScript != null));
			if (this.javaScript != null)
				text = evalJS(text, rowData);
			List<String> paragraphs = splitIntoParagraphs(text, "-n-");
			drawParagraphs(gFront, gRear, paragraphs, fieldStyle);
		}
	}
	
	private String evalJS(String text, Map<String, String> rowData) {
		String js = "";
		for (String key : rowData.keySet()) {
			js += String.format("var %s = \"%s\"\n", key, rowData.get(key));
		}
		js += javaScript;
		System.out.println("--------------------------------------------------");
		System.out.println("Calling Javascript");
		System.out.println("Source:");
		System.out.println("--------------------------------------------------");
		System.out.println(js);
		System.out.println("--------------------------------------------------");
	    try {
			engine.eval(js);
			Invocable invocable = (Invocable) engine;
			Object result = invocable.invokeFunction("calculate", text);
			text = String.valueOf(result);
			System.out.println("Result: " + text);
			System.out.println("--------------------------------------------------");
		} catch (NoSuchMethodException | ScriptException e) {
			e.printStackTrace();
		}
		return text;
	}

	public FieldStyle getFieldStyle() {
		return new FieldStyle(this);
	}

	protected void drawParagraphs(Graphics2D gFront, Graphics2D gRear, List<String> paragraphs, FieldStyle fieldStyle) {
		FieldStyle newStyle = new FieldStyle(this);
		if(fieldStyle.bold)
			newStyle = newStyle.bold();
		if(fieldStyle.italic)
			newStyle = newStyle.italic();
		fieldStyle = newStyle;
		Graphics2D g = this.getSide() == CardSide.FRONT ? gFront : gRear;
		g.setFont(fieldStyle.getFont());
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		int width = this.rect.width;
		List<String> drawnLines = new LinkedList<>();
		List<String> drawnParagraphs = new LinkedList<>();
		int imageHeightOffset = 0;
		for (String paragraph : paragraphs) {
			// Prepare paragraph to be drawn on surface
			String textToDraw = paragraph;
			// Resize font if text should be resized to fit
			if (resizeText) {
				// calc string width
				char[] calcString = textToDraw.replace("\t", TAB_SPACING).toCharArray();
				int stringWidth = 0;
				FieldStyle fieldStyleCalc = fieldStyle.clone();
				stringWidth = calculateStringWidth(calcString, fieldStyleCalc, g);
				while (stringWidth > width) {
					fieldStyle = fieldStyle.smaller();
					fieldStyleCalc = fieldStyle.clone();
					stringWidth = calculateStringWidth(calcString, fieldStyleCalc, g);
				}
			}
			List<String> lines = new LinkedList<>();
			String[] arr = textToDraw.split(" ");
			int[] wordWidth = new int[arr.length];
			FieldStyle fontCalc = fieldStyle.clone();
			String line = "";
			int lineWidth = 0;
			int spaceWidth = fieldStyle.noBold().noItalic().getMetrics(g).stringWidth(" ");
			int tabLength =  fieldStyle.noBold().noItalic().getMetrics(g).stringWidth(TAB_SPACING);
			for (int f = 0; f < wordWidth.length; f++) {
				char[] calcString = arr[f].replace("\t", TAB_SPACING).replace("-$-", "").toCharArray();
				int stringWidth = 0;
				for (int i = 0; i < calcString.length; i++) {
					fontCalc = checkFontChange(calcString, i, fontCalc);
					if (fontCalc.hasChangedToBold() || fontCalc.hasChangedToItalic())
						i += 3;
					else if (fontCalc.hasChangedFromBold() || fontCalc.hasChangedFromItalic())
						i += 4;
					if (i < calcString.length)
						stringWidth += fontCalc.getMetrics(g).stringWidth(String.valueOf(calcString[i]));
				}
				wordWidth[f] = stringWidth;
				if (lineWidth + (lineWidth > 0 ? spaceWidth : 0) + wordWidth[f] < width) {
					line += (!line.isEmpty() ? " " : "") + arr[f];
					lineWidth += (lineWidth > 0 ? spaceWidth : 0) + wordWidth[f];
				} else {
					if (!line.isEmpty())
						lines.add(line.charAt(0) == ' ' ? line.substring(1) : line);
					line = (!line.isEmpty() ? " " : "") + arr[f];
					lineWidth = wordWidth[f];
				}
			}
			if (!line.isEmpty() && !line.equals(" ")) {
				lines.add(line.charAt(0) == ' ' ? line.substring(1) : line);
			}
			drawnParagraphs.add(paragraph);
			// Draw lines on surface, continue overspilling text onto linked fields if
			// possible.
			int y = fieldStyle.getMetrics(g).getAscent() + 2 + (fieldStyle.getMetrics(g).getHeight() * drawnLines.size());
			if (this.verticalAlignment == VerticalTextAlignment.TEXT_CENTER) {
				int removeLine = 0;
				do {
				y = ((rect.height - fieldStyle.getMetrics(g).getHeight()) / 2) + fieldStyle.getMetrics(g).getAscent() + 2;
				if (drawnLines.size() > 1)
					y = y - ((fieldStyle.getMetrics(g).getHeight() * (drawnLines.size() - (1 + removeLine)) / 2));
				removeLine++;
				} while (y < 0);
			}
			
			for (String l : lines) {
				String orgLine = l;
				boolean endOfParagraph = l.endsWith("-$-");
				if (endOfParagraph)
					l = l.substring(0, l.lastIndexOf("-$-"));
				boolean indented = l.startsWith("\t");
				if (indented)
					l = l.substring(1);
				String[] words = l.split(" ");
				// Setup spacing independent of italic or bold
				int spacing = fieldStyle.noBold().noItalic().getMetrics(g).stringWidth(" ");
				//** Setup linestart, depending on set text alignment, default is left aligned (0)
				int lineStart = indented ? fieldStyle.noBold().noItalic().getMetrics(g).stringWidth(TAB_SPACING) : 0;
				// Check for either centered or right aligned text
				if (this.type == Field.FieldType.TEXT_CENTER || this.type == Field.FieldType.TEXT_RIGHT) {
					// calculate line width
					char[] calcString = l.toCharArray();
					fontCalc = fieldStyle.clone();
					lineWidth = calculateStringWidth(calcString, fontCalc, g);
					lineStart = (rect.width - lineWidth);
					if (this.type == Field.FieldType.TEXT_CENTER)
						lineStart = lineStart / 2;
				}
				// Check and calculate spacing for text in block format
				if (this.type == Field.FieldType.TEXT_BLOCK) {
					// Calc total width of all characters except whitepsace
					char[] calcString = l.replace(" ", "").replace("-$-", "").toCharArray();
					fontCalc = fieldStyle.clone();
					int lineWordWidth = calculateStringWidth(calcString, fontCalc, g);
					spacing = (rect.width - lineWordWidth - lineStart) / (words.length > 1 ? words.length - 1 : words.length);
					if (lineWordWidth + spaceWidth * words.length < rect.width * 0.60f || endOfParagraph)
						spacing = spaceWidth;
				}
				// If the given line is a base 64 encoded image, decode it and draw if possible
				if (ProjectManager.isBase64Image(l)) {
					BufferedImage bimg = ProjectManager.decodeImageFromBase64(l.substring(6, l.lastIndexOf("</imgb>")));
					if (bimg != null) {
						int height = (int) ((((float) rect.getWidth()) / bimg.getWidth()) * bimg.getHeight());
						Image img = bimg.getScaledInstance((int) rect.getWidth(), height, BufferedImage.SCALE_SMOOTH);
						if (height + y + imageHeightOffset <= rect.height)
							g.drawImage(img, rect.x, rect.y + y, null);
						imageHeightOffset += height;
					}
					if (imageHeightOffset + y <= rect.height) {
						drawnLines.add(orgLine);
						imageHeightOffset += fieldStyle.getMetrics(g).getHeight();
					}
				} else {
					// Draw char for char with the correct spacing
					int caret = 0;
					// System.out.println("Line: \"" + l + "\"");
					char[] lineArray = l.toCharArray();
					for (int i = 0; i < lineArray.length; i++) {
						fieldStyle = checkFontChange(lineArray, i, fieldStyle);
						if (fieldStyle.hasChangedToBold() || fieldStyle.hasChangedToItalic())
							i += 3;
						else if (fieldStyle.hasChangedFromBold() || fieldStyle.hasChangedFromItalic())
							i += 4;
						g.setFont(fieldStyle.getFont());
						if (i >= lineArray.length)
							break;
						if (lineArray[i] == ' ')
							caret += spacing;
						else if (lineArray[i] == '\t')
							caret += tabLength;
						else {
							g.drawString(String.valueOf(lineArray[i]), rect.x + lineStart + caret,
									rect.y + y + imageHeightOffset);
							caret += fieldStyle.getMetrics(g).stringWidth(String.valueOf(lineArray[i]));
						}
					}
					drawnLines.add(orgLine);
					y += fieldStyle.getMetrics(g).getHeight();
				}
				// If end of Field is reached, the rest of the text is either transfered to the
				// linked field or not drawn at all
				if ((y + imageHeightOffset) > rect.height && this.hasLinkedField()) {
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
					this.getLinkedField().drawParagraphs(gFront, gRear, paragraphsToCopyOver, fieldStyle);
					return;
				} else if ((y + imageHeightOffset) > rect.height) {
					return;
				}
			}
		}

	}

	private FieldStyle checkFontChange(char[] calcString, int i, FieldStyle fieldStyle) {
		fieldStyle.notChanged();
		if (i < 0 || i >= calcString.length)
			return fieldStyle;
		// check for bold
		if (calcString[i] == '<' && this.startsAt(calcString, i, "<b>".toCharArray())) {
			fieldStyle = fieldStyle.bold();
		} else if (calcString[i] == '<' && this.startsAt(calcString, i, "</b>".toCharArray())) {
			fieldStyle = fieldStyle.noBold();
		}
		// check for italic
		else if (i < calcString.length && calcString[i] == '<'
				&& this.startsAt(calcString, i, "<i>".toCharArray())) {
			fieldStyle = fieldStyle.italic();
		} else if (i < calcString.length && calcString[i] == '<'
				&& this.startsAt(calcString, i, "</i>".toCharArray())) {
			fieldStyle = fieldStyle.noItalic();
		}
		return fieldStyle;
	}

	private int calculateStringWidth(char[] calcString, FieldStyle fontCalc, Graphics2D g) {
		int stringWidth = 0;
		for (int i = 0; i < calcString.length; i++) {
			fontCalc = checkFontChange(calcString, i, fontCalc);
			if (fontCalc.hasChangedToBold() || fontCalc.hasChangedToItalic())
				i += 3;
			else if (fontCalc.hasChangedFromBold() || fontCalc.hasChangedFromItalic())
				i += 4;
			if (i < calcString.length)
				stringWidth += fontCalc.getMetrics(g).stringWidth(String.valueOf(calcString[i]));
		}
		return stringWidth;
	}

	private boolean startsAt(char[] calcString, int i, char[] charArray) {
		boolean startsWith = true;
		if (calcString.length < i + charArray.length)
			startsWith = false;
		else {
			for (int j = 0; j < charArray.length; j++) {
				if (calcString[i + j] != charArray[j]) {
					startsWith = false;
					break;
				}
			}
		}
		return startsWith;
	}

	public void drawImage(Graphics2D gFront, Graphics2D gRear, String content) {
		Graphics2D g = this.getSide() == CardSide.FRONT ? gFront : gRear;
		if (ProjectManager.containsImageLink(content))
			content = ProjectManager.processMediaEntry(content);
		if (ProjectManager.isBase64Image(content.trim())) {
			BufferedImage bimg = ProjectManager
					.decodeImageFromBase64(content.trim().substring(6, content.trim().lastIndexOf("</imgb>")));
			if (bimg != null) {
				int height = (int) ((((float) rect.getWidth()) / bimg.getWidth()) * bimg.getHeight());
				int width = (int) rect.getWidth();
				if (height > rect.getHeight()) {
					height = (int) rect.getHeight();
					width = (int) ((((float) rect.getHeight()) / bimg.getHeight()) * bimg.getWidth());
				}
				Image img = bimg.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
				g.drawImage(img, rect.x, rect.y, null);
			}
		}
	}

	/**
	 * Splits a given text into seperate paragraphs. Puts "-$-" at the end of every
	 * paragraph, so it can be identified as such.
	 * 
	 * @param text
	 *            Give text
	 * @param splitter
	 *            String to split the text by
	 * @return Paragraphs with a \t if indented is set to true
	 */
	private List<String> splitIntoParagraphs(String text, String splitter) {
		if (this.indented)
			text = text.replaceAll(splitter, splitter + "\t");
		List<String> paragraphsZ = Arrays.asList(text.split(splitter));
		List<String> paragraphs = new LinkedList<>();
		for (int i = 0; i < paragraphsZ.size(); i++) {
			String p = paragraphsZ.get(i) + "-$-";
			if (i == 0 && this.indented)
				p = "\t" + p;
			if (ProjectManager.containsBase64Image(p)) {
				while (ProjectManager.containsBase64Image(p)) {
					String prevP = p.substring(0, p.indexOf("<imgb>")) + "-$-";
					String imgP = p.substring(p.indexOf("<imgb>"), p.indexOf("</imgb>") + 7);
					p = "\t" + p.substring(p.indexOf("</imgb>") + 7);
					if (!prevP.isEmpty() && !prevP.equals("-$-"))
						paragraphs.add(prevP);
					paragraphs.add(imgP);
				}
				if (!p.isEmpty())
					paragraphs.add(p);
			} else
				paragraphs.add(p);
		}
		return paragraphs;
	}

	public Field clone() {
		return new Field(name, new Dimension(rect.x, rect.y), new Dimension(rect.x + rect.width, rect.y + rect.height),
				color, side, type, font, fontSize, linkedField, indented, javaScript);
	}
	
	public static enum CardSide {

		FRONT("Front"), REAR("Back");

		private String display = null;

		private CardSide(String display) {
			this.display = display;
		}

		@Override
		public String toString() {
			return display;
		}
	}

	public static enum FieldType {

		TEXT_LEFT("Text left-aligned"), TEXT_RIGHT("Text right-aligned"), TEXT_CENTER("Text centered"), TEXT_BLOCK(
				"Text block"), IMAGE("Image");

		private String display = null;

		private FieldType(String display) {
			this.display = display;
		}

		@Override
		public String toString() {
			return display;
		}
	}

	public static enum VerticalTextAlignment {

		TEXT_CENTER("Text centered"), TEXT_TOP("Text top-aligned"), TEXT_BOTTOM("Text bottom-aligned");

		private String display = null;

		private VerticalTextAlignment(String display) {
			this.display = display;
		}

		@Override
		public String toString() {
			return display;
		}
	}
}
