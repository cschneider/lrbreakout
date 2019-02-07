package breakout;

import static java.util.stream.Collectors.toSet;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import breakout.constants.BallMode;
import breakout.constants.ExtraType;
import breakout.constants.GameImage;
import breakout.constants.PaddleMode;
import breakout.constants.Sound;
import breakout.sprite.Ball;
import breakout.sprite.Bullet;
import breakout.sprite.Extra;
import breakout.sprite.Paddle;
import breakout.sprite.Sprite;

public class BreakoutGame {
	
	private static final int ADD_SECONDS = 5;
	// Effect timeouts
	
	private static final String LEVEL_SET_FILE_NAME = "levels/Original";
	
	static final int MAX_LIVES = 8;
	private static final int START_LIVES = 5;
	
	private static final int MAX_BALL_SPEED = 500;
	private static final int DEFAULT_BALL_SPEED = 250;
	private static final int MIN_BALL_SPEED = 200;
	
	private volatile boolean gameOver = false;
	private volatile boolean paused = false;
	private volatile boolean running = false;
	
	// Game objects
	private Paddle paddle;
	private List<Ball> ballList;
	private List<Extra> extraList;
	private List<Sprite> spriteList;
	private BrickLayer brickLayer;

	boolean generateBall = true;
	private int level;
	private int lives;
	private int score;
	
	private int sizeX;
	private int sizeY;
	
	final Map<ExtraType, TimedEvent> eventMap;
	
	private int ballSpeed;
	private BallMode ballMode = BallMode.Normal;
	
	// for displaying messages
	private Font msgFont;
	
	private int frameCount;
	private long frameCountStartTime;
	
	private Queue<MouseEvent> mouseEvents = new ConcurrentLinkedQueue<MouseEvent>();
	private Queue<KeyEvent> keyEvents = new ConcurrentLinkedQueue<KeyEvent>();

	private int fps;

	public BreakoutGame(int sizeX, int sizeY) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		
		// Load game elements
		ballList = new ArrayList<Ball>(10);
		extraList = new ArrayList<Extra>(10);
		spriteList = new ArrayList<Sprite>(10);
		brickLayer = new BrickLayer(14, 23, extraList, spriteList);
		
		paddle = new Paddle();
		paddle.setPosY(sizeY - paddle.getSizeY() - 30);
		msgFont = new Font("SansSerif", Font.BOLD, 12);
		gameOver = false;
		running = true;
		
		eventMap = initEventMap();
		newGame();
	}

	private Map<ExtraType, TimedEvent> initEventMap() {
		EnumMap<ExtraType, TimedEvent> events = new EnumMap<ExtraType, TimedEvent>(ExtraType.class);
		for (ExtraType eventType : ExtraType.values()) {
			TimedEvent event = new TimedEvent();
			events.put(eventType, event);
		}
		return events;
	}
	
	public void generateExtra(int x, int y, ExtraType extraType) {
		brickLayer.generateExtra(x, y, extraType);
	}
	
	public void doGenerateBall(BallMode mode, boolean stickToPaddle) {
		int x = new Double(paddle.getPosX() + paddle.getSizeX() / 2).intValue();
		int y = new Double(paddle.getPosY()- Ball.RADIUS).intValue();
		Ball ball = new Ball(mode, x, y, -30, DEFAULT_BALL_SPEED, stickToPaddle);
		ballList.add(ball);
		generateBall = false;
		if (!stickToPaddle) SoundPlayer.playSound(Sound.ExtraBall);
	}
	
	public void setBallMode(BallMode mode) {
		if (mode == BallMode.Normal) {
			eventMap.get(ExtraType.EnergyBall).deActivate();
		}
		ballMode = mode;
		for (Ball ball : ballList) {
			ball.setMode(mode);
		}
	}
	
	public void newGame() {
		score = 0;
		level = 0;
		lives = START_LIVES;
		brickLayer.loadLayout(LEVEL_SET_FILE_NAME, level);
		paused = false;
		gameOver = false;
		frameCount = 0;
		frameCountStartTime = System.nanoTime();
		fps = 0;
		nextLevel();
	}
	
	void resetEffects() {
	    eventMap.values().forEach(TimedEvent::deActivate);
		brickLayer.destroyLowerWall();
		ballSpeed = DEFAULT_BALL_SPEED;
		setBallMode(BallMode.Normal);
		paddle.reset();
		ballList.clear();
		extraList.clear();
		spriteList.clear();
	}
	
	public void setBallSpeed(int ballSpeed) {
		this.ballSpeed = Math.max(MIN_BALL_SPEED, Math.min(ballSpeed, MAX_BALL_SPEED));
		ballList.forEach(ball -> ball.setSpeed(this.ballSpeed));
	}
	
	void nextLevel() {
		resetEffects();
		brickLayer.countBricks();
		level++;
		brickLayer.loadLayout(level);
		doGenerateBall(BallMode.Normal, true);
	}
	
	public void setPaddlePos(int posX) {
		double oldPosX = paddle.getPosX();
		if (eventMap.get(ExtraType.Freeze).isActive()) {
			return;
		}
		if (posX > sizeX - paddle.getSizeX()) {
			posX = sizeX - paddle.getSizeX();
		}
		paddle.setPosX(posX);
		double moveX = posX - oldPosX;
		ballList.stream()
		    .filter(Ball::isStickToPaddle)
		    .forEach(ball -> ball.moveX(moveX));
	}
	
	public void checkWalls(Ball ball) {
		double ballNewX = ball.getPosX() + ball.getDx();
		double ballNewY = ball.getPosY() + ball.getDy();
		if (ballNewX - Ball.RADIUS < 0
				|| ballNewX + Ball.RADIUS > sizeX) {
			ball.flipSpeedX();
		}
		if (ballNewY - Ball.RADIUS < 0) {
			ball.flipSpeedY();
		}
		if (ballNewY + Ball.RADIUS > sizeY) {
			ball.setDeleted(true);
		}
	}
	
	public void checkWalls(Extra extra) {
		if (extra.getPosY() > sizeY) {
			extra.setDeleted(true);
		}
	}

	public void resumeGame() {
		paused = false;
	}

	// called when the JFrame is deactivated / iconified
	public void pauseGame() {
		paused = true;
	}

	// called when the JFrame is closing
	public void stopGame() {
		running = false;
	}

	
	private void addScore(int aScore) {
		score += aScore;
		//SoundPlayer.playSound(Sound.Score);
	}
	
	public void consumeBonusesDestroyMaluses() {
		for (int c=0; c<extraList.size(); c++) {
			Extra extra = extraList.get(c);
			if (extra.getType().isBonus() && extra.getType()!=ExtraType.Stars) {
				handleExtraHit(extra);
			} else {
				extra.setDeleted(true);
			}
		}
	}
	
	public void handleExtraHit(Extra extra) {
		if (extra.getType() == ExtraType.Random) {
			// Generate a random event
			int randNum = new Random().nextInt(ExtraType.values().length-1) + 1;
			extra.setType(ExtraType.values()[randNum]); 
		}
		
		// Setup timeout for event
		if (extra.getType().getTimeOut()>0) {
			ExtraType eventType = extra.getType();
			switch (eventType) {
				case ExplosiveBall:
				case EnergyBall:
				case WeakBall: eventType = ExtraType.EnergyBall; break;
				default: break;
			}
			eventMap.get(eventType).setupEvent(extra.getType().getTimeOut());
		}
		
		switch (extra.getType()) {
			case Points200: addScore(200); break; 
			case Points500: addScore(500); break;
			case Points1000: addScore(1000); break;
			case Points2000: addScore(2000); break;
			case Points5000: addScore(5000); break;
			case Points10000: addScore(10000); break;
			case ExtraScore:  break;
			case Smaller: paddle.makeSmaller(); break;
			case Larger: paddle.makeLarger(); break;
			case Life:
				SoundPlayer.playSound(Sound.GainLife);
				lives = Math.max(lives+1, MAX_LIVES);
				break;
			case Sticky: paddle.setPaddleMode(PaddleMode.Sticky); break;
			case EnergyBall: setBallMode(BallMode.Penetrative); break;
			case NormalBall: doGenerateBall(ballMode, eventMap.get(ExtraType.Sticky).isActive()); break;
			case Wall: brickLayer.createLowerWall(); break;
			case Freeze: paddle.setPaddleMode(PaddleMode.Frozen); break;
			case Weapon: paddle.setWeapon(true); break;
			case Random: break; // Handled before
			case Fast: setBallSpeed(MAX_BALL_SPEED); eventMap.get(ExtraType.Slow).deActivate(); break;
			case Slow: setBallSpeed(MIN_BALL_SPEED); eventMap.get(ExtraType.Fast).deActivate(); break;
			case Stars: consumeBonusesDestroyMaluses(); break;
			case Dark: break;
			case Chaos: break;
			case GhostPaddle: break;
			case Reset: resetEffects(); break;
			case Time: prolongEvents(); break;
			case ExplosiveBall: setBallMode(BallMode.Explosive); break;
			case AttractBonus: break;
			case AttractMalus: break;
			case WeakBall: setBallMode(BallMode.Weak); break;
			default: break;
		}
		extra.setDeleted(true);
		SoundPlayer.playSound(Sound.Click);
	}

	// Add ADD_SECONDS seconds to all active events
    private void prolongEvents() {
        eventMap.values().stream()
            .filter(TimedEvent::isActive)
            .forEach(event -> event.setupEvent(ADD_SECONDS));
    }
	
	public void releaseStickyBalls() {
	    ballList.stream().forEach(ball -> ball.setStickToPaddle(false));
	}

	public void handleMouseEvent(MouseEvent e) {
		if (isGameOver()) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				newGame();
			}
		} else {
			setPaddlePos(e.getX());
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (paddle.isWeapon()) {
					Bullet bullet = new Bullet(paddle.getPosX()+paddle.getSizeX()/2-2, paddle.getPosY(), 
							0, -300, 100);
					spriteList.add(bullet);
				}
				releaseStickyBalls();
			}
		}
	}
	
	void handleKeyEvent(KeyEvent e) {
		int keyCode = e.getKeyCode();

		// termination keys
		// listen for esc, q, end, ctrl-c on the canvas to
		// allow a convenient exit from the full screen configuration
		if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q)
				|| (keyCode == KeyEvent.VK_END)
				|| ((keyCode == KeyEvent.VK_C) && e.isControlDown())) {
			stopGame();
		}

		if (keyCode == KeyEvent.VK_P) {
			paused = ! paused;
		}
		
		// game-play keys
		if (!isPaused() && !isGameOver()) {
			// move the sprite and ribbons based on the arrow key pressed
			if (keyCode == KeyEvent.VK_M) {
				PaddleMode mode = paddle.getPaddleMode();
				int newModeOrdinal = (mode.ordinal() + 1)
						% PaddleMode.values().length;
				paddle.setPaddleMode(PaddleMode.values()[newModeOrdinal]);
			}
			if (keyCode == KeyEvent.VK_E) {
				ExtraType extraType = ExtraType.values()[new Random().nextInt(ExtraType.values().length)];
				generateExtra(new Random().nextInt(400)+100, 100, extraType);
			}
			if (keyCode == KeyEvent.VK_N) {
				nextLevel();
			}
		}

		if (isGameOver()) {
			if (keyCode == KeyEvent.VK_SPACE) {
				newGame();
			}
		}
	}

    // Delete all sprites that are marked for deletion
    public void deleteMarked(List<? extends Sprite> spriteList) {
        Set<? extends Sprite> toRemove = spriteList.stream().filter(Sprite::isDeleted).collect(toSet());
        spriteList.removeAll(toRemove);
    }

	void gameUpdate() {
		while (!mouseEvents.isEmpty()) {
			MouseEvent me = mouseEvents.remove();
			handleMouseEvent(me);
		}
		while (!keyEvents.isEmpty()) {
			KeyEvent ke = keyEvents.poll();
			handleKeyEvent(ke);
		}
		if (paused || gameOver) {
		    return;
		}
		
        // Generate ball if necessary
        if (generateBall) {
            // BallMode ballMode = BallMode.values()[new
            // Random().nextInt(BallMode.values().length)];
            doGenerateBall(ballMode, eventMap.get(ExtraType.Sticky).isActive());
        }

        ballList.forEach(this::moveBall);
        extraList.forEach(this::moveExtra);
        spriteList.stream().peek(Sprite::move).filter(Bullet.class::isInstance).map(sprite -> (Bullet) sprite)
                .forEach(this::handleBullet);

        deleteMarked(ballList);
        deleteMarked(extraList);
        deleteMarked(spriteList);

        // Last ball out => loose a life
        if (ballList.size() == 0) {
            SoundPlayer.playSound(Sound.LooseLife);
            lives--;
            if (lives <= 0) {
                SoundPlayer.playSound(Sound.GameOver);
                gameOver = true;
            } else {
                resetEffects();
                doGenerateBall(BallMode.Normal, true);
            }
        }

        timeoutEvents();

        // Check if all bricks have been hit
        int numBricks = brickLayer.countBricks();
        if (numBricks == 0) {
            nextLevel();
        }

	}

    private void moveBall(Ball ball) {
        int aScore = brickLayer.checkBricks(ball);
        if (aScore>0) {
        	addScore(aScore);
        }
        if (paddle.checkPaddle(ball) && paddle.getPaddleMode() == PaddleMode.Sticky) {
        	ball.setStickToPaddle(true);
        }
        checkWalls(ball);
        ball.move();
    }

    private void moveExtra(Extra extra) {
        checkWalls(extra);
        if (paddle.checkExtraHit(extra)) {
        	handleExtraHit(extra);
        }
        boolean attract = (extra.getType().isBonus() && eventMap.get(ExtraType.AttractBonus).isActive())
        	|| (!extra.getType().isBonus() && eventMap.get(ExtraType.AttractMalus).isActive());
        extra.move(paddle.getPosX(), attract);
    }

    private void handleBullet(Bullet sprite) {
        int score = brickLayer.checkBricks(sprite);
        if (score > 0) {
            addScore(score);
            sprite.setDeleted(true);
        }
    }

    private void timeoutEvents() {
        eventMap.entrySet().stream()
            .filter(entry -> entry.getValue().checkTriggered())
            .map(Entry::getKey)
            .forEach(this::timeoutEvent);
    }

    private void timeoutEvent(ExtraType extraType) {
        switch (extraType) {
        case EnergyBall:
            setBallMode(BallMode.Normal);
            break;
        case Freeze:
            paddle.setPaddleMode(PaddleMode.Normal);
            break;
        case Wall:
            brickLayer.destroyLowerWall();
            break;
        case Weapon:
            paddle.setWeapon(false);
            break;
        case Sticky:
            paddle.setPaddleMode(PaddleMode.Normal);
            releaseStickyBalls();
            break;
        default:
            break;
        }
    }
		
	public String getStatusLine() {
		StringBuilder statusLine = new StringBuilder();
		statusLine.append("  Score: " + score + " ");
		/*
		for (ExtraType extraType : ExtraType.values()) {
			TimedEvent event = eventMap.get(extraType);
			if (event.isActive()) {
				statusLine.append(" " + extraType.toString() + ": " + event.getSecondsRemaining());
			}
		}	
		*/
		return statusLine.toString();
	}
	
	public void drawCentered(Graphics2D g, String message) {
		g.setFont(msgFont);
		FontRenderContext frc = g.getFontRenderContext();
		Rectangle2D r = msgFont.getStringBounds(message, frc);
		int x = new Double(sizeX / 2 - r.getWidth() / 2).intValue();
		int y = new Double(sizeY / 2 - r.getHeight() / 2).intValue();
		g.setColor(Color.black);
		g.drawString(message, x+1, y+1);
		g.setColor(Color.red);
		g.drawString(message, x, y);
		g.setColor(Color.black);
	}

	// Utility method to tile an image on the background
	protected void tile(Graphics g, Image im, int sizeX, int sizeY) {
		if (im==null) {
			return;
		}
		for (int x = 0; x < sizeX; x += im.getWidth(null)) {
			for (int y = 0; y < sizeY; y += im.getHeight(null)) {
				g.drawImage(im, x, y, null);
			}
		}
	}
	
	public boolean isDarkMode() {
		return eventMap.get(ExtraType.Dark).isActive();
	}
	
	void gameRender(Graphics2D g) {
		if (isDarkMode()) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, sizeX, sizeY);
		} else {			
			BufferedImage[] backgroundImAr = ImageCache.getImageAr(GameImage.Background);
			int imNum = level % backgroundImAr.length;
			tile(g, backgroundImAr[imNum], sizeX, sizeY); // draw backgorund image
			brickLayer.draw(g);
		}
		ballList.forEach(ball -> ball.draw(g));
		extraList.forEach(extra -> extra.draw(g));
		spriteList.forEach(sprite -> sprite.draw(g));
		paddle.draw(g);
		g.setFont(msgFont);
		g.setColor(Color.red);
		g.drawString("Fps: " + fps, 20, 20);
		frameCount ++;
		if (System.nanoTime()-frameCountStartTime>1000000000L) {
			frameCountStartTime = System.nanoTime();
			fps = frameCount;
			frameCount = 0;
		}
		
		if (paused) {
			drawCentered(g, brickLayer.getAuthor() + " -- " + brickLayer.getLevelName());
		}
		if (gameOver) {
			drawCentered(g, "Press Space to start !");
		}
	}

	public void addMouseEvent(MouseEvent e) {
		mouseEvents.add(e);
	}
	
	public void addKeyEvent(KeyEvent e) {
		keyEvents.add(e);
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public boolean isPaused() {
		return paused;
	}

	public int getLives() {
		return lives;
	}

}
