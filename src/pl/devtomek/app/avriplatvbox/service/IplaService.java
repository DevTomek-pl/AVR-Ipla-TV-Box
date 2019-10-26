package pl.devtomek.app.avriplatvbox.service;

import pl.devtomek.app.avriplatvbox.service.constant.MaterialIcon;

/**
 * Service responsible for controlling Ipla TV player and website.
 *
 * @author DevTomek.pl
 */
public interface IplaService {

    void initialize();

    void login();

    void close();

    void loadChannel(String url);

    void displayInfo();

    void play();

    void pause();

    void fullscreen();

    void goToNow();

    void rewind();

    void forward();

    void fastRewind();

    void fastForward();

    void incrementBrightness();

    void decrementBrightness();

    void resetBrightness();

    void displayMessage(String message);

    void displayIconMessage(MaterialIcon icon);

    void changeResolution();

}
