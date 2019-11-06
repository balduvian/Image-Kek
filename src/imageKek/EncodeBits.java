import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class EncodeBits {
	
	public static void main(String[] args) {
		new EncodeBits();
	}
	
	public EncodeBits() {
		try {
			var msg = "have you seen the video of the guy who used autopilot";
			
			encode("comp.png", msg.toCharArray(), 34, "bits.png");
			
			var message = new int[msg.length()];
			
			decode("bits.png", message, 34);
			
			for(var i = 0; i < msg.length(); ++i) {
				System.out.print((char)message[i]);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void encode(String in, char[] bytes, int stride, String out) throws Exception {
		var img = ImageIO.read(new File(in));
		
		var width = img.getWidth();
		var height = img.getHeight();
		
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
			
			//System.out.println("smbt: " + smallBit);
			//System.out.println("smb2: " + (rgb & 1));
			
		    img.setRGB(x, y, rgb);
		}
		
		ImageIO.write(img, "png", new File(out));
	}
	
	public void decode(String in, int[] writeTo, int stride) throws Exception {
		var img = ImageIO.read(new File(in));
		
		var width = img.getWidth();
		var height = img.getHeight();
		
		int length = writeTo.length * 8;
		
		for(var i = 0; i < length; ++i) {
			var x = (i * stride) % width;
			var y = (i * stride) / width;
			
			var bigByteIndex = i / 8;
			var smallBitIndex = 7 - (i % 8);
			
			var rgb = img.getRGB(x, y);
			
			rgb = rgb & 1;
			
			//System.out.println(rgb);
			
			writeTo[bigByteIndex] |= (rgb << smallBitIndex);
		}
	}
}
