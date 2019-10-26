package pl.devtomek.app.avriplatvbox.service;

/**
 * Service for sound effects.
 *
 * @author DevTomek.pl
 */
public interface SoundService {

    void initialize();

    void mute();

    boolean isMuted();

    void playSwipe();

    void playSelect();

    void playClick();

}
