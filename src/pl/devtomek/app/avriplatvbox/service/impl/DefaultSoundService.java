package pl.devtomek.app.avriplatvbox.service.impl;

import pl.devtomek.app.avriplatvbox.service.SoundService;

import java.applet.Applet;
import java.applet.AudioClip;

/**
 * Default implementation of {@link SoundService}.
 *
 * @author DevTomek.pl
 */
public class DefaultSoundService implements SoundService {

    private boolean isMute;
    private AudioClip swipeAudioClip;
    private AudioClip selectAudioClip;
    private AudioClip clickAudioClip;

    @Override
    public void initialize() {
        isMute = false;
        swipeAudioClip = Applet.newAudioClip(getClass().getClassLoader().getResource("sound/button-46.wav"));
        selectAudioClip = Applet.newAudioClip(getClass().getClassLoader().getResource("sound/button-50.wav"));
        clickAudioClip = Applet.newAudioClip(getClass().getClassLoader().getResource("sound/beep-21.wav"));
    }

    @Override
    public void mute() {
        isMute = !isMute;
    }

    @Override
    public boolean isMuted() {
        return isMute;
    }

    @Override
    public void playSwipe() {
        if (!isMute) {
            swipeAudioClip.play();
        }
    }

    @Override
    public void playSelect() {
        if (!isMute) {
            selectAudioClip.play();
        }
    }

    @Override
    public void playClick() {
        if (!isMute) {
            clickAudioClip.play();
        }
    }

}
