package breakout.sprite;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.EnumMap;

import breakout.ImageCache;
import breakout.constants.BallMode;
import breakout.constants.GameImage;

public class Ball extends Sprite {

	public static final int RADIUS = 6;

	private EnumMap<BallMode, BufferedImage> imageMap = new EnumMap<BallMode, BufferedImage>(BallMode.class);
	
	private double dx;
	private double dy;
	private double speed;
	private double angle;
	
	private BallMode mode;
	private boolean stickToPaddle;
	
	public Ball(BallMode mode, int posX, int posY, double angle, double speed, boolean stickToPaddle) {
		super(posX, posY, 0, 0);
		this.angle = angle;
		this.speed = speed;
		this.stickToPaddle = stickToPaddle;
		calcDxDy();
		this.deleted = false;
		this.mode = mode;
		imageMap = new EnumMap<BallMode, BufferedImage>(BallMode.class);
		int c=0;
		for (BufferedImage image : ImageCache.getImageAr(GameImage.Ball)) {
			if (c<BallMode.values().length) {
				imageMap.put(BallMode.values()[c], image);
			}
			c++;
		}
	}
	
	public void move() {
		if (!stickToPaddle) {
			posX+=getDx();
			posY+=getDy();
		}
	}
	
	public void draw(Graphics2D g) {
		g.drawImage(
				imageMap.get(mode), 
				new Double(posX - RADIUS).intValue(), 
				new Double(posY - RADIUS).intValue(), 
				null);
		//g.drawString(""+new Double(angle).intValue(), new Double(posX).intValue(), new Double(posY).intValue());
	}

	// Getters and Setters
	
	public BallMode getMode() {
		return mode;
	}

	public void setMode(BallMode mode) {
		this.mode = mode;
	}

	public void calcDxDy() {
		dx = Math.sin(angle * (Math.PI / 180 )) * speed * period / 1000000000.0;
		dy = -Math.cos(angle * (Math.PI / 180 )) * speed * period / 1000000000.0;
	}
	
	public double getDx() {
		return dx;		
	}
	
	public double getDy() {
		return dy;
	}
	
	public double getSpeedX() {
		return dx / period * 1000000000.0;
	}
	
	public double getSpeedY() {
		return dy / period * 1000000000.0;
	}

	public void flipSpeedX() {
		angle = -angle;
		calcDxDy();
	}
	
	public void flipSpeedY() {
		if (angle>0) {
			angle = 180 - angle;
		} else {
			angle = - (180 + angle);
		}
		calcDxDy();
	}
	
	public double getAngle() {
		return this.angle; 
	}

	public void setAngle(double angle) {
		this.angle = angle;
		calcDxDy();
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
		calcDxDy();
	}

	public boolean isStickToPaddle() {
		return stickToPaddle;
	}

	public void setStickToPaddle(boolean stickToPaddle) {
		this.stickToPaddle = stickToPaddle;
	}

}
