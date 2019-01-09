package breakout;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JApplet;

public class BreakoutApplet extends JApplet {

	private static final int FPS = 30;

	private static final int HEIGHT = 480;

	private static final int WIDTH = 6040;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8291800152310127713L;

	private BreakoutPanel bp = null;

	/**
	 * This is the default constructor
	 */
	public BreakoutApplet() {
		super();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	public void init() {
		this.setContentPane(getBreakoutPanel());
		this.setSize(WIDTH, HEIGHT);
		this.setLocation(0, 0);
		addListeners();
		Image cursorImage = Toolkit.getDefaultToolkit().createImage(new byte[]{});
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point( 0, 0), "" );
		setCursor( blankCursor );
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
	
	public void start() {
		super.start();
	}

	public void stop() {
		super.stop();
	}

	/**
	 * This method initializes tetris
	 * 
	 * @return tetris.Tetris
	 */
	private BreakoutPanel getBreakoutPanel() {
		if (bp == null) {
			bp = new BreakoutPanel(this.getGraphicsConfiguration(), FPS, WIDTH, HEIGHT);
			bp.setFocusable(true);
			bp.setRequestFocusEnabled(true);
		}
		return bp;
	}
}
