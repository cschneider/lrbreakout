package breakout;

import games.ClipInfo;

import java.util.EnumMap;

import breakout.constants.Sound;

public class SoundPlayer {
	public static EnumMap<Sound, ClipInfo> soundMap = new EnumMap<Sound, ClipInfo>(Sound.class);
	
	public static void loadSounds() {
		for (Sound sound : Sound.values()) {
			ClipInfo clip = new ClipInfo("/sounds/" + sound.getFileName());
			soundMap.put(sound, clip);
		}
	}
	
	public static void playSound(Sound sound) {
		soundMap.get(sound).play();
	}
}
