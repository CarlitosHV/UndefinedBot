package com.undefined.commands.music;

import com.undefined.commands.Command;
import com.undefined.core.player.PlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PauseCommand implements Command {

    private final PlayerManager playerManager;

    public PauseCommand(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getName() {
        return "pause";
    }

    @Override
    public String getDescription() {
        return "Pausa la música actual";
    }

    @Override
    public void execute(MessageReceivedEvent event, String args) {
        var guild = event.getGuild();
        var musicManager = playerManager.getMusicManagers().get(guild.getIdLong());

        if (musicManager == null || musicManager.getPlayer().getPlayingTrack() == null) {
            event.getChannel().sendMessage("No hay música reproduciéndose.").queue();
            return;
        }

        if (musicManager.getPlayer().isPaused()) {
            event.getChannel().sendMessage("La música ya está pausada.").queue();
            return;
        }

        musicManager.getPlayer().setPaused(true);
        event.getChannel().sendMessage("Música pausada.").queue();
    }
}
