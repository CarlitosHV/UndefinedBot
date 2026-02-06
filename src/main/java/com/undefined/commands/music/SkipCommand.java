package com.undefined.commands.music;

import com.undefined.commands.Command;
import com.undefined.core.player.PlayerManager;
import dev.arbjerg.lavalink.client.player.LavalinkPlayer;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SkipCommand implements Command {

    private final PlayerManager playerManager;

    public SkipCommand(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getDescription() {
        return "Salta a la siguiente canción en la cola";
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

        musicManager.getScheduler().nextTrack();
        event.getChannel().sendMessage("Canción saltada.").queue();
    }
}
