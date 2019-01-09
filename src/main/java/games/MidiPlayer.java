package games;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;

public class MidiPlayer implements MetaEventListener {

	private static final int END_OF_TRACK = 47;
	private Sequencer sequencer;
	private HashMap<String, Sequence> midisMap;
	private String currentMidi = null;
	private boolean isLooping = false;

	public MidiPlayer() {
		midisMap = new HashMap<String, Sequence>();
		initSequencer();
	}

	public MidiPlayer(String soundsFnm) {
		midisMap = new HashMap<String, Sequence>();
		initSequencer();
		loadSoundsFile(soundsFnm);
	}

	private void initSequencer()
	{
		try {
			sequencer = MidiSystem.getSequencer();
			if (sequencer == null) {
				throw new RuntimeException("Cannot get a sequencer");
			}

			sequencer.open();
			sequencer.addMetaEventListener(this);

			// maybe the sequencer is not the same as the synthesizer
			// so link sequencer --> synth (this is required in J2SE 1.5)
			if (!(sequencer instanceof Synthesizer)) {
				System.out
						.println("Linking the MIDI sequencer and synthesizer");
				Synthesizer synthesizer = MidiSystem.getSynthesizer();
				Receiver synthReceiver = synthesizer.getReceiver();
				Transmitter seqTransmitter = sequencer.getTransmitter();
				seqTransmitter.setReceiver(synthReceiver);
			}
		} catch (MidiUnavailableException e) {
			System.out.println("No sequencer available");
			sequencer = null;
		}
	}

	/**
	 * The format of the input lines are: <name> <fnm>
	 * and blank lines and comment lines.
	 * 
	 * @param soundsFnm
	 */
	private void loadSoundsFile(String soundsFnm) {
		System.out.println("Reading file: " + soundsFnm);
		try {
			InputStream in = this.getClass().getResourceAsStream(soundsFnm);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			// BufferedReader br = new BufferedReader( new FileReader(sndsFNm));
			StringTokenizer tokens;
			String line;
			String name;
			String fnm;
			while ((line = br.readLine()) != null) {
				if (line.length() == 0) // blank line
					continue;
				if (line.startsWith("//")) // comment
					continue;

				tokens = new StringTokenizer(line);
				if (tokens.countTokens() != 2)
					System.out.println("Wrong no. of arguments for " + line);
				else {
					name = tokens.nextToken();
					fnm = tokens.nextToken();
					load(name, fnm);
				}
			}
			br.close();
		} catch (IOException e) {
			throw new RuntimeException("Error reading file: " + soundsFnm);
		}
	}

	public void close() {
		stop(); // stop the playing sequence
		if (sequencer == null) {
			return;
		}
		if (sequencer.isRunning()) {
			sequencer.stop();
		}
		sequencer.removeMetaEventListener(this);
		sequencer.close();
		sequencer = null;
	}


	/**
	 * create a MidiInfo object, and store it under name
	 */
	public void load(String name, String fnm) {
		if (midisMap.containsKey(name)) {
			System.out.println("Error: " + name + "already stored");
			return;
		}
		Sequence seq = null;
		try {
			seq = MidiSystem.getSequence(ClassLoader.getSystemResourceAsStream(fnm));
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException("Unreadable/unsupported midi file: " + fnm);
		} catch (IOException e) {
			throw new RuntimeException("Could not read: " + fnm);
		}
		midisMap.put(name, seq);
		System.out.println("-- " + name + "/" + fnm);
	}

	/**
	 * play the sequence
	 * 
	 * @param name
	 * @param toLoop
	 */
	public void play(String name, boolean toLoop) {
		Sequence seq = midisMap.get(name);
		if (seq == null) {
			throw new RuntimeException("Error: " + name + "not stored");
		}
		stop();
		try {
			sequencer.setSequence(seq); // load MIDI sequence into the sequencer
			sequencer.setTickPosition(0); // reset to the start
			isLooping = toLoop;
			sequencer.start(); // play it
		} catch (InvalidMidiDataException e) {
			System.out.println("Corrupted/invalid midi file: " + name);
		}
		currentMidi = name;
	}

	public void stop() {
		if (currentMidi != null) {
			isLooping = false;
			if (!sequencer.isRunning()) {
				sequencer.start();
			}
			sequencer.setTickPosition(sequencer.getTickLength());
		}
	}

	public void pause() {
		if (sequencer.isRunning()) {
			sequencer.stop();
		}
	}

	public void resume() {
		if (currentMidi != null) {
			sequencer.start();
		}
	}
	
	public boolean tryLooping()	{
		if (currentMidi == null) {
			return false;
		}
		if (sequencer.isRunning()) {
			sequencer.stop();
			sequencer.setTickPosition(0);
			if (isLooping) { // play it again
				sequencer.start();
				return true;
			}
		}
		return false;
	}

	public void meta(MetaMessage meta) {
		if (meta.getType() == END_OF_TRACK) {
			
		}
	}

}
