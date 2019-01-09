package breakout.sprite;

import java.awt.image.BufferedImage;

import breakout.ImageCache;
import breakout.constants.GameImage;

public class Bullet extends Sprite {

	int animStepTime;
	
	public Bullet(double posX, double posY, double speedX, double speedY, int animStepTime) {
		super(posX, posY, speedX, speedY);
		this.animStepTime = animStepTime;
		this.sizeX = 10;
		this.sizeY = 10;
	}
	
	public BufferedImage[] getImageAr() {
		return ImageCache.getImageAr(GameImage.Shot);
	}
	
	public void move() {
		int imageNum = (getMillisSinceCreate() / animStepTime) % getImageAr().length;
		setImage(getImageAr()[imageNum]);
		super.move();
	}
	
}
