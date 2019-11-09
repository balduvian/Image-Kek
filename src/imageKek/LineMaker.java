package imageKek;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.function.IntToDoubleFunction;

public class LineMaker
{
	public static BufferedImage encodeLines(File svg) throws Exception {
		var lines = gatherLines(svg);
		
		var bounds = minMax(lines);
		
		var border = 5.7f;
		var width = (int)Math.ceil(bounds[2] - bounds[0] + 2 * border);
		var height = (int)Math.ceil(bounds[3] - bounds[1] + 2 * border);
		
		var img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		placeLines(img, lines, bounds[0], bounds[1], border);
		
		return img;
	}
	
	public static BufferedImage reconstruct(BufferedImage img) {
		var width = img.getWidth();
		var height = img.getHeight();
		
		var sheet = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		var lastPos = 0;
		var color = 0;
		
		boolean found = false;
		
		do {
			found = false;
			
			for(var i = lastPos; i < width * height; ++i) {
				var x = i % width;
				var y = i / width;
				
				if(!found) {
					// look for our first color to grab
					var tempColor = img.getRGB(x, y);
					
					// if we find our first nonwhite
					if(tempColor != 0xffffffff) {
						found = true;
						lastPos = i;
						color = tempColor;
					}
				} else {
					// look for that same color again
					if(img.getRGB(x, y) == color) {
						// draw a connecting line
						var x0 = lastPos % width;
						var y0 = lastPos / width;
						
						var x1 = i % width;
						var y1 = i / width;
						
						drawLine(sheet, x0, x1, y0, y1, color);
						
						// make sure we don't try and hit this color again
						img.setRGB(x1, y1, 0xffffffff);
						
						// make sure we don't find the same color again
						++lastPos;
						
						// goto the while loop and find the next color
						break;
					}
				}
			}
		}
		while (found);
		
		return sheet;
	}
	
	private static void drawLine(BufferedImage img, int x0, int x1, int y0, int y1, int color) {
		//put a pixel on each column if the change in x is greater than the change in y
		if (Math.abs(x1 - x0) > Math.abs(y1 - y0)) {
			var slope = ((float)y1 - y0) / ((float)x1 - x0);
		
			var sX = Math.min(x0, x1);
			var lX = Math.max(x0, x1);
			
			for (var i = sX; i <= lX; ++i) {
				var y_val = (int) Math.round(slope * (i - x0) + y0);
				img.setRGB(i, y_val, color);
			}
		
		}
		//put a pixel on each row
		else {
			var slope = ((float) x1 - x0) / ((float) y1 - y0);
			
			var sY = Math.min(y0, y1);
			var lY = Math.max(y0, y1);
			
			for (var j = sY; j <= lY; ++j) {
				var x_val = (int) Math.round(slope * (j - y0) + x0);
				img.setRGB(x_val, j, color);
			}
		}
	}
	
	private static void placeLines(BufferedImage img, Line[] lines, float left, float top, float border) {
		var amount = lines.length;
		
		var width = img.getWidth();
		var height = img.getHeight();
		
		var colorMin = 0x0000ff;
		var colorMax = 0xffff00;
		
		//paint white
		var graphics = img.createGraphics();
		graphics.setPaint(Color.WHITE);
		graphics.fillRect(0, 0, width, height);
		
		for (var i = 0; i < amount; ++i) {
			var interp = ((float)i / (amount - 1));
			var color = 0xff000000 | ((int)((colorMax - colorMin) * interp + colorMin));
			
			var line = lines[i];
			
			var x = (int)Math.round(line.x0 - left + border);
			var y = (int)Math.round(line.y0 - top + border);
			
			// move away from any pixel that has already been painted
			while(img.getRGB(x, y) != 0xffffffff) {
				if (Math.random() < 0.5) {
					--x;
					x = Math.floorMod(x, width);
				} else {
					--y;
					y = Math.floorMod(y, height);
				}
			}
			
			img.setRGB(x, y, color);
			
			//do the same for line ending coordinate
			
			x = (int)Math.round(line.x1 - left + border);
			y = (int)Math.round(line.y1 - top + border);
			
			while(img.getRGB(x, y) != 0xffffffff) {
				if (Math.random() < 0.5) {
					--x;
					x = Math.floorMod(x, width);
				} else {
					--y;
					y = Math.floorMod(y, height);
				}
			}
			
			img.setRGB(x, y, color);
		}
	}
	
	private static float[] minMax(Line[] lines) {
		var minX = Float.MAX_VALUE;
		var minY = Float.MAX_VALUE;
		var maxX = Float.MIN_VALUE;
		var maxY = Float.MIN_VALUE;
		
		for (var line : lines) {
			if(line.x0 < minX)
				minX = line.x0;
			if(line.x1 < minX)
				minX = line.x1;
			if(line.x0 > maxX)
				maxX = line.x0;
			if(line.x1 > maxX)
				maxX = line.x1;
			if(line.y0 < minY)
				minY = line.y0;
			if(line.y1 < minY)
				minY = line.y1;
			if(line.y0 > maxY)
				maxY = line.y0;
			if(line.y1 > maxY)
				maxY = line.y1;
		}
		
		return new float[] {minX,minY,maxX,maxY};
	}
	
	private static class Line {
		interface PutIn {
			void put(float v);
		}
		
		public float x0, y0, x1, y1;
		
		public PutIn[] putters = {
			(v) -> x0 = v,
			(v) -> y0 = v,
			(v) -> x1 = v,
			(v) -> y1 = v
		};
		
		public void print() {
			System.out.println("line<" + x0 + ", " + y0 + " " + x1 + ", " + y1 + ">");
		}
	}
	
	private static final String lineName = "line";
	private static final String[] valueNames = {
		"x1", "y1", "x2", "y2"
	};
	
	private static Line[] gatherLines(File svg) throws Exception {
		// we don't know how many lines we'll need
		var lineList = new ArrayList<Line>();
		
		// we read the svg at path
		var reader = new FileReader(svg);
		
		// how far into a value we are looking for
		int lookingCounter = 0;
		// if we are inside a line statement
		boolean inLine = false;
		// which value of the line we are looking for
		int valueLooking = 0;
		// if we are reading a line value
		boolean inValue = false;
		// at the number part
		boolean ininValue = false;
		
		//
		String tempValue = "";
		Line tempLine = new Line();
		
		// read through the whole file byte by byte
		int current;
		while((current = reader.read()) != -1) {
			if(inLine) {
				if(inValue) {
					if(ininValue) {
						if(current == '"') {
							// put value in temp line
							tempLine.putters[valueLooking].put(Float.parseFloat(tempValue));
							
							// advance line value
							inValue = false;
							++valueLooking;
							
							// if we are done with this line
							if(valueLooking == valueNames.length)
							{
								// put value in string
								lineList.add(tempLine);
								inLine = false;
								lookingCounter = 0;
							}
						} else {
							// add this number bit to string
							tempValue += (char)current;
						}
					} else {
						if(current == '"') {
							ininValue = true;
						}
					}
				} else {
					if(current == valueNames[valueLooking].charAt(lookingCounter)) {
						++lookingCounter;
						if(lookingCounter == valueNames[valueLooking].length()) {
							// we are now in a value
							inValue = true;
							ininValue = false;
							tempValue = "";
							lookingCounter = 0;
						}
					} else {
						lookingCounter = 0;
					}
				}
			} else {
				if(current == lineName.charAt(lookingCounter)) {
					++lookingCounter;
					// if we have read every character we are in the line now
					if(lookingCounter == lineName.length()) {
						// prepare default value
						inLine = true;
						valueLooking = 0;
						inValue = false;
						lookingCounter = 0;
						
						// the line we will be using
						tempLine = new Line();
					}
				} else {
					lookingCounter = 0;
				}
			}
		}
		
		reader.close();
		
		// get the array from the list
		Line[] ret = new Line[lineList.size()];
		lineList.toArray(ret);
		
		return ret;
	}
}
