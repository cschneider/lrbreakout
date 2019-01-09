package breakout.sprite;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.EnumMap;

import breakout.ImageCache;
import breakout.constants.GameImage;
import breakout.constants.PaddleMode;

public class Paddle extends Sprite {
	public static final int LEFT = 16;
	public static final int RIGHT = 16;

	private static final int PADDLE_SIZE_CHANGE = 20;
	private static final int DEFAULT_PADDLE_SIZE = 60;
	private static final int MIN_PADDLE_SIZE = 60;
	private static final int MAX_PADDLE_SIZE = 200;
	
	private PaddleMode paddleMode;
	
	private static EnumMap<PaddleMode, BufferedImage> imageMap = new EnumMap<PaddleMode, BufferedImage>(PaddleMode.class);
	
	private BufferedImage leftImage; 
	private BufferedImage midImage;
	private BufferedImage rightImage;
	private boolean weapon;

	public Paddle() {
		super(0, 0, 0, 0);
		sizeX = DEFAULT_PADDLE_SIZE;
		sizeY = 18;
		setPaddleMode(PaddleMode.Normal);
		int c=0;
		for (BufferedImage image : ImageCache.getImageAr(GameImage.Paddle)) {
			if (c<PaddleMode.values().length) {
				imageMap.put(PaddleMode.values()[c], image);
			}
			c++;
		}
		weapon =false;
	}
	
	public boolean checkPaddle(Ball ball) {
		if (ball.getDy()<0) {
			return false; // Avoid flip downwards
		}
		double ballNewX = ball.getPosX() + ball.getDx();
		double ballNewY = ball.getPosY() + ball.getDy();
		boolean hitX = (ballNewX + Ball.RADIUS >= posX && ballNewX - Ball.RADIUS <= posX + sizeX);
		boolean hitY = (ballNewY + Ball.RADIUS >= posY && ballNewY - Ball.RADIUS <= posY + sizeY);
		double maxAngle = 90;
		if (hitX && hitY) {
			ball.flipSpeedY();
			double ballPos = (ballNewX - (posX + sizeX / 2)) / sizeX;
			//double angle = ball.getAngle();
			ball.setAngle(/*angle +*/ maxAngle * ballPos);
			return true;
		}
		return false;
	}
	
	public boolean checkExtraHit(Extra extra) {
		Rectangle extraR = extra.getBounds();
		return getBounds().intersects(extraR);
	}

	protected void tile(Graphics g, BufferedImage im, int sx, int sy, int width) {
		int iw = im.getWidth();
		int ih = im.getHeight();
		for (int x = 0; x < width; x += iw) {
			if (x + iw > width) {
				iw = width - x;
			}
			g.drawImage(im, sx + x, sy, iw, ih, null);
		}
	}
	
	// Getters and Setters
	
	public void buildImage() {
		image = gc.createCompatibleImage(sizeX, sizeY, Transparency.TRANSLUCENT);
		Graphics2D g = (Graphics2D) image.createGraphics();

		BufferedImage im = imageMap.get(paddleMode);
		if (im==null) {
			return;
		}
		leftImage = im.getSubimage(0, 0, LEFT, im.getHeight()); 
		midImage = im.getSubimage(LEFT, 0, im.getWidth() - LEFT -RIGHT, im.getHeight()) ;
		rightImage = im.getSubimage(im.getWidth() - RIGHT, 0, RIGHT, im.getHeight());
		
		g.drawImage(leftImage, 0, 0, null);
		tile(g, midImage, LEFT, 0, sizeX - LEFT -RIGHT);
		g.drawImage(rightImage,	sizeX - RIGHT, 0, null);
	}
	
	public void draw(Graphics2D g) {
		super.draw(g);
		if (weapon) {
			g.drawImage(ImageCache.getImage(GameImage.Weapon), 
				new Double(posX + sizeX /2 - 7).intValue(), 
				new Double(posY).intValue(), null);
		}
	}
	
	public void makeLarger() {
		if (sizeX+PADDLE_SIZE_CHANGE<=MAX_PADDLE_SIZE) {
			sizeX += PADDLE_SIZE_CHANGE;
		}
		buildImage();
	}
	
	public void makeSmaller() {
		if (sizeX-PADDLE_SIZE_CHANGE>=MIN_PADDLE_SIZE) {
			sizeX -= PADDLE_SIZE_CHANGE;
		}
		buildImage();
	}

	public void reset() {
		setPaddleMode(PaddleMode.Normal);
		sizeX = DEFAULT_PADDLE_SIZE;
		weapon = false;
	}

	public PaddleMode getPaddleMode() {
		return paddleMode;
	}
	
	public void setPaddleMode(PaddleMode paddleMode) {
		this.paddleMode = paddleMode;
		buildImage();
	}

	public boolean isWeapon() {
		return weapon;
	}

	public void setWeapon(boolean weapon) {
		this.weapon = weapon;
	}

	
}
