package com.undefined.commands.music;

import com.undefined.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class LeaveCommand implements Command {

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
        var audioManager = guild.getAudioManager();

        if (!audioManager.isConnected()) {
            event.getChannel().sendMessage("No estoy conectado a ningún canal de voz.").queue();
            return;
        }

        audioManager.closeAudioConnection();
        event.getChannel().sendMessage("Desconectado del canal de voz.").queue();
    }
}
