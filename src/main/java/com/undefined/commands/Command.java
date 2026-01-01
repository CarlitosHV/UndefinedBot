package com.undefined.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface Command {
    String getName();
    String getDescription();
    void execute(MessageReceivedEvent event, String args);
}


