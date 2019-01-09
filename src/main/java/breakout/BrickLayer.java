package breakout;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import breakout.constants.BallMode;
import breakout.constants.BrickType;
import breakout.constants.ExtraType;
import breakout.constants.GameImage;
import breakout.constants.Sound;
import breakout.sprite.Ball;
import breakout.sprite.Bullet;
import breakout.sprite.Extra;
import breakout.sprite.FadingSprite;
import breakout.sprite.Sprite;

public class BrickLayer {
	
	private static final int BRICK_FADE_TIME = 500;
	
	private static final int LINES_IN_LEVEL_FILE = 18;
	BrickType[][] bricks;
	ExtraType[][] extras;

	int sizeX;
	int sizeY;

	int brickSizeX = 40;
	int brickSizeY = 20;

	Map<Character, BrickType> brickTypeMap;
	Map<Character, ExtraType> extraTypeMap;

	String fileName;
	List<Extra> extraList;
	List<Sprite> spriteList;
	private String author;
	private String levelName;
	
	public BrickLayer(int sizeX, int sizeY, List<Extra> extraList, List<Sprite> spriteList) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;

		bricks = new BrickType[sizeY][sizeX];
		extras = new ExtraType[sizeY][sizeX];
		
		brickTypeMap = new HashMap<Character, BrickType>();
		for (BrickType brickType : BrickType.values()) {
			brickTypeMap.put(brickType.getType(), brickType);
		}
		
		extraTypeMap = new HashMap<Character, ExtraType>();
		for (ExtraType extraType : ExtraType.values()) {
			extraTypeMap.put(extraType.getType(), extraType);
		}
		
		this.extraList = extraList;
		this.spriteList = spriteList;
		clearBricks();
	}
	
	public void loadLayout(int level) {
		loadLayout(fileName, level);
	}

	public void loadLayout(String fileName, int level) {
		this.fileName = fileName;
		if (level == 0) return;
		try {
			InputStream is = this.getClass().getClassLoader()
					.getResourceAsStream(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			char ch;
			int cy = 0;
			
			for (int c=0; c < level; c++) {
				line = "";
				while (line!=null && !line.equals("Level:")) {
					line = br.readLine();
				}
			}
			if (line==null) return;
			author = br.readLine();
			levelName =	br.readLine();
			br.readLine();

			for (cy=0; cy < LINES_IN_LEVEL_FILE; cy++) {
				line = br.readLine();
				if (line.length() > sizeX) {
					throw new RuntimeException("Line too long");
				}
				for (int cx = 0; cx < line.length(); cx++) {
					ch = line.charAt(cx);
					BrickType brick = brickTypeMap.get(ch);
					setBrick(cx, cy, brick);
				}
			}
			
			line = br.readLine();
			for (cy=0; cy < LINES_IN_LEVEL_FILE; cy++) {
				line = br.readLine();
				if (line.length() > sizeX) {
					throw new RuntimeException("Line too long");
				}
				for (int cx = 0; cx < line.length(); cx++) {
					ch = line.charAt(cx);
					ExtraType extra = extraTypeMap.get(ch);
					setExtra(cx, cy, extra);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Error loading layout");
		}
	}

	public void clearBricks() {
		for (int cy = 0; cy < sizeY; cy++) {
			for (int cx = 0; cx < sizeX; cx++) {
				setBrick(cx, cy, BrickType.Empty);
				setExtra(cx, cy, ExtraType.Empty);
			}
		}
	}

	public BrickType getBrick(int x, int y) {
		try {
			return bricks[y][x];
		} catch (Exception e) {
			return BrickType.Empty;
		}
	}

	public void setBrick(int x, int y, BrickType brick) {
		if (brick == null ||  x<0 || x>=sizeX || y<0 || y>=sizeY) {
			return;
		}
		bricks[y][x] = brick;
	}
	
	public ExtraType getExtra(int x, int y) {
		try {
			return extras[y][x];
		} catch (Exception e) {
			return ExtraType.Empty;
		}
	}

	public void setExtra(int x, int y, ExtraType extra) {
		if (extra == null || x<0 || x>=sizeX || y<0 || y>=sizeY) {
			return;
		}
		extras[y][x] = extra;
	}

	public void draw(Graphics2D g) {
		
		int x = 0;
		int y = 0;
		for (int cy = 0; cy < sizeY; cy++) {
			for (int cx = 0; cx < sizeX; cx++) {
				BrickType brick = getBrick(cx, cy);
				BufferedImage image = ImageCache.getImage(GameImage.Brick, brick.ordinal() - 1);
				if (!BrickType.Empty.equals(brick) && image != null) {
					g.drawImage(image, x, y, null);
				}
				x += brickSizeX;
			}
			x = 0;
			y += brickSizeY;
		}
	}

	public int getRow(double y) {
		return new Double(y / brickSizeY).intValue();
	}

	public int getCol(double x) {
		return new Double(x / brickSizeX).intValue();
	}
	
	public void generateExtra(int x, int y, ExtraType extraType) {
		Extra extra = new Extra(x, y, extraType);
		extraList.add(extra);
	}

	private int brickHit(BallMode ballMode, int col, int row, double speedX, double speedY) {
		BrickType oldBrick = getBrick(col, row);
		BrickType brick = oldBrick;
		
		if (ballMode == BallMode.Weak && new Random().nextInt(100)<40) {
			// Weak ball and 40% chance of no break true
			return 0;
		}
		
		switch (oldBrick) {
			case Indestructible: break;
			case IndestEnergy: 
				if (ballMode == BallMode.Penetrative) {
					brick = BrickType.Empty;
				} 
				break;
			case IndestEnergyRandom:
				if (ballMode == BallMode.Penetrative) {
					brick = BrickType.Empty;
				}
				// deflect randomly
				break;
			case BlueThree: brick = BrickType.BlueTwo; break;
			case BlueTwo: brick = BrickType.BlueOne; break;
			case GreenThree: brick = BrickType.GreenTwo; break;
			case GreenTwo: brick = BrickType.GreenOne; break;
			default: brick = BrickType.Empty; break;
		}
		int score = 0;
		if (ballMode == BallMode.Explosive && 
				!(  oldBrick == BrickType.Indestructible ||
					oldBrick == BrickType.IndestEnergy ||
					oldBrick == BrickType.IndestEnergyRandom)) {
			// Explosive ball
			score+=brickExplode(col, row);
		} else if (oldBrick == BrickType.TNT) {
			// Explosive brick
			score+=brickExplode(col, row);
		} else {
			SoundPlayer.playSound(Sound.Reflect);
		}
		
		if (brick!=oldBrick) {
			score = oldBrick.getScore();
			ExtraType extraType = getExtra(col, row);
			if (extraType == ExtraType.Empty && new Random().nextInt(10)>7) {
				extraType = ExtraType.values()[new Random().nextInt(ExtraType.values().length)];
			}
			if (extraType != ExtraType.Empty) {	
				generateExtra(col*brickSizeX, row*brickSizeY, extraType);
			}
			// Add a sprite representing the fading out brick
			if (brick == BrickType.Empty) {
				BufferedImage im = ImageCache.getImage(GameImage.Brick, oldBrick.ordinal()-1);
				FadingSprite sprite = new FadingSprite(col*brickSizeX, row*brickSizeY, speedX, speedY, BRICK_FADE_TIME);
				sprite.setImage(im);
				spriteList.add(sprite);
			}
		}
		setBrick(col, row, brick);
		return score;
	}
	
	public int brickExplode(int cx, int cy) {
		SoundPlayer.playSound(Sound.Explosion);
		setBrick(cx, cy, BrickType.Empty); // Avoid recursion
		int score = 0;
		score += brickHit(BallMode.Normal, cx-1, cy-1, 0, 0);
		score += brickHit(BallMode.Normal, cx,   cy-1, 0, 0);
		score += brickHit(BallMode.Normal, cx+1, cy-1, 0, 0);
		score += brickHit(BallMode.Normal, cx-1, cy, 0, 0);
		score += brickHit(BallMode.Normal, cx+1, cy, 0, 0);
		score += brickHit(BallMode.Normal, cx-1, cy+1, 0, 0);
		score += brickHit(BallMode.Normal, cx,   cy+1, 0, 0);
		score += brickHit(BallMode.Normal, cx+1, cy+1, 0, 0);
		return score;
	}

	/* Test an individual brick */
	private boolean checkBrick(Ball ball, int col, int row) {
		Point brick = gridToScreen(col, row);

		double ballX = ball.getPosX();
		double ballY = ball.getPosY();
		double radius = Ball.RADIUS;
		double dx = ball.getDx();
		double dy = ball.getDy();

		int left = brick.x;
		int top = brick.y;
		int right = left + brickSizeX;
		int bottom = top + brickSizeY;

		/* First check if ball collides with brick */
		if (ballX + radius + dx < left
				|| ballX - radius + dx > right
				|| ballY + radius + dy < top
				|| ballY - radius + dy > bottom) {
			return false; // Ball doesn't touch this brick.
		}

		/*
		 * Which side of the brick we are bouncing off? First, we determine the
		 * distances to each side
		 */

		double distToLeft = Math.abs((ballX + radius) - left);
		double distToRight = Math.abs((ballX - radius) - right);

		double distToTop = Math.abs((ballY + radius) - top);
		double distToBottom = Math.abs((ballY - radius) - bottom);

		/*
		 * Then we need to know which side is the shortest distance from the
		 * ball.
		 */

		double xMinDistance = Math.min(distToRight, distToLeft);
		double yMinDistance = Math.min(distToTop, distToBottom);

		BrickType brickType = getBrick(col, row);
		if (ball.getMode()==BallMode.Penetrative && brickType != BrickType.Indestructible) {
			// Do not deflect
			return true;
		}
		
		if (xMinDistance < yMinDistance) {
			ball.flipSpeedX();
		} else
			ball.flipSpeedY();

		return true;
	}

	private Point gridToScreen(int col, int row) {
		return new Point(col * brickSizeX, row * brickSizeY);
	}

	public int checkBricks(Ball ball) {
		double speedX = ball.getSpeedX();
		double speedY = ball.getSpeedY();
		for (int cy=0; cy<sizeY; cy++) {
			for (int cx=0; cx<sizeX; cx++) {
				BrickType brickType = getBrick(cx, cy);
				if (brickType != BrickType.Empty) {
					if (checkBrick(ball, cx, cy)) {
						return brickHit(ball.getMode(), cx, cy, speedX, speedY);
					}
				}
			}
		}
		return 0;
	}
	
	/* Test an individual brick */
	private boolean checkBrick(Bullet bullet, int col, int row) {
		Rectangle brickBounds = new Rectangle(col * brickSizeX, row * brickSizeY, brickSizeX, brickSizeY);
		Rectangle bulletBounds = bullet.getBounds();
		return brickBounds.intersects(bulletBounds);
	}

	public int checkBricks(Bullet bullet) {
		for (int cy=0; cy<sizeY; cy++) {
			for (int cx=0; cx<sizeX; cx++) {
				BrickType brickType = getBrick(cx, cy);
				if (brickType != BrickType.Empty) {
					if (checkBrick(bullet, cx, cy)) {
						return brickHit(BallMode.Normal, cx, cy, 
								bullet.getSpeedX(), bullet.getSpeedY());
					}
				}
			}
		}
		return 0;
	}


	public int countBricks() {
		int numBricks = 0;
		for (int cy=0; cy<sizeY; cy++) {
			for (int cx=0; cx<sizeX; cx++) {
				BrickType brick = getBrick(cx, cy);
				if (brick.toDestroy()) numBricks ++;
			}
		}
		return numBricks;
	}

	public void createLowerWall() {
		for (int cx=0; cx<sizeX; cx++) {
			bricks[sizeY-1][cx]=BrickType.Indestructible;
		}
	}
	
	public void destroyLowerWall() {
		for (int cx=0; cx<sizeX; cx++) {
			bricks[sizeY-1][cx]=BrickType.Empty;
		}
	}

	public String getAuthor() {
		return author;
	}

	public String getLevelName() {
		return levelName;
	}
	
	


}
