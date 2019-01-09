package breakout.sprite;

public class FadingSprite extends Sprite {
	int fadeTime;
	
	public FadingSprite(double posX, double posY, double speedX, double speedY, int fadeTime) {
		super(posX, posY, speedX, speedY);
		this.fadeTime = fadeTime;
	}

	@Override
	public void move() {
		int millis = getMillisSinceCreate();
		if (millis <= fadeTime) {
			float opacity = (float)((1000.0-millis)/1000.0);
			setOpacity(opacity);
		} else {
			setDeleted(true);
		}
		super.move();
	}
}
