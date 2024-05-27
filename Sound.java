import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {

    private Clip clip;
    private FloatControl volumeControl;

    public Sound(String path)
            throws IOException, UnsupportedAudioFileException, LineUnavailableException {

        File audioFile = new File(path);
        AudioInputStream in = AudioSystem.getAudioInputStream(audioFile);

        clip = AudioSystem.getClip();
        clip.open(in);

        // Get the volume control from the clip
        volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
    }

    public void loop() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void play() {
        if (clip.isRunning()) {
            stop();
        }
        clip.start();
        clip.setFramePosition(0);
    }

    public void stop() {
        clip.stop();
        clip.setFramePosition(0);
    }

    public void setVolume(int percentage) {
        // Ensure the percentage is within the valid range [0, 100]
        percentage = Math.max(0, Math.min(100, percentage));

        // Convert percentage to a linear scale between minimum and maximum values
        float min = volumeControl.getMinimum();
        float max = volumeControl.getMaximum();
        float range = max - min;
        volumeControl.setValue(min + range * (percentage / 100.0f));
    }

    public float getVolume() {
        // Convert the volume from decibels to a linear scale
        float min = volumeControl.getMinimum();
        float max = volumeControl.getMaximum();
        float range = max - min;
        float currentVolume = volumeControl.getValue();
        return (currentVolume - min) / range;
    }
}