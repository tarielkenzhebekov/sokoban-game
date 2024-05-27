import java.util.Map;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class SoundsRepository {

    private Map<String, Sound> sounds;

    public SoundsRepository() {
        try {
            sounds = new HashMap<>();

            Sound boxSound = new Sound("./sounds/box_sound.wav");
            addSound("BoxSound", boxSound);
            Sound playerSound = new Sound("./sounds/player_sound.wav");
            addSound("PlayerSound", playerSound);
            Sound wallBumpSound = new Sound("./sounds/wall_sound.wav");
            addSound("WallSound", wallBumpSound);
            Sound goalSound = new Sound("./sounds/goal_sound.wav");
            addSound("GoalSound", goalSound);
            Sound winSound = new Sound("./sounds/win_sound.wav");
            addSound("WinSound", winSound);
            Sound fonSound = new Sound("./sounds/fon_sound.wav");
            addSound("FonSound",fonSound);
        } catch (UnsupportedAudioFileException uafe) {
            System.out.println(uafe);
        } catch (IOException ioe) {
            System.out.println(ioe);
        } catch (LineUnavailableException lue) {
            System.out.println(lue);
        }
    }

    public void addSound(String soundName, String path) {
        try {
            Sound sound = new Sound(path);
            addSound(soundName, sound);
        } catch (UnsupportedAudioFileException uafe) {
            System.out.println(uafe);
        } catch (IOException ioe) {
            System.out.println(ioe);
        } catch (LineUnavailableException lue) {
            System.out.println(lue);
        }
    }

    public void addSound(String soundName, Sound sound) {
        sounds.put(soundName, sound);
    }

    public Sound getSound(String soundName) {
        return sounds.get(soundName);
    }

    public Sound removeSound(String soundName) {
        return sounds.remove(soundName);
    }

    public boolean removeSound(Sound sound) {
        Iterator<String> it = sounds.keySet().iterator();

        while (it.hasNext()) {
            String soundName = it.next();
            Sound s = sounds.get(soundName);
            if (s == sound) {
                sounds.remove(soundName);
                return true;
            }
        }

        return false;
    }

    public void playSound(String soundName) {
        Sound sound = sounds.get(soundName);
        if (sound != null) {
            sound.play();
        }
    }

    public void playFonSound(String soundName) {
        Sound sound = sounds.get(soundName);
        if (sound != null) {
            sound.loop();
        }
    }


}
