package vv3ird.populatecard.control.postprocessing;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import vv3ird.populatecard.gui.StatusListener;

public class ReplaceImage implements Runnable {
	
	private Path root = null;
	
	private BufferedImage target = null;
	
	private BufferedImage replacement = null;
	
	private StatusListener listener = null;

	public ReplaceImage(Path root, BufferedImage target, BufferedImage replacement, StatusListener listener) {
		super();
		this.root = root;
		this.target = target;
		this.replacement = replacement;
		this.listener = listener;
	}

	public  void run() {
		try {
			Files.list(root).forEach(f -> checkImage(f, target, replacement));
			listener.setText("Done replaceing images");
		} catch (IOException e) {
			listener.setText("An error occured while replaceing images: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void checkImage(Path f, BufferedImage empty, BufferedImage repalcement) {
		System.out.println("Testing image: " + f.toString());
		try {
			BufferedImage d = ImageIO.read(f.toFile());
			boolean same = d.getWidth() == empty.getWidth() && d.getHeight() == empty.getHeight();
			if(same) {
				
				int width = d.getWidth();
				int height = d.getHeight();
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						if (d.getRGB(x, y) != empty.getRGB(x, y)) {
							if(f.toString().contains("rear")) {
								System.out.println("File is different");
								System.out.println("("+x+", "+ y + ") Searched: " + d.getRGB(x, y) + ", Target: " + empty.getRGB(x, y));
								System.out.println();
							}
							return;
						}
					}
				}
				System.out.println("Replace file " + f.toString());
				ImageIO.write(repalcement, f.toString().substring(f.toString().lastIndexOf(".")+1), f.toFile());
				listener.setText("Image \"" + f.getFileName() + "\" replaced");
				System.out.println();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
