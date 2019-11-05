package imageKek;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class JpgWind {

	public static void main(String[] args) {
		new JpgWind();
	}

	public JpgWind() {
		try { 
		
			var img = ImageIO.read(new File("test2.png"));
			var img2 = ImageIO.read(new File("hidden.jpg"));
			
			ImageWithin.imageWithin(img, img2);
			
			var hidden = ImageWithin.revealImage(img, 116, 116);
			
			ImageIO.write(img, "png", new File("layered.png"));
			ImageIO.write(hidden, "png", new File("revealed.png"));
			/*
			img = spiral(img, true);
			img = wind(img, true);
			img = spiral(img, true);
			img = wind(img, true);
			img = spiral(img, true);
			img = wind(img, true);
			img = spiral(img, true);
			img = wind(img, true);
			
			ImageIO.write(img, "png", new File("arranged.png"));
			
			img = wind(img, false);
			img = spiral(img, false);
			img = wind(img, false);
			img = spiral(img, false);
			img = wind(img, false);
			img = spiral(img, false);
			img = wind(img, false);
			img = spiral(img, false);
			
			ImageIO.write(img, "png", new File("unarranged.png"));
			*/
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static boolean UP_RIGHT = true;
	private static boolean DOWN_LEFT = false;
	
	public BufferedImage wind(BufferedImage img, boolean forward) {
		boolean direction = UP_RIGHT;
		
		var width = img.getWidth();
		var height = img.getHeight();
		
		var sheet = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		var total = width * height;
		
		// the winding coords from original image
		var wX = 0;
		var wY = 0;
		
		for(var i = 0; i < total; ++i) {
			// the regular row order coords to apply to
			var nX = i % width;
			var nY = i / width;
			
			if(forward)
				sheet.setRGB(wX, wY, img.getRGB(nX, nY));				
			else 
				sheet.setRGB(nX, nY, img.getRGB(wX, wY));
			
			// now wind the winding coords
			if(direction == UP_RIGHT) {
				++wX;
				--wY;
				
				if(wX >= width) {
					wX = width - 1;
					wY += 2;
					direction = DOWN_LEFT;
				}
				
				if(wY < 0) {
					wY = 0;
					direction = DOWN_LEFT;
				}
				
			// if the direction is down left
			} else {
				--wX;
				++wY;
				
				if(wX < 0) {
					wX = 0;
					direction = UP_RIGHT;
				}
				
				if(wY >= height) {
					wY = height - 1;
					wX += 2;
					direction = UP_RIGHT;
				}
			}
		}
		
		return sheet;
	}
	
	private static final int RIGHT = 0;
	private static final int DOWN = 1;
	private static final int LEFT = 2;
	private static final int UP = 3;
	
	public BufferedImage spiral(BufferedImage img, boolean forward) {
		var direction = 0;
		
		var width = img.getWidth();
		var height = img.getHeight();
		
		var rightBound = width;
		var downBound = height;
		var leftBound = -1;
		var upBound = 0;
		
		var sheet = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		var total = width * height;
		
		// spiral coords we will go to
		var sX = 0;
		var sY = 0;
		
		for(var i = 0; i < total; ++i) {
			// the regular row order coords to apply to
			var nX = i % width;
			var nY = i / width;
			
			if(forward)
				sheet.setRGB(sX, sY, img.getRGB(nX, nY));				
			else 
				sheet.setRGB(nX, nY, img.getRGB(sX, sY));
			
			switch(direction) {
				case RIGHT -> {
					++sX;
					if(sX == rightBound) {
						--sX;
						++sY;
						rightBound = sX;
						direction = DOWN;
					}
				}
				case DOWN -> {
					++sY;
					if(sY == downBound) {
						--sY;
						--sX;
						downBound = sY;
						direction = LEFT;
					}
				}
				case LEFT -> {
					--sX;
					if(sX == leftBound) {
						++sX;
						--sY;
						leftBound = sX;
						direction = UP;
					}
				}
				case UP -> {
					--sY;
					if (sY == upBound) {
						++sY;
						++sX;
						upBound = sY;
						direction = RIGHT;
					}
				}
			}
		}
		
		return sheet;
	}
	
}
