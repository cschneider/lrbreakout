package breakout;

import java.awt.Cursor;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;


public class Breakout extends JFrame implements WindowListener {
	private static final long serialVersionUID = -6966108542815061092L;
	private static final boolean FULLSCREEN = true;
	private static int DEFAULT_FPS = 60;
	private BreakoutPanel bp;

	public Breakout(int fps) {
		super("Java Breakout");
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		
		setIgnoreRepaint(true); // turn off paint events since doing active rendering
		setResizable(false);
		
		if (FULLSCREEN) {
			setUndecorated(true); // no menu bar, borders, etc.
			gd.setFullScreenWindow(this);
			DisplayMode dm = getBestMode(gd, 640, 480, 32);
			gd.setDisplayMode(dm);
			
		}

		bp = new BreakoutPanel(gd.getDefaultConfiguration(), fps, 640, 480);
		getContentPane().add(bp, "Center");
		addWindowListener(this);
		
		addListeners();
		
		Image cursorImage = Toolkit.getDefaultToolkit().getImage("");
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point( 0, 0), "" );
		setCursor( blankCursor );
		
		pack();
		setResizable(false);
		setVisible(true);
	}

	public void addListeners() {
		setFocusable(true);
		requestFocus(); // the JPanel now has focus, so receives key events
		// Does not work in fullscreen. Then the Frame needs to receive the keys

		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				bp.breakoutGame.addKeyEvent(e);
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				super.mouseDragged(e);
				bp.breakoutGame.addMouseEvent(e);
			}

			public void mouseMoved(MouseEvent e) {
				super.mouseMoved(e);
				bp.breakoutGame.addMouseEvent(e);
			}
		});

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				super.mouseClicked(e);
				bp.breakoutGame.addMouseEvent(e);
			}
		});
	}
	
	public DisplayMode getBestMode(GraphicsDevice gd, int width, int height, int depth) {
		DisplayMode[] modes = gd.getDisplayModes();
		DisplayMode bestMode = null;
		for (DisplayMode mode : modes) {
			if (mode.getWidth() == width && mode.getHeight() == height && mode.getBitDepth() == depth && mode.getRefreshRate()<75) {
				if (bestMode==null || bestMode.getRefreshRate()<mode.getRefreshRate()) {
					bestMode = mode;
				}
			}
		}
		return bestMode;
	}

	// ----------------- window listener methods -------------

	public void windowActivated(WindowEvent e) {
		bp.breakoutGame.resumeGame();
	}

	public void windowDeactivated(WindowEvent e) {
		bp.breakoutGame.pauseGame();
	}

	public void windowDeiconified(WindowEvent e) {
		bp.breakoutGame.resumeGame();
	}

	public void windowIconified(WindowEvent e) {
		bp.breakoutGame.pauseGame();
	}

	public void windowClosing(WindowEvent e) {
		bp.breakoutGame.stopGame();
	}

	public void windowClosed(WindowEvent e) {
		bp.breakoutGame.stopGame();
	}

	public void windowOpened(WindowEvent e) {
	}

	// ----------------------------------------------------
	public static void main(String args[]) {
		new Breakout(DEFAULT_FPS);
	}

}
