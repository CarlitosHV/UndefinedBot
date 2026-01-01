package com.undefined.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandHandler {

    private final Map<String, Command> commands = new HashMap<>();

    public CommandHandler(List<Command> commandList) {
        for (Command command : commandList) {
            commands.put(command.getName().toLowerCase(), command);
        }
    }

    public void handle(MessageReceivedEvent event, String commandName, String args) {
        Command command = commands.get(commandName.toLowerCase());
        if (command == null) {
            return;
        }
        command.execute(event, args);
    }

    public void registerCommand(Command command) {
        commands.put(command.getName().toLowerCase(), command);
    }

    public List<Command> getAllCommands() {
        return List.copyOf(commands.values());
    }

}
