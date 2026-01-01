package com.undefined.events;

import com.undefined.commands.CommandHandler;
import com.undefined.config.BotConfiguration;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {

    private final BotConfiguration config;
    private final CommandHandler commandHandler;

    public MessageListener(BotConfiguration config, CommandHandler commandHandler) {
        this.config = config;
        this.commandHandler = commandHandler;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        String prefix = config.getCommandPrefix();
        String content = event.getMessage().getContentRaw();

        if (!content.startsWith(prefix)) {
            return;
        }

        String withoutPrefix = content.substring(prefix.length()).trim();
        if (withoutPrefix.isEmpty()) {
            return;
        }

        String[] parts = withoutPrefix.split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1] : "";

        commandHandler.handle(event, command, args);
    }
}
