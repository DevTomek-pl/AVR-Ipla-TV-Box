package pl.devtomek.app.serial;

import pl.devtomek.app.serial.read.Command;

/**
 * Interface for Serial communication.
 *
 * @author DevTomek.pl
 */
public interface Serial {

    void initialize();

    void reinitialize();

    void register(String commandName, Command... commands);

    boolean isConnected();

    void send(String message);

    void close();

}
