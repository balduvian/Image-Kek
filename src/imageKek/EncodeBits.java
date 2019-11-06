package imageKek;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class EncodeBits {
	public static void encode(BufferedImage img, char[] bytes, int stride) throws Exception {
		var width = img.getWidth();
		var height = img.getHeight();
		
		if(bytes.length > (width * height) / (stride * 8))
			throw new Exception("image not large enough to write this message");
		
		int length = (bytes.length + 1) * 8;
		
		for(var i = 0; i < length; ++i) {
			var x = (i * stride) % width;
			var y = (i * stride) / width;
			
			var smallBit = 0;
			
			if(i < length - 8) {
				var bigByte = bytes[i / 8];
				smallBit = (bigByte >> (7 - (i % 8))) & 1;
			}
			
			var rgb = img.getRGB(x, y);
			
			rgb = smallBit | (rgb & 0xfffffffe);
			
		    img.setRGB(x, y, rgb);
		}
	}
	
	public static void decode(BufferedImage img, ArrayList<Integer> writeTo, int stride) throws Exception {
		var width = img.getWidth();

		var i = 0;
		
		while(true) {
			var x = (i * stride) % width;
			var y = (i * stride) / width;
			
			var bigByteIndex = i / 8;
			var smallBitIndex = 7 - (i % 8);
			
			// if we need to add a byte
			if(writeTo.size() == bigByteIndex)
				writeTo.add(0);
			
			// write in the next bit
			var writeToRGB = writeTo.get(bigByteIndex);
			
			writeToRGB |= ((img.getRGB(x, y) & 1) << smallBitIndex);
			
			writeTo.set(bigByteIndex, writeToRGB);
			
			// when we hit the null terminator
			if(smallBitIndex == 0 && writeToRGB == 0) {
				break;
			}
			
			++i;
		}
	}
}
