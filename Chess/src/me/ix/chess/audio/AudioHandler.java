package me.ix.chess.audio;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class AudioHandler {

	private String location;
	
	public AudioHandler(String location) {
		this.location = location;
	}
	
	public void playAudio(String audioName) {
		try {
			File musicPath = new File(location + "//" + audioName + ".wav");
			
			if(musicPath.exists()) {
				AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
				Clip clip = AudioSystem.getClip();
				clip.open(audioInput);
				clip.start();
			} else {
				System.out.println("Audio file does not exist!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
