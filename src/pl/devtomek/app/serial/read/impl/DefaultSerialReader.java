package pl.devtomek.app.serial.read.impl;

import gnu.io.SerialPortEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.devtomek.app.serial.read.SerialCommand;
import pl.devtomek.app.serial.read.SerialReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Default implementation of {@link SerialReader}.
 *
 * @author DevTomek.pl
 */
public class DefaultSerialReader implements SerialReader {

    private final Logger LOG = LogManager.getLogger(DefaultSerialReader.class);

    private SerialCommand serialReaderCommand;
    private BufferedReader bufferedReader;

    public DefaultSerialReader(SerialCommand serialReaderCommand) {
        this.serialReaderCommand = serialReaderCommand;
    }

    @Override
    public void setInputStream(InputStream inputStream) {
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {

        if (serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {

            try {

                if (bufferedReader.ready()) {
                    final String receivedCommandName = bufferedReader.readLine().trim();
                    serialReaderCommand.execute(receivedCommandName);
                    LOG.info("Received: [{}]", receivedCommandName);
                }

            } catch (IOException e) {
                LOG.error("Can not open input stream [{}]", e.getMessage());
            }
        }

    }
}

