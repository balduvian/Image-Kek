package imageKek;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class DoubleShred {
	public static void shred(BufferedImage img) throws Exception {		
		var width = img.getWidth();
		var height = img.getHeight();
		
		/* insert guide pixels */
		
		// purple in corner
		img.setRGB(0, 0, 0xffff00ff);
		
		// red on top
		for(var i = 1; i < width; ++i) {
			img.setRGB(i, 0, 0xffff0000);
		}
		
		// blue on side
		for(var j = 1; j < height; ++j) {
			img.setRGB(0, j, 0xff0000ff);
		}
		
		var newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		// shred colums
		for(var i = 0; i < width; ++i) {
			var offset = (int)(Math.random() * height * 2 - height);
			offset = Math.floorMod(offset, height);
			
			for(var j = 0; j < height; ++j) {
				newImg.setRGB(i, j, img.getRGB(i, offset));
				
				++offset;
				
				if(offset == height)
					offset = 0;
			}
		}
		
		//shred rows
		for(var j = 0; j < height; ++j) {
			var offset = (int)(Math.random() * width * 2 - width);
			offset = Math.floorMod(offset, width);
			
			for(var i = 0; i < width; ++i) {
				img.setRGB(i, j, newImg.getRGB(offset, j));
				
				++offset;
				
				if(offset == width)
					offset = 0;
			}
		}
	}
	
	public static void unShred(BufferedImage img) throws Exception {
		var width = img.getWidth();
		var height = img.getHeight();
		
		var newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		// unshred rows
		for(var j = 0; j < height; ++j) {
			var foundOffset = 0;
			
			// find the blue pixel
			for(var i = 0; i < width; ++i) {
				var rgb = img.getRGB(i, j);
				
				if(rgb == 0xffff00ff || rgb == 0xff0000ff) {
					foundOffset = i;
					break;
				}
			}
			
			// unshred this row
			for(var i = 0; i < width; ++i) {
				newImg.setRGB(i, j, img.getRGB(foundOffset, j));
				
				++foundOffset;
				if(foundOffset == width)
					foundOffset = 0;
			}
		}
		
		// unshred columns
		for(var i = 0; i < width; ++i) {
			var foundOffset = 0;
			
			// find the blue pixel
			for(var j = 0; j < height; ++j) {
				var rgb = newImg.getRGB(i, j);
				
				if(rgb == 0xffff00ff || rgb == 0xffff0000) {
					foundOffset = j;
					break;
				}
			}
			
			// unshred this row
			for(var j = 0; j < height; ++j) {
				img.setRGB(i, j, newImg.getRGB(i, foundOffset));
				
				++foundOffset;
				if(foundOffset == height)
					foundOffset = 0;
			}
		}
	}
}
