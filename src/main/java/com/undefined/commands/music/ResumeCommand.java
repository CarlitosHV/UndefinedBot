package com.undefined.commands.music;

import com.undefined.commands.Command;
import com.undefined.core.player.PlayerManager;
import dev.arbjerg.lavalink.client.player.LavalinkPlayer;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ResumeCommand implements Command {

    private final PlayerManager playerManager;

    public ResumeCommand(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getName() {
        return "resume";
    }

    @Override
    public String getDescription() {
        return "Reanuda la música pausada";
    }

    @Override
    public void execute(MessageReceivedEvent event, String args) {
        var guild = event.getGuild();
        var musicManager = playerManager.getMusicManagers().get(guild.getIdLong());

        if (musicManager == null) {
            event.getChannel().sendMessage("No hay música para reanudar.").queue();
            return;
        }

        LavalinkPlayer player = musicManager.getPlayer();
        if (player == null || player.getTrack() == null) {
            event.getChannel().sendMessage("No hay música para reanudar.").queue();
            return;
        }

        if (!player.getPaused()) {
            event.getChannel().sendMessage("La música no está pausada.").queue();
            return;
        }

        musicManager.getLink().createOrUpdatePlayer()
                .setPaused(false)
                .subscribe();

        event.getChannel().sendMessage("Música reanudada.").queue();
    }
}
