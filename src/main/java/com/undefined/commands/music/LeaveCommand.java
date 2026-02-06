package com.undefined.commands.music;

import com.undefined.commands.Command;
import com.undefined.core.player.PlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

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
        AudioManager audioManager = guild.getAudioManager();

        if (!audioManager.isConnected()) {
            event.getChannel().sendMessage("No estoy conectado a ningún canal de voz.").queue();
            return;
        }

        playerManager.getLavalinkClient().getOrCreateLink(guild.getIdLong())
                .destroy()
                .subscribe();

        audioManager.closeAudioConnection();

        event.getChannel().sendMessage("Desconectado del canal de voz.").queue();
    }
}
