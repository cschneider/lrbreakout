package breakout.sprite;

import java.awt.image.BufferedImage;
import java.util.EnumMap;

import breakout.ImageCache;
import breakout.constants.ExtraType;
import breakout.constants.GameImage;

public class Extra extends Sprite {
	private ExtraType type;
	
	private static EnumMap<ExtraType, BufferedImage> imageMap = new EnumMap<ExtraType, BufferedImage>(ExtraType.class);
	
	public Extra(double posX, double posY, ExtraType type) {
		super(posX, posY, 0.0, 50.0);
		int c=1;
		for (BufferedImage image : ImageCache.getImageAr(GameImage.Extra)) {
			if (c<ExtraType.values().length) {
				ExtraType type2 = ExtraType.values()[c];
				Extra.imageMap.put(type2, image);
			}
			c++;
		}
		setType(type);
		opacity = 0.5f;
	}
	
	public static void setImages(BufferedImage[] images) {
		
	}
	
	public void move(double attractPointX, boolean attract) {
		if (attract) {
			double maxSpeedX = Math.abs(speedY);
			double dist = attractPointX-posX;
			speedX = Math.min(dist, maxSpeedX);
			speedX = Math.max(speedX, -maxSpeedX);
		} else {
			speedX = 0;
		}
		posX = posX + getDx();
		posY = posY + getDy();
	}

	public ExtraType getType() {
		return type;
	}

	public void setType(ExtraType type) {
		this.image = Extra.imageMap.get(type);
		if (image != null) {
			sizeX = image.getWidth(null);
			sizeY = image.getHeight(null);
		}
		this.type = type;
	}

}
