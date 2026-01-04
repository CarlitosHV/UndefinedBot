package com.undefined.commands.music;

import com.undefined.commands.Command;
import com.undefined.core.player.PlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PlayCommand implements Command {

    private final PlayerManager playerManager;

    public PlayCommand(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Reproduce música de YouTube o Spotify. Uso: !play <url o nombre>";
    }

    @Override
    public void execute(MessageReceivedEvent event, String args) {
        if (args.isEmpty()) {
            event.getChannel().sendMessage("Debes proporcionar una URL o nombre de canción.").queue();
            return;
        }

        var guild = event.getGuild();
        var channel = event.getChannel().asTextChannel();

        playerManager.loadAndPlay(guild, channel, args);
    }
}
