package game;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Music;
import org.newdawn.slick.MusicListener;

public class SoundHandler {
	
	private static String[] songPaths = new String[]{"music2.wav", "music.wav"};
	private static List<Music> musicTracks = new ArrayList<Music>();
	private static int currentSongIndex = 0;

	private static boolean musicJustEnded = false;
	private static final int TIME_BETWEEN_MUSIC = 1000;
	private static int timeUntilStartNextMusic = 0;
	
	static boolean mute = false;
	static float soundsVolume = 0.25f;
	static float musicVolume = 1f;
	
	public static void loadMusic(){
		for(String path : songPaths){
			musicTracks.add(ResourceLoader.createMusic(path));
		}
	}
	
	public static void update(int delta){
		if(musicJustEnded){
			timeUntilStartNextMusic -= delta;
			if(timeUntilStartNextMusic <= 0){
				musicJustEnded = false;
				playNextMusic();
			}
		}
	}
	

	
	
	
	public static void toggleMute(){
		mute(!mute);
	}

	public static void mute(boolean muteSound){
		mute = muteSound;
		if(mute){
			musicTracks.get(currentSongIndex).pause();
		}else{
			musicTracks.get(currentSongIndex).resume();
		}
	}
	
	public static void playNextMusic() {
		
		musicTracks.get(currentSongIndex).play(1, musicVolume);
		musicTracks.get(currentSongIndex).addListener(new MusicListener(){
			public void musicEnded(Music music) {
				musicJustEnded = true;
				timeUntilStartNextMusic = TIME_BETWEEN_MUSIC;
			//DO NOT call "playNextMusic()" from this callback.
			//Causes a deadlock deep down in Slick's classes.
			}
			public void musicSwapped(Music music, Music newMusic) {}
		});
		currentSongIndex = (currentSongIndex + 1) % songPaths.length;
		if(mute){
			musicTracks.get(currentSongIndex).pause();
		}
	}

	public static void play(SoundWrapper soundContainer) {
		if (soundContainer == null) {
			throw new IllegalArgumentException();
		}
		if (mute) {
			return;
		}
		soundContainer.sound.play(1,
				(float) (soundsVolume * soundContainer.volume));
	}

}
