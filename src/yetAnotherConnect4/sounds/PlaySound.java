package yetAnotherConnect4.sounds;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class PlaySound {
	
	private static Bell lastBell = Bell.b5;
	
	public static void playNextBell() {
		playSound(lastBell.next());
	}
	public static void playSound(Bell bell) {
		try {
			Clip clip = AudioSystem.getClip();
			InputStream in = PlaySound.class.getResourceAsStream(bell.name()+".wav");
			AudioInputStream ais = AudioSystem.getAudioInputStream(in);
			clip.open(ais);
			
			clip.start();
			lastBell = bell;
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	public enum Bell{
		d5,
		e5,
		f5,
		g5,
		a5,
		b5;
		
		public Bell next() {
			switch (this) {
			case a5:
				return b5;
			case b5:
				return d5;
			case d5:
				return e5;
			case e5:
				return f5;
			case f5:
				return g5;
			case g5:
				return a5;
			default:
				return null;
			}
		}
	}

	public static void playDorp() {
		try {
			Clip clip = AudioSystem.getClip();
			InputStream in = PlaySound.class.getResourceAsStream("dunt.wav");
			AudioInputStream ais = AudioSystem.getAudioInputStream(in);
			clip.open(ais);
			
			clip.start();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	public static void resetBell() {
		lastBell = Bell.b5;
	}
}
