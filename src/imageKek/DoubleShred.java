import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class DoubleShred {
	public static void main(String[] args) {
		try {
			new DoubleShred();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public DoubleShred() throws Exception {
		superRotate("comp.png", "midway.png");
		unRotate("midway.png", "normal.png");
		//unShred("dubl.png");
	}
	
	public void shred(String path) throws Exception {
		var img = ImageIO.read(new File(path));
		
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
		
		ImageIO.write(img, "PNG", new File("dubl.png"));
	}
	
	public void unShred(String path) throws Exception {
		var img = ImageIO.read(new File(path));
		
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
		
		ImageIO.write(img, "PNG", new File("reverb.png"));
	}
	
	public void chunkRotate(BufferedImage base, BufferedImage apply, int chunkSize, int color) {
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
	
	public void chunkUnRotate(BufferedImage base, BufferedImage apply, int chunkSize, int color) {
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
	
	public void unRotate(String path, String out) throws Exception {
		var img = ImageIO.read(new File(path));
		
		var width = img.getWidth();
		var height = img.getHeight();
		
		var newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		chunkUnRotate(img, newImg, 315, 0xff0000ff);
		chunkUnRotate(newImg, img, 126, 0xff00ff00);
		chunkUnRotate(img, newImg,  63, 0xffff0000);
		chunkUnRotate(newImg, img,  45, 0xff00ffff);
		chunkUnRotate(img, newImg,  21, 0xffffff00);
		chunkUnRotate(newImg, img,   7, 0xffff00ff);
		
		ImageIO.write(img, "PNG", new File(out));
	}
	
	public void superRotate(String path, String out) throws Exception {
		var img = ImageIO.read(new File(path));
		
		var width = img.getWidth();
		var height = img.getHeight();
		
		var newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		chunkRotate(img, newImg,   7, 0xffff00ff);
		chunkRotate(newImg, img,  21, 0xffffff00);
		chunkRotate(img, newImg,  45, 0xff00ffff);
		chunkRotate(newImg, img,  63, 0xffff0000);
		chunkRotate(img, newImg, 126, 0xff00ff00);
		chunkRotate(newImg, img, 315, 0xff0000ff);
		
		ImageIO.write(img, "PNG", new File(out));
	}
	
	public void rotate0(BufferedImage base, BufferedImage apply, int x, int y, int size) {
		for(var i = 0; i < size; ++i) {
			for(var j = 0; j < size; ++j) {
				apply.setRGB(i + x, j + y, base.getRGB(i + x, j + y));
			}
		}
	}
	
	public void rotate90(BufferedImage base, BufferedImage apply, int x, int y, int size) {
		for(var i = 0; i < size; ++i) {
			for(var j = 0; j < size; ++j) {
				apply.setRGB(i + x, j + y, base.getRGB(j + x, y + size - i - 1));
			}
		}
	}
	
	public void rotate180(BufferedImage base, BufferedImage apply, int x, int y, int size) {
		for(var i = 0; i < size; ++i) {
			for(var j = 0; j < size; ++j) {
				apply.setRGB(i + x, j + y, base.getRGB(x + size - i - 1, y + size - j - 1));
			}
		}
	}
	
	public void rotate270(BufferedImage base, BufferedImage apply, int x, int y, int size) {
		for (var i = 0; i < size; ++i) {
			for (var j = 0; j < size; ++j) {
				apply.setRGB(i + x, j + y, base.getRGB(x + size - j - 1, i + y));
			}
		}
	}
}
