package com.undefined.commands.music;

import com.undefined.commands.Command;
import com.undefined.core.player.PlayerManager;
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
        var service = playerManager.getGuildAudioService(guild);

        service.getPlayer().stopTrack();
        service.getScheduler().getQueue().clear();

        event.getChannel().sendMessage("Música detenida.").queue();
    }
}
