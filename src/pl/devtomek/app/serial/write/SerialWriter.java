package pl.devtomek.app.serial.write;

import java.io.OutputStream;

/**
 * Interface for Serial Writer functionality.
 *
 * @author DevTomek.pl
 */
public interface SerialWriter {

    void setOutputStream(OutputStream outputStream);

    void send(String message);

}
