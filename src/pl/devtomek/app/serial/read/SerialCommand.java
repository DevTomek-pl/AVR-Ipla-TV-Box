package pl.devtomek.app.serial.read;

/**
 * Core interface for Serial Command Pattern.
 *
 * @author DevTomek.pl
 */
public interface SerialCommand {

    void register(String commandName, Command... commands);

    void execute(String commandName);

}
