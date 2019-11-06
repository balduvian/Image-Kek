package imageKek;

import javax.imageio.ImageIO;
import java.io.File;

public class ImageKek {
	
	public static void main(String[] args) {
		new ImageKek();
	}
	
	public ImageKek() {
		try {
			imageWithinRoutine();
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
	
}
