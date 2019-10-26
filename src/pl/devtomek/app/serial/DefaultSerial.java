package pl.devtomek.app.serial;

import gnu.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.devtomek.app.serial.read.Command;
import pl.devtomek.app.serial.read.SerialCommand;
import pl.devtomek.app.serial.read.SerialReader;
import pl.devtomek.app.serial.write.SerialWriter;
import pl.devtomek.app.utils.PropertiesUtils;
import pl.devtomek.app.utils.constant.Property;

import java.io.IOException;
import java.util.TooManyListenersException;

/**
 * Default implementation of {@link Serial}.
 *
 * @author DevTomek.pl
 */
public class DefaultSerial implements Serial {

    private final Logger LOG = LogManager.getLogger(DefaultSerial.class);

    private static final String DEFAULT_PORT = "/dev/ttyUSB0";
    private static final int DEFAULT_BAUD_RATE = 9600;
    private static final int DATA_BITS = 8;
    private static final int STOP_BITS = 1;
    private static final int PARITY = 0;
    private static final int TIMEOUT = 5000;

    private SerialWriter serialWriter;
    private SerialReader serialReader;
    private SerialCommand serialReaderCommand;

    private SerialPort serialPort;
    private boolean isConnected;

    public DefaultSerial(SerialWriter serialWriter, SerialReader serialReader, SerialCommand serialReaderCommand) {
        this.serialWriter = serialWriter;
        this.serialReader = serialReader;
        this.serialReaderCommand = serialReaderCommand;
    }

    @Override
    public void initialize() {
        final String portName = PropertiesUtils.getProperty(Property.SERIAL_PORT, DEFAULT_PORT);
        final int baudRate = PropertiesUtils.getIntegerProperty(Property.SERIAL_BAUD_RATE, DEFAULT_BAUD_RATE);

        try {
            LOG.info("Serial connection started [{}]...", portName);
            LOG.info("BaudRate=[{}], DataBits=[{}], Parity=[{}], StopBits=[{}], Timeout=[{}]",
                    baudRate, DATA_BITS, PARITY, STOP_BITS, TIMEOUT);

            final CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);

            if (portIdentifier.isCurrentlyOwned()) {
                throw new PortInUseException();
            }

            LOG.info("Waiting for AVR communication...");
            Thread.sleep(2000); // quick fix for Arduino Serial lib

            final CommPort commPort = portIdentifier.open(getClass().getName(), TIMEOUT);

            if (commPort instanceof SerialPort) {
                serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(baudRate, DATA_BITS, STOP_BITS, PARITY);
                serialPort.addEventListener(serialReader);
                serialPort.notifyOnDataAvailable(true);
                serialReader.setInputStream(serialPort.getInputStream());
                serialWriter.setOutputStream(serialPort.getOutputStream());
                isConnected = true;
                LOG.info("Serial port [{}] is ready to use", portName);
            } else {
                LOG.error("Only serial ports can be handled");
            }

        } catch (IOException e) {
            LOG.error("Device not found on port name: [{}] [{}]", portName, e.getMessage());
        } catch (UnsupportedCommOperationException e) {
            LOG.error("Unsupported comm operation exception [{}]", e.getMessage());
        } catch (NoSuchPortException e) {
            LOG.error("No such port exception [{}]", portName);
        } catch (PortInUseException e) {
            LOG.error("Port in use exception [{}]", e.getMessage());
        } catch (TooManyListenersException e) {
            LOG.error("Too many listeners exception for port [{}]", portName);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void reinitialize() {
        if (isConnected) {
            LOG.error("Can not reinitialize, because the connection is already open");
        } else {
            initialize();
        }
    }

    @Override
    public void register(String commandName, Command... commands) {
        serialReaderCommand.register(commandName, commands);
    }

    @Override
    public void send(String message) {
        serialWriter.send(message);
    }

    @Override
    public void close() {
        LOG.info("Serial communication stopping... [{}]", serialPort != null ? serialPort.getName() : "null");

        if (serialPort != null) {
            serialPort.close();
        }

        isConnected = false;
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

}
