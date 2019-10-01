package vv3ird.populatecard.data;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import vv3ird.populatecard.CardCreator;

public class FieldStyle {
	public final Font font;	
	public final boolean italic;
	public final boolean bold;
	public final int size;
	private FieldStyle parent = null;
	private boolean notChanged = false;
	
	public FieldStyle(Field field) {
		this(CardCreator.getFont(field.getFont()), field.getFontSize(), field.isItalic(), field.isBold(), null);
	}
	
	public FieldStyle(Font font, int size, boolean italic, boolean bold, FieldStyle parent) {
		super();
		this.italic = italic;
		this.bold = bold;
		this.size = size;
		this.parent = parent;
		this.font = parent != null && parent.bold == bold && parent.italic == italic && parent.size == size ?
				font : font.deriveFont((italic ? Font.ITALIC : Font.PLAIN) | (bold ? Font.BOLD : Font.PLAIN), size);
	}
	
	public Font getFont() {
		return font;
	}
	
	public FieldStyle bold() {
		return new FieldStyle(font, size, italic, true, this);
	}
	
	public FieldStyle noBold() {
		return new FieldStyle(font, size, italic, false, this);
	}
	
	public boolean hasBoldChanged() {
		return this.parent != null && this.parent.bold != this.bold && !notChanged;
	}

	public boolean hasChangedToBold() {
		return this.parent != null && !this.parent.bold && this.bold && !notChanged;
	}

	public boolean hasChangedFromBold() {
		return this.parent != null && this.parent.bold && !this.bold && !notChanged;
	}
	
	public FieldStyle italic() {
		return new FieldStyle(font, size, true, bold, this);
	}
	
	public boolean hasItalicChanged() {
		return this.parent != null && this.parent.italic != this.italic && !notChanged;
	}

	public boolean hasChangedToItalic() {
		return this.parent != null && !this.parent.italic && this.italic && !notChanged;
	}

	public boolean hasChangedFromItalic() {
		return this.parent != null && this.parent.italic && !this.italic && !notChanged;
	}
	
	public FieldStyle noItalic() {
		return new FieldStyle(font, size, false, bold, this);
	}
	
	public FieldStyle smaller() {
		return new FieldStyle(font, size-1, italic, bold, this);
	}
	
	public FieldStyle bigger() {
		return new FieldStyle(font, size+1, italic, bold, this);
	}
	
	public boolean hasSizeChanged() {
		return this.parent != null && this.parent.size != this.size && !notChanged;
	}
	
	public FieldStyle resize(int size) {
		return new FieldStyle(font, size, italic, bold, this);
	}
	
	public FieldStyle revert() {
		return parent != null ? parent.notChanged() : this;
	}
	
	public FieldStyle clone() {
		FieldStyle f = new FieldStyle(font, size, italic, bold, parent != null ? parent.clone() : null);
		f.g = g;
		f.fm = fm;
		return f;
	}
	
	private transient Graphics2D g = null;
	
	private transient FontMetrics fm = null; 
	
	public FontMetrics getMetrics(Graphics2D g) {
		if (this.g != g) {
			this.g = g;
			this.fm = g.getFontMetrics(this.getFont());
		}
		return fm;
	}

	public FieldStyle notChanged() {
		this.notChanged = true;
		return this;
	}
}
