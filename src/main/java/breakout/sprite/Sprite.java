package breakout.sprite;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Sprite {
	protected double posX;
	protected double posY;
	
	protected int sizeX = 0;
	protected int sizeY = 0;	
	
	protected boolean deleted;

	protected double speedX;
	protected double speedY;

	public static long period = 0;
	public static GraphicsConfiguration gc;
	public float opacity;
	
	BufferedImage image;
	
	long createTime;
	
	public Sprite(double posX, double posY, double speedX, double speedY) {
		this.posX = posX;
		this.posY = posY;
		this.speedX = speedX;
		this.speedY = speedY;
		deleted = false;
		opacity = 1;
		image = null;
		createTime = System.nanoTime();
	}
	
	public Rectangle getBounds() {
		return new Rectangle(
				new Double(posX).intValue(), 
				new Double(posY).intValue(), 
				sizeX, 
				sizeY);
	}
	
	public double getDx() {
		return speedX * period / 1000000000.0;
	}
	
	public double getDy() {
		return speedY * period / 1000000000.0;
	}
	
	public void move() {
		posX = posX + getDx();
		posY = posY + getDy();
	}

	private AlphaComposite makeComposite(float alpha) {
	    int type = AlphaComposite.SRC_OVER;
	    return(AlphaComposite.getInstance(type, alpha));
	  }

	public void draw(Graphics2D g) {
		Composite originalComposite = null;
		if (opacity<1) {
			originalComposite = g.getComposite();
			g.setComposite(makeComposite(opacity));
		}
		g.drawImage( image,
					new Double(posX).intValue(), 
					new Double(posY).intValue(), 
					null);
		if (opacity<1) {
			g.setComposite(originalComposite);
		}
		//g.draw(getBounds());
	}
	
	public double getPosX() {
		return posX;
	}

	public void setPosX(double posX) {
		this.posX = posX;
	}

	public double getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public int getSizeX() {
		return sizeX;
	}

	public void setSizeX(int sizeX) {
		this.sizeX = sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	public void setSizeY(int sizeY) {
		this.sizeY = sizeY;
	}

	public BufferedImage getImage() {
		return image;
	}
	
	public void setImage(BufferedImage image) {
		this.image = image; 
	}

	public long getCreateTime() {
		return createTime;
	}

	public float getOpacity() {
		return opacity;
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}
	
	public int getMillisSinceCreate() {
		return new Long((System.nanoTime() - createTime)/1000000L).intValue();
	}

	public double getSpeedX() {
		return speedX;
	}

	public double getSpeedY() {
		return speedY;
	}
}
