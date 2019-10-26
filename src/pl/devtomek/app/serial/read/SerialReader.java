package pl.devtomek.app.serial.read;

import gnu.io.SerialPortEventListener;

import java.io.InputStream;

/**
 * Interface for Serial Reader functionality.
 *
 * @author DevTomek.pl
 */
public interface SerialReader extends SerialPortEventListener {

    void setInputStream(InputStream inputStream);

}
