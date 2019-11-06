package imageKek;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;

public class ImageKek {
	
	public static void main(String[] args) {
		new ImageKek();
	}
	
	public ImageKek() {
		try {
			//imageWithinRoutine();
			//windingRoutine();
			//rotationsRoutine();
			//encodeBitsRoutine("yallready know");
			shredRoutine();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void imageWithinRoutine() throws Exception {
		var img = ImageIO.read(new File("img/test2.png"));
		var img2 = ImageIO.read(new File("img/test3.png"));
		
		ImageWithin.imageWithin(img, img2);
		
		var img3 = ImageWithin.revealImage(img, 116, 116);
		
		ImageIO.write(img, "png", new File("process/container.png"));
		ImageIO.write(img3, "png", new File("process/revealed.png"));
	}
	
	private void windingRoutine() throws Exception {
		var img = ImageIO.read(new File("img/test2.png"));
		
		img = Windings.spiral(img, true);
		img = Windings.wind(img, true);
		
		ImageIO.write(img, "png", new File("process/midway_wound.png"));
		
		img = Windings.wind(img, false);
		img = Windings.spiral(img, false);
		
		ImageIO.write(img, "png", new File("process/unwound.png"));
	}
	
	private void rotationsRoutine() throws Exception {
		var img = ImageIO.read(new File("img/test0.png"));
		
		Rotations.rotations(img);
		
		ImageIO.write(img, "png", new File("process/rotated.png"));
		
		Rotations.unRotations(img);
		
		ImageIO.write(img, "png", new File("process/unrotated.png"));
	}
	
	private void encodeBitsRoutine(String msg) throws Exception {
		var img = ImageIO.read(new File("img/test0.png"));
		
		EncodeBits.encode(img, msg.toCharArray(), 34);
		
		ImageIO.write(img, "png", new File("process/encoded_bits.png"));
		
		var message = new ArrayList<Integer>();
		
		EncodeBits.decode(img, message, 34);
		
		for(var i = 0; i < msg.length(); ++i) {
			System.out.print((char)(int)message.get(i));
		}
	}
	
	private void shredRoutine() throws Exception {
		var img = ImageIO.read(new File("img/test2.png"));
		
		DoubleShred.shred(img);
		
		ImageIO.write(img, "png", new File("process/shredded.png"));
		
		DoubleShred.unShred(img);
		
		ImageIO.write(img, "png", new File("process/unshredded.png"));
	}
}
