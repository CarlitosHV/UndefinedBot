package com.undefined.commands.music;

import com.undefined.commands.Command;
import com.undefined.core.player.PlayerManager;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.LinkState;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class StopCommand implements Command {

    private final PlayerManager playerManager;

    public StopCommand(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return "Para la música y limpia la cola de reproducción.";
    }

    @Override
    public void execute(MessageReceivedEvent event, String args) {
        var guild = event.getGuild();
        Link link = playerManager.getLavalinkClient().getOrCreateLink(guild.getIdLong());

        if (link.getState() != LinkState.CONNECTED) {
            event.getChannel().sendMessage("No hay música reproduciéndose.").queue();
            return;
        }

        var musicManager = playerManager.getMusicManagers().get(guild.getIdLong());
        if (musicManager != null) {
            musicManager.getScheduler().getQueue().clear();
        }

        link.createOrUpdatePlayer()
                .setTrack(null)
                .subscribe();

        event.getChannel().sendMessage("Música detenida y cola limpiada.").queue();
    }
}
