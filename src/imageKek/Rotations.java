package imageKek;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Rotations {
	public static void chunkRotate(BufferedImage base, BufferedImage apply, int chunkSize, int color) {
		var across = base.getWidth() / chunkSize;
		var down = base.getHeight() / chunkSize;
		
		for(var i = 0; i < across; ++i) {
			for(var j = 0; j < down; ++j) {
				// set yellow in top left corner
				base.setRGB(i * chunkSize, j * chunkSize, color);
				
				var rand = (int)(Math.random() * 4);
				
				switch(rand) {
					case 0 -> rotate0(base, apply, i * chunkSize, j * chunkSize, chunkSize);
					case 1 -> rotate90(base, apply, i * chunkSize, j * chunkSize, chunkSize);
					case 2 -> rotate180(base, apply, i * chunkSize, j * chunkSize, chunkSize);
					case 3 -> rotate270(base, apply, i * chunkSize, j * chunkSize, chunkSize);
				}
			}
		}
	}
	
	public static void chunkUnRotate(BufferedImage base, BufferedImage apply, int chunkSize, int color) {
		var across = base.getWidth() / chunkSize;
		var down = base.getHeight() / chunkSize;
		
		for(var i = 0; i < across; ++i) {
			for(var j = 0; j < down; ++j) {
				// find which corner it is in
				
				// if color is in top right
				if(base.getRGB((i + 1) * chunkSize - 1, j * chunkSize) == color) {
					rotate270(base, apply, i * chunkSize, j * chunkSize, chunkSize);
					
				// if color is in bottom left
				} else if(base.getRGB(i * chunkSize, (j + 1) * chunkSize - 1) == color) {
					rotate90(base, apply, i * chunkSize, j * chunkSize, chunkSize);
					
				// if color is in bottom right
				} else if(base.getRGB((i + 1) * chunkSize - 1, (j + 1) * chunkSize - 1) == color) {
					rotate180(base, apply, i * chunkSize, j * chunkSize, chunkSize);
					
				// if alreay in top left corner
				} else {
					
					rotate0(base, apply, i * chunkSize, j * chunkSize, chunkSize);
				}
			}
		}
	}

	public static void unRotations(BufferedImage img) throws Exception {
		var width = img.getWidth();
		var height = img.getHeight();
		
		var newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		chunkUnRotate(img, newImg, 315, 0xff0000ff);
		chunkUnRotate(newImg, img, 126, 0xff00ff00);
		chunkUnRotate(img, newImg,  63, 0xffff0000);
		chunkUnRotate(newImg, img,  45, 0xff00ffff);
		chunkUnRotate(img, newImg,  21, 0xffffff00);
		chunkUnRotate(newImg, img,   7, 0xffff00ff);
	}
	
	public static void rotations(BufferedImage img) throws Exception {
		var width = img.getWidth();
		var height = img.getHeight();
		
		var newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		chunkRotate(img, newImg,   7, 0xffff00ff);
		chunkRotate(newImg, img,  21, 0xffffff00);
		chunkRotate(img, newImg,  45, 0xff00ffff);
		chunkRotate(newImg, img,  63, 0xffff0000);
		chunkRotate(img, newImg, 126, 0xff00ff00);
		chunkRotate(newImg, img, 315, 0xff0000ff);
	}
	
	private static void rotate0(BufferedImage base, BufferedImage apply, int x, int y, int size) {
		for(var i = 0; i < size; ++i) {
			for(var j = 0; j < size; ++j) {
				apply.setRGB(i + x, j + y, base.getRGB(i + x, j + y));
			}
		}
	}
	
	private static void rotate90(BufferedImage base, BufferedImage apply, int x, int y, int size) {
		for(var i = 0; i < size; ++i) {
			for(var j = 0; j < size; ++j) {
				apply.setRGB(i + x, j + y, base.getRGB(j + x, y + size - i - 1));
			}
		}
	}
	
	private static void rotate180(BufferedImage base, BufferedImage apply, int x, int y, int size) {
		for(var i = 0; i < size; ++i) {
			for(var j = 0; j < size; ++j) {
				apply.setRGB(i + x, j + y, base.getRGB(x + size - i - 1, y + size - j - 1));
			}
		}
	}
	
	private static void rotate270(BufferedImage base, BufferedImage apply, int x, int y, int size) {
		for (var i = 0; i < size; ++i) {
			for (var j = 0; j < size; ++j) {
				apply.setRGB(i + x, j + y, base.getRGB(x + size - j - 1, i + y));
			}
		}
	}
}
