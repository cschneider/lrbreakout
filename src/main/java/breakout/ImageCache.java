package breakout;

import games.ImagesLoader;

import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

import breakout.constants.GameImage;

public class ImageCache {
	private static Map<GameImage, BufferedImage[]> imageCache = null;
	
	public static void init(GraphicsConfiguration gc, String imagePath) {
		imageCache = new EnumMap<GameImage, BufferedImage[]>(GameImage.class);
		for (GameImage gameImage : GameImage.values()) {
			BufferedImage[] imageAr;
			if (gameImage.getSuffix() == null) {
				 imageAr = ImagesLoader.loadStripImageArray(gc, 
					imagePath + gameImage.getPath(), gameImage.getNumX(), gameImage.getNumY());
			} else {
				imageAr = ImagesLoader.loadIndexedImageArray(gc, imagePath + gameImage.getPath(), gameImage.getSuffix());
			}
			imageCache.put(gameImage, imageAr);
		}
	}
	
	public static BufferedImage getImage(GameImage gameImage) {
		return imageCache.get(gameImage)[0];
	}
	
	public static BufferedImage getImage(GameImage gameImage, int num) {
		if (num<0 || num > imageCache.get(gameImage).length)  {
			return null;
		}
		return imageCache.get(gameImage)[num];
	}
	
	public static BufferedImage[] getImageAr(GameImage gameImage) {
		return imageCache.get(gameImage);
	}
}
