import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class TRI2
{
	public static final int HEADING = 187;
	
	public static void main(String[] args) {
		new TRI2();
	}
	
	public TRI2() {
		try {
			var baseEncoder = new Encoder(new int[] {
				0, 0, 2, 3,
				2, 0, 3, 2,
				5, 0, 3, 2,
				0, 3, 2, 3,
				2, 2, 2, 4,
				4, 2, 4, 2,
				0, 6, 4, 2,
				4, 4, 2, 4,
				6, 4, 2, 4
			});
			//encodeFile("moonsmooth.png", baseEncoder);
			
			//printPatternTable("tri.tri");
			
			//decodeFile("tri.tri");
			
			System.out.println(getPatternCount("tri.tri", 1));
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static class Encoder {
		int[] dims;
		
		public Encoder(int[]  dims) {
			this.dims = dims;
		}
	}
	
	public void encodeFile(String path, Encoder encoder) throws Exception {
		BufferedImage img = ImageIO.read(new File(path));
		var width = img.getWidth();
		var height = img.getHeight();
		
		var pWidth = width / 8;
		var pHeight = height / 8;
		
		var sheet = new boolean[width][height];
		
		var holdCount = 1 << 8;
		var holding = new int[8][8][holdCount];
		var holdTotals = new int[holdCount];
		
		var holdPattern = new int[pWidth][pHeight];
		
		for(var j = 0; j < pHeight; ++j) {
			for(var i = 0; i < pWidth; ++i) {
				var colors = new int[8][8][3];
				
				// we need to find the average of this 8 x 8 chunk
				var avgR = 0.f;
				var avgG = 0.f;
				var avgB = 0.f;
				
				// loop through the chunk
				for(var k = 0; k < 8; ++k) {
					for (var l = 0; l < 8; ++l) {
						var color = img.getRGB(i * 8 + l, j * 8 + k);
						var r = (color >> 16) & 0xff;
						var g = (color >>  8) & 0xff;
						var b = (color      ) & 0xff;
						
						colors[l][k][0] = r;
						colors[l][k][1] = g;
						colors[l][k][2] = b;
						
						avgR += r;
						avgG += g;
						avgB += b;
					}
				}
				
				avgR /= 64;
				avgG /= 64;
				avgB /= 64;
				
				var values = new boolean[8][8];
				
				// loop through again for difference values
				for(var k = 0; k < 8; ++k) {
					for (var l = 0; l < 8; ++l) {
						var r = colors[l][k][0] > avgR;
						var g = colors[l][k][1] > avgG;
						var b = colors[l][k][2] > avgB;
						
						var c = false;
						
						if (r) {
							if(g) {
								c = true;
							} else if(!b) {
								c = true;
							}
						} else if(g && !b) {
							c = true;
						}
						
						values[l][k] = c;
						sheet[i * 8 + l][j * 8 + k] = c;
					}
				}
				
				// the encoder sections
				var sortPosition = 0;
				var numSections = encoder.dims.length / 4;
				if(numSections > 9)
					throw new Exception("too many sections in encoder");
				
				for(var s = 0; s < numSections; ++s) {
					// get this encoder box
					var left = encoder.dims[s * 4];
					var top = encoder.dims[s * 4 + 1];
					var wi = encoder.dims[s * 4 + 2];
					var hi = encoder.dims[s * 4 + 3];
					
					var count = 0;
					
					// loop over this box
					for(var k = top; k < top + hi; ++k) {
						for (var l = left; l < left + wi; ++l) {
							// collect its value
							if (values[l][k]) ++count;
						}
					}
					
					// fill in the binary representation
					// from the left to the right
					// at max will be a 9 bit binary number
					if (count > (wi * hi) / 2) {
						sortPosition |= 1 << (numSections - s - 1);
					}
				}
				
				// now we may want to invert if we want to put it back
				// in range
				// flip the top half of number into the bottom half
				var range = (1 << numSections);
				var halfRange = (1 << (numSections - 1));
				
				var invert = false;
				
				if(sortPosition >= halfRange) {
					sortPosition = range - sortPosition - 1;
					invert = true;
				}
				
				// now add this chunk to the holding
				for(var k = 0; k < 8; ++k) {
					for (var l = 0; l < 8; ++l) {
						var x = i * 8 + l;
						var y = j * 8 + k;
						
						holding[l][k][sortPosition] += invert ? (sheet[x][y]?0:1) : (sheet[x][y]?1:0);
					}
				}
				
				// increase the count for this hold
				++holdTotals[sortPosition];
				
				holdPattern[i][j] = sortPosition;
			}
		}
		
		// now go through each hold and find the average
		for(var h = 0; h < holdCount; ++h) {
			var halfTotal = holdTotals[h] / 2.f;
			
			// compress the pattern into 1 and 0
			for(var j = 0; j < 8; ++j) {
				for(var i = 0; i < 8; ++i) {
					holding[i][j][h] = (holding[i][j][h] > halfTotal) ? 1 : 0;
				}
			}
		}
		
		// now start writing the file
		var write = new FileOutputStream("tri.tri");
		
		// put in header
		write.write(HEADING);
		write.write('T');
		write.write('R');
		write.write('I');
		
		//write version
		write.write(2);
		
		// width in byte sections
		write.write((width >> 24) & 0xff);
		write.write((width >> 16) & 0xff);
		write.write((width >>  8) & 0xff);
		write.write((width      ) & 0xff);
		
		write.write((height >> 24) & 0xff);
		write.write((height >> 16) & 0xff);
		write.write((height >>  8) & 0xff);
		write.write((height      ) & 0xff);
		
		// write color bytes byte
		write.write(3);
		
		// write pattern table
		// ignore the first pattern
		for (var i = 1; i < 256; ++i) {
			// each row is a byte
			for(var r = 0; r < 8; ++r) {
				var row = 0;
				for(var s = 0; s < 8; ++s) {
					row |= (holding[s][r][i] << (7 - s));
				}
				write.write(row);
			}
		}
		
		//now write in chunk pattern and color data
		for(var j = 0; j < pHeight; ++j) {
			for(var i = 0; i < pWidth; ++i) {
				var pattern = holdPattern[i][j];
				
				// we already found the pattern
				write.write(pattern);
				
				if(pattern == 0) {
					// for if we only have one color
					var avgR = 0.f;
					var avgG = 0.f;
					var avgB = 0.f;
					
					for(var k = 0; k < 8; ++k) {
						for (var l = 0; l < 8; ++l) {
							var color = img.getRGB(i * 8 + l, j * 8 + k);
							avgR += (color >> 16) & 0xff;
							avgG += (color >>  8) & 0xff;
							avgB += (color      ) & 0xff;
						}
					}
					
					write.write(Math.round(avgR / 64) & 0xff);
					write.write(Math.round(avgG / 64) & 0xff);
					write.write(Math.round(avgB / 64) & 0xff);
				} else {
					// now find the two average colors
					var avgR0 = 0.f;
					var avgG0 = 0.f;
					var avgB0 = 0.f;
					var total0 = 0;
					
					var avgR1 = 0.f;
					var avgG1 = 0.f;
					var avgB1 = 0.f;
					var total1 = 0;
					
					for(var k = 0; k < 8; ++k) {
						for (var l = 0; l < 8; ++l) {
							var color = img.getRGB(i * 8 + l, j * 8 + k);
							var r = (color >> 16) & 0xff;
							var g = (color >>  8) & 0xff;
							var b = (color      ) & 0xff;
							
							if(holding[l][k][pattern] == 0) {
								++total0;
								avgR0 += r;
								avgG0 += g;
								avgB0 += b;
							} else {
								++total1;
								avgR1 += r;
								avgG1 += g;
								avgB1 += b;
							}
						}
					}
					
					avgR0 /= total0;
					avgG0 /= total0;
					avgB0 /= total0;
					avgR1 /= total1;
					avgG1 /= total1;
					avgB1 /= total1;
					
					// write the 3 bytes of color 0 R, G, B
					write.write(Math.round(avgR0) & 0xff);
					write.write(Math.round(avgG0) & 0xff);
					write.write(Math.round(avgB0) & 0xff);
					
					// then for color 1
					write.write(Math.round(avgR1) & 0xff);
					write.write(Math.round(avgG1) & 0xff);
					write.write(Math.round(avgB1) & 0xff);
				}
			}
		}
		
		// finish
		write.close();
		
		//ImageIO.write(img, "PNG", new File("skak.png"));
	}
	
	public int getPatternCount(String path, int find) throws Exception {
		var read = new FileInputStream(path);
		
		if(read.read() != HEADING)
			throw new Exception("not a TRI file");
		
		read.read(); // T
		read.read(); // R
		read.read(); // I
		
		var version = read.read();
		
		var width0 = read.read();
		var width1 = read.read();
		var width2 = read.read();
		var width3 = read.read();
		
		var width = (width0 << 24) | (width1 << 16) | (width2 << 8) | width3;
		
		var height0 = read.read();
		var height1 = read.read();
		var height2 = read.read();
		var height3 = read.read();
		
		var height = (height0 << 24) | (height1 << 16) | (height2 << 8) | height3;
		
		//read the color byte
		read.read();
		
		
		// now read patterns table
		// ignore number one
		for(var i = 1; i < 256; ++i) {
			for(var r = 0; r < 8; ++r) {
				var row = read.read();
			}
		}
		
		var pWidth = width / 8;
		var pHeight = height / 8;
		
		//what we output
		var count = 0;
		
		// now read the image
		for(var j = 0; j < pHeight; ++j) {
			for(var i = 0; i < pWidth; ++i) {
				var pattern = read.read();
				
				if(pattern == find)
					++count;
	
				if(pattern == 0) {
					read.skip(3);
				} else {
					read.skip(6);
				}
			}
		}
		
		return count;
	}
	
	public void printPatternTable(String path) throws Exception {
		var read = new FileInputStream(path);
		
		read.read(); // heading
		
		read.read(); // T
		read.read(); // R
		read.read(); // I
		
		read.read(); // version
		
		read.read(); // wdith
		read.read(); // wdith
		read.read(); // wdith
		read.read(); // wdith
		
		read.read(); // height
		read.read(); // height
		read.read(); // height
		read.read(); // height
		
		read.read(); // color
		
		var img = new BufferedImage(128, 128, BufferedImage.TYPE_BYTE_GRAY);
		
		// now read patterns table
		for(var i = 0; i < 256; ++i) {
			var x = i % 16;
			var y = i / 16;
			
			for(var r = 0; r < 8; ++r) {
				var row = read.read();
				
				for(var s = 0; s < 8; ++s) {
					img.setRGB(x * 8 + s, y * 8 + r, ((row >> (7 - s)) & 1) == 1 ? 0xffffffff : 0x00000000);
				}
			}
		}
		
		ImageIO.write(img, "png", new File("patternTable.png"));
	}
	
	public void decodeFile(String path) throws Exception {
		var read = new FileInputStream(path);
		
		if(read.read() != HEADING)
			throw new Exception("not a TRI file");
		
		read.read(); // T
		read.read(); // R
		read.read(); // I
		
		var version = read.read();
		
		var width0 = read.read();
		var width1 = read.read();
		var width2 = read.read();
		var width3 = read.read();
		
		var width = (width0 << 24) | (width1 << 16) | (width2 << 8) | width3;
		
		var height0 = read.read();
		var height1 = read.read();
		var height2 = read.read();
		var height3 = read.read();
		
		var height = (height0 << 24) | (height1 << 16) | (height2 << 8) | height3;
		
		// create output bufferedImage
		var img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		//read the color byte
		read.read();
		
		var patterns = new boolean[8][8][256];
		
		// now read patterns table
		// ignore number one
		for(var i = 1; i < 256; ++i) {
			for(var r = 0; r < 8; ++r) {
				var row = read.read();
				
				for(var s = 0; s < 8; ++s) {
					patterns[s][r][i] = ((row >> (7 - s)) & 1) == 1;
				}
			}
		}
		
		var pWidth = width / 8;
		var pHeight = height / 8;
		
		var pretty = false;
		
		// now read the image
		for(var j = 0; j < pHeight; ++j) {
			for(var i = 0; i < pWidth; ++i) {
				var pattern = read.read();
				
				if(pattern == 0) {
					var red = read.read();
					var gre = read.read();
					var blu = read.read();
					
					var color = (0xff << 24) | (red << 16) | (gre << 8) | blu;
					
					for(var k = 0; k < 8; ++k) {
						for(var l = 0; l < 8; ++l) {
							img.setRGB(i * 8 + l, j * 8 + k, color);
						}
					}
				} else {
					var red0 = read.read();
					var gre0 = read.read();
					var blu0 = read.read();
					
					var color0 = (0xff << 24) | (red0 << 16) | (gre0 << 8) | blu0;
					
					var red1 = read.read();
					var gre1 = read.read();
					var blu1 = read.read();
					
					var color1 = (0xff << 24) | (red1 << 16) | (gre1 << 8) | blu1;
					
					if(pretty) {
						var chunk = new int[8][8][3];
						for(var k = 0; k < 8; ++k) {
							for(var l = 0; l < 8; ++l) {
								if(patterns[l][k][pattern]) {
									chunk[l][k][0] = red1;
									chunk[l][k][1] = gre1;
									chunk[l][k][2] = blu1;
								} else {
									chunk[l][k][0] = red0;
									chunk[l][k][1] = gre0;
									chunk[l][k][2] = blu0;
								}
							}
						}
						
						for(var k = 0; k < 8; ++k) {
							for (var l = 0; l < 8; ++l) {
								var total = 0;
								var avgR = 0;
								var avgG = 0;
								var avgB = 0;
								if(l != 0) {
									avgR += chunk[l - 1][k][0];
									avgG += chunk[l - 1][k][1];
									avgB += chunk[l - 1][k][2];
									++total;
								}
								if(l != 7) {
									avgR += chunk[l + 1][k][0];
									avgG += chunk[l + 1][k][1];
									avgB += chunk[l + 1][k][2];
									++total;
								}
								if(k != 0) {
									avgR += chunk[l][k - 1][0];
									avgG += chunk[l][k - 1][1];
									avgB += chunk[l][k - 1][2];
									++total;
								}
								if(k != 7) {
									avgR += chunk[l][k + 1][0];
									avgG += chunk[l][k + 1][1];
									avgB += chunk[l][k + 1][2];
									++total;
								}
								
								var avgColor = (0xff << 24) | ((avgR / total) << 16) | ((avgG / total) << 8) | (avgB / total);
								
								img.setRGB(i * 8 + l, j * 8 + k, avgColor);
							}
						}
					} else {
						for(var k = 0; k < 8; ++k) {
							for(var l = 0; l < 8; ++l) {
								img.setRGB(i * 8 + l, j * 8 + k, patterns[l][k][pattern] ? color1 : color0);
							}
						}
					}
				}
			}
		}
		
		// finish reading
		read.close();
		
		// write output image
		ImageIO.write(img, "png", new File("decodedTRI.png"));
	}
}
