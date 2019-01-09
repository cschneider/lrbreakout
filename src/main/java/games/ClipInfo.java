package games;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineEvent.Type;

/**
 * Load a clip, which can be played, stopped, resumed, looped.
 * 
 * An object implementing the SoundsWatcher interface can be notified when the
 * clip loops or stops.
 * 
 */
public class ClipInfo implements LineListener {
	private byte[] content;

	private Clip clip;

	public ClipInfo(String fileName) {
		InputStream is = this.getClass().getResourceAsStream(fileName);
		if (is == null) {
			throw new RuntimeException("Resource not found on classpath: " + fileName);
		}
		content = loadStream(is);
		clip = loadClip();
	}
	
	public byte[] loadStream(InputStream is) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		int bufLen = 10000;
		byte[] buf = new byte[bufLen];
		int count = 1;		
		try {
			while ((count = is.read(buf, 0, buf.length))>0) {
			    os.write(buf, 0, count);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return os.toByteArray();
	}
	
	public Clip loadClip() {
		try {
			InputStream is = new ByteArrayInputStream(content);
			AudioInputStream ais = AudioSystem.getAudioInputStream(is);
			AudioFormat audioFormat = ais.getFormat();
			clip = (Clip) AudioSystem.getLine(
					new DataLine.Info(Clip.class, audioFormat));
			clip.open(ais);
			ais.close();
			is.close();
			clip.addLineListener(this);
			return clip;
		} catch (Exception e) {
			throw new RuntimeException("Error loading clip", e);
		}
	}
	
	public void play() {
		clip = loadClip();
		if (clip!=null) {
			clip.start();
		}
	}

	/**
	 * stop and reset clip to its start
	 * 
	 */
	public void stop() {
		if (clip!=null) {
			clip.stop();
			clip.setFramePosition(0);
			clip.close();
		}
	}

	public void update(LineEvent event) {
		if (event.getType() == Type.STOP) {
			// clip has stopped => close it so it can be garbage collected 
			event.getLine().close();
		}
	}

}
