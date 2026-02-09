package com.undefined.commands.music;

import com.undefined.commands.Command;
import com.undefined.core.player.PlayerManager;
import dev.arbjerg.lavalink.client.player.LavalinkPlayer;
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

        if (musicManager == null) {
            event.getChannel().sendMessage("No hay música reproduciéndose.").queue();
            return;
        }

        LavalinkPlayer player = musicManager.getPlayer();
        if (player == null || player.getTrack() == null) {
            event.getChannel().sendMessage("No hay música reproduciéndose.").queue();
            return;
        }

        if (player.getPaused()) {
            event.getChannel().sendMessage("La música ya está pausada.").queue();
            return;
        }

        musicManager.getLink().createOrUpdatePlayer()
                .setPaused(true)
                .subscribe();

        event.getChannel().sendMessage("Música pausada.").queue();
    }
}
