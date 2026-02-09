package com.undefined.commands.music;

import com.undefined.commands.Command;
import com.undefined.core.player.PlayerManager;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.LinkState;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class LeaveCommand implements Command {

    private final PlayerManager playerManager;

    public LeaveCommand(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getDescription() {
        return "Permite que el usuario expulse manualmente el bot del canal de voz.";
    }

    @Override
    public void execute(MessageReceivedEvent event, String args) {
        var guild = event.getGuild();
        Link link = playerManager.getLavalinkClient().getOrCreateLink(guild.getIdLong());

        if (link.getState() != LinkState.CONNECTED) {
            event.getChannel().sendMessage("No estoy conectado a ningún canal de voz.").queue();
            return;
        }

        link.destroy().subscribe();
        guild.getAudioManager().closeAudioConnection();

        event.getChannel().sendMessage("Desconectado del canal de voz.").queue();
    }
}
