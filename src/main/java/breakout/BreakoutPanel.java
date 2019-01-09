package breakout;

// JackPanel.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* The game's drawing surface. Uses active rendering to a JPanel
 with the help of Java 3D's timer.

 Set up the background and sprites, and update and draw
 them every period nanosecs.

 The background is a series of ribbons (wraparound images
 that move), and a bricks ribbon which the JumpingSprite
 (called 'jack') runs and jumps along.

 'Jack' doesn't actually move horizontally, but the movement
 of the background gives the illusion that it is.

 There is a fireball sprite which tries to hit jack. It shoots
 out horizontally from the right hand edge of the panel. After
 MAX_HITS hits, the game is over. Each hit is accompanied 
 by an animated explosion and sound effect.

 The game begins with a simple introductory screen, which
 doubles as a help window during the course of play. When
 the help is shown, the game pauses.

 The game is controlled only from the keyboard, no mouse
 events are caught.
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import breakout.constants.ExtraType;
import breakout.constants.GameImage;
import breakout.sprite.Ball;
import breakout.sprite.Sprite;


public class BreakoutPanel extends JPanel implements Runnable {
	private static final int GAME_BOARD_HEIGHT = 460;
	private static final int GAME_BOARD_WIDTH = 560;
	private static final long serialVersionUID = 4313130781947352184L;

	private Thread gameThread; // the thread that performs the animation
	private long period; // period between drawing in _nanosecs_

	// off-screen rendering
	private Graphics2D g;
	private Image gameBufferImage = null;
	private Image bufferImage;

	BreakoutGame breakoutGame;
	
	// Images
	private BufferedImage frLeft;
	private BufferedImage frRight;
	private BufferedImage frTop;
	private BufferedImage[] life;

	public BreakoutPanel(GraphicsConfiguration gc, int framesPerSecond,
			int width, int height) {
		this.period = 1000000000L / framesPerSecond;

		setDoubleBuffered(true);
		setBackground(Color.white);
		setPreferredSize(new Dimension(width, height));
		setSize(new Dimension(width, height));

		Sprite.period = period;
		Sprite.gc = gc;
		Ball.period = period;

		ImageCache.init(gc, "/gfx/");
		SoundPlayer.loadSounds();

		breakoutGame = new BreakoutGame(GAME_BOARD_WIDTH, GAME_BOARD_HEIGHT);
		frLeft = ImageCache.getImage(GameImage.FrLeft);
		frRight = ImageCache.getImage(GameImage.FrRight);
		frTop = ImageCache.getImage(GameImage.FrTop);
		life = ImageCache.getImageAr(GameImage.Life);
		//midiPlayer = new MidiPlayer();
		//midiPlayer.play("jjf", true); // repeatedly play it
	}

	private long getTime() {
		return System.nanoTime();
	}

	private long getTimeDiff(long startTime) {
		return System.nanoTime() - startTime;
	}

	/**
	 * wait for the JPanel to be added to the JFrame before starting
	 */
	public void addNotify() {
		super.addNotify(); // creates the peer
		startGame(); // start the thread
	}

	/**
	 * initialise and start the thread
	 * 
	 */
	private void startGame() {
		if (gameThread == null) {
			gameThread = new Thread(this);
			gameThread.start();
		}
	}
	
	public void run() {
		long beforeTime;
		long timeDiff;
		long sleepTime;

		while (breakoutGame.isRunning()) {
 			beforeTime = getTime();
			breakoutGame.gameUpdate();
			render();
			this.repaint(0);

			timeDiff = getTimeDiff(beforeTime);
			sleepTime = (period - timeDiff);

			if (sleepTime > 0) { // some time left in this cycle
				try {
					Thread.sleep(sleepTime / 1000000L); // nano -> ms
				} catch (InterruptedException ex) {
				}
			}
			
		}
		System.exit(0); 
	}
	
	public void render() {
		if (gameBufferImage == null) {
			gameBufferImage = createImage(GAME_BOARD_WIDTH, GAME_BOARD_HEIGHT);
		}
		if (bufferImage == null) {
			bufferImage = createImage(this.getWidth(), this.getHeight());
		}
		g = (Graphics2D) gameBufferImage.getGraphics();
		breakoutGame.gameRender(g);
		
	}
	
	private void reportStats(Graphics g, int x , int y) {
		//g.setFont(msgFont);
		String msg = breakoutGame.getStatusLine();
		g.setColor(Color.black);
		g.drawString(msg, x+1, y+1);
		g.setColor(Color.red);
		g.drawString(msg, x, y);
		g.setColor(Color.black);
		int statY = 50;
		for (ExtraType extra : ExtraType.values()) {
			TimedEvent event = breakoutGame.eventMap.get(extra);
			if (event.isActive()) {
				int seconds = event.getSecondsRemaining();
				BufferedImage im = ImageCache.getImage(GameImage.Extra, extra.ordinal() - 1);
				g.drawImage(im, 600, statY, null);
				g.setColor(Color.black);
				g.drawString(""+seconds, 613, statY + 15);
				g.setColor(Color.red);
				g.drawString(""+seconds, 612, statY + 14);
				statY += 40;
			}
		}
	}

	/**
	 * Draw the buffered image to the screen
	 *
	 */
	@Override
	public void paint(Graphics g) {
		try {
			if (breakoutGame.isDarkMode()) {
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
				g.drawImage(gameBufferImage, 40, 20, null);
			} else {
				g.drawImage(gameBufferImage, 40, 20, null);
				g.drawImage(frLeft, 0, 0, null);
				g.drawImage(frRight, 600, 0, null);
				g.drawImage(frTop, 40, 0, null);
				for (int c=0; c<BreakoutGame.MAX_LIVES; c++) {
					BufferedImage im = (BreakoutGame.MAX_LIVES-c < breakoutGame.getLives())?life[1]:life[0];
					g.drawImage(im, 0, 300 + c * im.getHeight(), null);
				}
			}
			
			reportStats(g, 360, 12);
			Toolkit.getDefaultToolkit().sync();
		} catch (Exception e) {
			System.out.println("Graphics context error: " + e);
		}
	}

}
