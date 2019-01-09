package games;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class ImagesLoader {

	public static BufferedImage loadImage(GraphicsConfiguration gc, String fnm) {
		try {
			InputStream is = ImagesLoader.class.getResourceAsStream(fnm);
			if (is == null) {
				throw new FileNotFoundException(fnm);
			}
			BufferedImage im = ImageIO.read(is);

			int transparency = im.getColorModel().getTransparency();
			BufferedImage copy = gc.createCompatibleImage(im.getWidth(), im
					.getHeight(), transparency);
			// create a graphics context
			Graphics2D g2d = copy.createGraphics();
			g2d.drawImage(im, 0, 0, null);
			g2d.dispose();
			return copy;
		} catch (IOException e) {
			throw new RuntimeException("Load Image error for " + fnm + ":\n" + e);
		}
	}

	public static BufferedImage[] loadStripImageArray(GraphicsConfiguration gc, String fnm, int number) {
		return loadStripImageArray(gc, fnm, number, 1);
	}

	public static BufferedImage[] loadStripImageArray(GraphicsConfiguration gc, String fnm, int numX, int numY) {
		//Assert.assertTrue("numX <= 0", numX > 0);
		//Assert.assertTrue("numY <= 0", numY > 0);

		BufferedImage stripIm = loadImage(gc, fnm);
		if (stripIm == null) {
			return new BufferedImage[0];
		}

		int imWidth = (int) stripIm.getWidth() / numX;
		int height = (int) stripIm.getHeight() / numY;
		int transparency = stripIm.getColorModel().getTransparency();

		BufferedImage[] strip = new BufferedImage[numX * numY];
		Graphics2D graphics;

		for (int cy = 0; cy < numY; cy++) {
			for (int cx = 0; cx < numX; cx++) {
				// Retrieve an image from the strip and store it in the strip[]
				strip[cx + cy * numX] = gc.createCompatibleImage(imWidth, height,
						transparency);
				graphics = strip[cx + cy * numX].createGraphics();
				graphics.drawImage(stripIm, 0, 0, imWidth, height,
						cx * imWidth, cy * height, 
						(cx * imWidth) + imWidth, (cy * height) + height, 
						null);
				graphics.dispose();
			}
		}

		return strip;
	}
	
	public static BufferedImage[] loadIndexedImageArray(GraphicsConfiguration gc, String fnm, String suffix) {
		List<BufferedImage> imageList = new ArrayList<BufferedImage>(10);
		int c = 0;
		try {
			while (true) {
				BufferedImage image = loadImage(gc, fnm + c + suffix);
				imageList.add(image);
				c++;
			}
		} catch (RuntimeException e) {
		} 
		return imageList.toArray(new BufferedImage[]{});
	}

}
