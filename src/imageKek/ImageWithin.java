package imageKek;

import java.awt.image.BufferedImage;

public class ImageWithin {
	
	/**
	 * places the image inside within main
	 */
	public static void imageWithin(BufferedImage main, BufferedImage inside) throws Exception {
		var width = main.getWidth();
		var height = main.getHeight();
		
		//var sheet = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		var inWidth = inside.getWidth();
		var inHeight = inside.getHeight();
		
		var bitDepth = 8 * 3;
		
		var inTotal = inWidth * inHeight * bitDepth;
		
		if(width * height < inTotal)
			throw new Exception("containing image is not big enough!");
		
		var insideIndex = 0;
		var insideRGB = inside.getRGB(0, 0);
		
		var bitIndex = 0;
		
		for(var i = 0; i < inTotal; ++i) {
			var x = i % width;
			var y = i / width;
			
			var rgb = main.getRGB(x, y);
			
			rgb &= 0xfffffffe;
			rgb |= (insideRGB >> bitIndex) & 1;
					
			main.setRGB(x, y, rgb);
			
			++bitIndex;
			if(i != inTotal - 1 && bitIndex == bitDepth) {
				bitIndex = 0;
				++insideIndex;
				insideRGB = inside.getRGB(insideIndex % inWidth, insideIndex / inWidth);
			}	
		}
		
	}
	
	public static BufferedImage revealImage(BufferedImage layered, int width, int height) {
		var layeredWidth = layered.getWidth();
		var layeredHeight = layered.getHeight();
		
		var sheet = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		var bitDepth = 8 * 3;
		
		var total = width * height * bitDepth;
		
		var currentRGB = 0xff000000;
		var currentIndex = 0;
		
		var bitIndex = 0;
		
		for(var i = 0; i < total; ++i) {
			var layeredRGB = layered.getRGB(i % layeredWidth, i / layeredWidth);
			
			currentRGB |= ((layeredRGB & 1) << bitIndex);
			
			++bitIndex;
			if(i != total - 1 && bitIndex == bitDepth) {
				// place this color in sheet
				sheet.setRGB(currentIndex % width, currentIndex / width, currentRGB);
				
				// then reset indices
				bitIndex = 0;
				++currentIndex;
				currentRGB = 0xff000000;
			}
		}
		
		return sheet;
	}
	
}
