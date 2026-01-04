package com.undefined.commands.music;

import com.undefined.commands.Command;
import com.undefined.core.player.PlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class VolumeCommand implements Command {

    private final PlayerManager playerManager;

    public VolumeCommand(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getName() {
        return "volume";
    }

    @Override
    public String getDescription() {
        return "Ajusta el volumen de la música (0-100). Uso: volume <número>";
    }

    @Override
    public void execute(MessageReceivedEvent event, String args) {
        var guild = event.getGuild();
        var musicManager = playerManager.getMusicManagers().get(guild.getIdLong());

        if (musicManager == null) {
            event.getChannel().sendMessage("No hay música reproduciéndose.").queue();
            return;
        }

        if (args.isEmpty()) {
            int currentVolume = musicManager.getPlayer().getVolume();
            event.getChannel().sendMessage("🔊 Volumen actual: " + currentVolume + "%").queue();
            return;
        }

        try {
            int volume = Integer.parseInt(args.trim());

            if (volume < 0 || volume > 100) {
                event.getChannel().sendMessage("El volumen debe estar entre 0 y 100.").queue();
                return;
            }

            musicManager.getPlayer().setVolume(volume);

            event.getChannel().sendMessage(" Volumen ajustado a: " + volume + "%").queue();

        } catch (NumberFormatException e) {
            event.getChannel().sendMessage("Por favor proporciona un número válido entre 0 y 100.").queue();
        }
    }
}
