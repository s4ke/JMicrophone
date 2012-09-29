package de.hotware.jmicrophone;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;

import de.hotware.hotsound.audio.data.BasicPlaybackAudioDevice;
import de.hotware.hotsound.audio.data.RecordAudio;
import de.hotware.hotsound.audio.player.IMusicPlayer;
import de.hotware.hotsound.audio.player.ISong;
import de.hotware.hotsound.audio.player.MusicPlayerException;
import de.hotware.hotsound.audio.player.RecordSong;
import de.hotware.hotsound.audio.player.StreamMusicPlayer;

public class MicrophonePlayer implements Runnable {

	@Override
	public void run() {

	}

	public static void main(String[] pArgs) throws LineUnavailableException,
			MusicPlayerException {
		if(pArgs.length > 0) {
			List<Mixer> mixers = RecordAudio.getRecordMixers();
			Map<Mixer, AudioFormat[]> formatsMapped = new HashMap<>();
			for(Mixer mixer : mixers) {
				mixer.open();
				Line.Info[] infos = (Line.Info[]) mixer.getTargetLineInfo();
				mixer.close();
				for(Line.Info info : infos) {
					AudioFormat[] supportedFormats = ((DataLine.Info) info)
							.getFormats();
					formatsMapped.put(mixer, supportedFormats);
				}
			}
			String first = pArgs[0];
			switch(first) {
				case "info": {
					for(Entry<Mixer, AudioFormat[]> entry : formatsMapped
							.entrySet()) {
						Mixer mixer = entry.getKey();
						Mixer.Info mixerInfo = mixer.getMixerInfo();
						System.out.println(mixerInfo);
						for(AudioFormat format : entry.getValue()) {
							System.out.println(format);
						}
					}
					break;
				}
				case "play": {
					IMusicPlayer player = new StreamMusicPlayer();
					Mixer mixer = mixers.get(Integer.valueOf(pArgs[1]));
					AudioFormat from = formatsMapped.get(mixer)[Integer
							.valueOf(pArgs[2])];
					float sampleRate = from.getSampleRate();
					if(sampleRate == AudioSystem.NOT_SPECIFIED) {
						sampleRate = Float.parseFloat(pArgs[3]);
					}
					float frameRate = from.getFrameRate();
					if(frameRate == AudioSystem.NOT_SPECIFIED) {
						frameRate = Float.parseFloat(pArgs[4]);
					}
					AudioFormat format = new AudioFormat(from.getEncoding(),
							sampleRate,
							from.getSampleSizeInBits(),
							from.getChannels(),
							from.getFrameSize(),
							frameRate,
							from.isBigEndian());
					ISong song = new RecordSong(mixer, format, 128000);
					player.insert(song, new BasicPlaybackAudioDevice(null, 128000));
					player.start();
					break;
				}
			}
		}
	}

}
