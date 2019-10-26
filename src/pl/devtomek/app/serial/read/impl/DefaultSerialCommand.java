package pl.devtomek.app.serial.read.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.devtomek.app.serial.read.Command;
import pl.devtomek.app.serial.read.SerialCommand;
import pl.devtomek.app.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Default implementation of {@link SerialCommand}.
 *
 * @author DevTomek.pl
 */
public class DefaultSerialCommand implements SerialCommand {

    private final Logger LOG = LogManager.getLogger(DefaultSerialCommand.class);

    private HashMap<String, List<Command>> commandMap;

    public DefaultSerialCommand() {
        commandMap = new HashMap<>();
    }

    @Override
    public void register(String commandName, Command... commands) {
        List<Command> commandList = commandMap.get(commandName);

        if (commandList != null) {
            commandList.addAll(Arrays.asList(commands));
        } else {
            commandMap.put(commandName, new ArrayList<>(Arrays.asList(commands)));
        }
    }

    @Override
    public void execute(String commandName) {
        List<Command> commandList = commandMap.get(commandName);

        if (CollectionUtils.isEmpty(commandList)) {
            LOG.error("No command registered for [{}]", commandName);
            return;
        }

        commandList.forEach(Command::execute);
    }

}