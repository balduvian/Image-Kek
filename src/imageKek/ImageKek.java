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
			//imageWithinDecode("C:\\Users\\Emmet\\PQ\\hana_hunt\\testing\\koki beach.png");
			//imageWithinRoutine("C:\\Users\\Emmet\\PQ\\hana_hunt\\Koki Beach.png", "C:\\Users\\Emmet\\PQ\\hana_hunt\\hidden.png");
			//windingRoutine("C:\\Users\\Emmet\\PQ\\hana_hunt\\hana-town.png");
			//jpgWind("C:\\Users\\Emmet\\PQ\\hana_hunt\\black_sand_wind.png");
			//unwind("C:\\Users\\Emmet\\PQ\\hana_hunt\\hana_spiral.png");
			//rotationsRoutine("C:\\Users\\Emmet\\PQ\\hana_hunt\\red_sand_beach.png");
			//unRotate("C:\\Users\\Emmet\\PQ\\hana_hunt\\rotated_red_sand_beach.png");
			encodeBitsRoutine("C:\\Users\\Emmet\\PQ\\hana_hunt\\testing\\second.png", "ss");
			//shredRoutine("C:\\Users\\Emmet\\PQ\\hana_hunt\\testing\\first.png");
			//linesRoutine("C:\\Users\\Emmet\\PQ\\hana_hunt\\challenge.svg");
			//triRoutine("C:\\Users\\Emmet\\PQ\\hana_hunt\\7 pools final.png");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void imageWithinRoutine(String path0, String path1) throws Exception {
		var img = ImageIO.read(new File(path0));
		var img2 = ImageIO.read(new File(path1));
		
		ImageWithin.imageWithin(img, img2);
		
		var img3 = ImageWithin.revealImage(img, 707, 707);
		
		ImageIO.write(img, "png", new File("process/container.png"));
		ImageIO.write(img3, "png", new File("process/revealed.png"));
	}
	
	private void imageWithinDecode(String path) throws Exception {
		var img = ImageIO.read(new File(path));
		
		var img0 = ImageWithin.revealImage(img, 707, 707);
		
		ImageIO.write(img0, "png", new File("process/decode_revealed.png"));
	}
	
	private void windingRoutine(String path) throws Exception {
		var img = ImageIO.read(new File(path));
		
		img = Windings.spiral(img, true);
		
		ImageIO.write(img, "png", new File("process/midway_wound.png"));
		
		img = Windings.spiral(img, false);
		
		ImageIO.write(img, "png", new File("process/unwound.png"));
	}
	
	private void unwind(String path) throws Exception {
		var img = ImageIO.read(new File(path));
		
		img = Windings.spiral(img, false);
		
		ImageIO.write(img, "png", new File("process/un_unwound.png"));
	}
	
	private void jpgWind(String path) throws Exception {
		var img = ImageIO.read(new File(path));
		
		//img = Windings.wind(img, true);
		
		//ImageIO.write(img, "png", new File("process/midway_jpg.png"));
		
		img = Windings.wind(img, false);
		
		ImageIO.write(img, "png", new File("process/un_unjpg.png"));
	}
	
	private void rotationsRoutine(String path) throws Exception {
		var img = ImageIO.read(new File(path));
		
		Rotations.rotations(img);
		
		ImageIO.write(img, "png", new File("process/rotated.png"));
		
		Rotations.unRotations(img);
		
		ImageIO.write(img, "png", new File("process/unrotated.png"));
	}
	
	private void unRotate(String path) throws Exception {
		var img = ImageIO.read(new File(path));
		
		Rotations.unRotations(img);
		
		ImageIO.write(img, "png", new File("process/unrotated.png"));
	}
	
	private void encodeBitsRoutine(String path, String msg) throws Exception {
		var img = ImageIO.read(new File(path));
		
		//EncodeBits.encode(img, msg.toCharArray(), 34);
		
		//ImageIO.write(img, "png", new File("process/encoded_bits.png"));
		
		var message = new ArrayList<Integer>();
		
		EncodeBits.decode(img, message, 34);
		
		for(var i = 0; i < message.size(); ++i)
			System.out.print((char)(int)message.get(i));
	}
	
	private void shredRoutine(String path) throws Exception {
		var img = ImageIO.read(new File(path));
		
		//DoubleShred.shred(img);
		
		//ImageIO.write(img, "png", new File("process/shredded.png"));
		
		DoubleShred.unShred(img);
		
		ImageIO.write(img, "png", new File("process/unshredded.png"));
	}
	
	private void linesRoutine(String path) throws Exception {
		var svg = new File(path);
		
		var img = LineMaker.encodeLines(svg);
		
		ImageIO.write(img, "png", new File("process/dots.png"));
		
		img = LineMaker.reconstruct(img);
		
		ImageIO.write(img, "png", new File("process/lines.png"));
	}
	
	private void triRoutine(String path) throws Exception {
		var img = ImageIO.read(new File(path));
		
		var tri = TRI2.encodeImage(img, TRI2.defaultEncoder, new File("process/encoded.tri"));
		
		var decoded = TRI2.decodeFile(tri);
		
		ImageIO.write(decoded, "png", new File("process/decoded_tri.png"));
	}
}
