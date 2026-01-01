package com.undefined.commands.basics;

import com.undefined.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PingCommand implements Command {

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return "Valida si el bot está activo";
    }

    @Override
    public void execute(MessageReceivedEvent event, String args) {
        event.getChannel().sendMessage("pong").queue();
    }
}
