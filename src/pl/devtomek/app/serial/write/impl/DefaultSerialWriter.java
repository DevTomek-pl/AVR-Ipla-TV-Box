package pl.devtomek.app.serial.write.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.devtomek.app.serial.write.SerialWriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Default implementation of {@link SerialWriter}.
 *
 * @author DevTomek.pl
 */
public class DefaultSerialWriter implements SerialWriter {

    private final Logger LOG = LogManager.getLogger(DefaultSerialWriter.class);

    private BufferedWriter bufferedWriter;

    @Override
    public void setOutputStream(OutputStream inputStream) {
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(inputStream));
    }

    @Override
    public void send(String message) {
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            LOG.info("Sent: [{}]", message);
        } catch (IOException e) {
            LOG.error("Can not open output stream [{}]", e.getMessage());
        }
    }

}
