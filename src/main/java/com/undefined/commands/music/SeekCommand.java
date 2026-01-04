package com.undefined.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.undefined.commands.Command;
import com.undefined.core.player.PlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SeekCommand implements Command {

    private final PlayerManager playerManager;

    public SeekCommand(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getName() {
        return "seek";
    }

    @Override
    public String getDescription() {
        return "Avanza o retrocede en la canción actual. Uso: seek <mm:ss> o seek <segundos>";
    }

    @Override
    public void execute(MessageReceivedEvent event, String args) {
        var guild = event.getGuild();
        var musicManager = playerManager.getMusicManagers().get(guild.getIdLong());

        if (musicManager == null || musicManager.getPlayer().getPlayingTrack() == null) {
            event.getChannel().sendMessage("No hay música reproduciéndose.").queue();
            return;
        }

        AudioTrack track = musicManager.getPlayer().getPlayingTrack();

        if (!track.isSeekable()) {
            event.getChannel().sendMessage("Esta canción no permite avanzar/retroceder.").queue();
            return;
        }

        if (args.isEmpty()) {
            event.getChannel().sendMessage("Debes especificar el tiempo. Ejemplo: `!seek 1:30` o `!seek 90`").queue();
            return;
        }

        try {
            long seekPosition;

            if (args.contains(":")) {
                String[] parts = args.split(":");
                int minutes = Integer.parseInt(parts[0].trim());
                int seconds = Integer.parseInt(parts[1].trim());
                seekPosition = (minutes * 60L + seconds) * 1000;
            } else {
                int seconds = Integer.parseInt(args.trim());
                seekPosition = seconds * 1000L;
            }

            if (seekPosition < 0 || seekPosition > track.getDuration()) {
                long duration = track.getDuration() / 1000;
                event.getChannel().sendMessage(String.format("El tiempo debe estar entre 0 y %02d:%02d",
                        duration / 60, duration % 60)).queue();
                return;
            }

            track.setPosition(seekPosition);

            long position = seekPosition / 1000;
            event.getChannel().sendMessage(String.format("Posición ajustada a: %02d:%02d",
                    position / 60, position % 60)).queue();

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            event.getChannel().sendMessage("Formato inválido. Usa `!seek 1:30` o `!seek 90`").queue();
        }
    }
}
