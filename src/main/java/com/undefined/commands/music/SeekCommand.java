package com.undefined.commands.music;

import com.undefined.commands.Command;
import com.undefined.core.player.PlayerManager;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.player.LavalinkPlayer;
import dev.arbjerg.lavalink.client.player.Track;
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

        if (musicManager == null) {
            event.getChannel().sendMessage("No hay música reproduciéndose.").queue();
            return;
        }

        LavalinkPlayer player = musicManager.getPlayer();
        if (player == null || player.getTrack() == null) {
            event.getChannel().sendMessage("No hay música reproduciéndose.").queue();
            return;
        }

        Track track = player.getTrack();

        if (!track.getInfo().isSeekable()) {
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

            long trackDuration = track.getInfo().getLength();
            if (seekPosition < 0 || seekPosition > trackDuration) {
                long duration = trackDuration / 1000;
                event.getChannel().sendMessage(String.format("El tiempo debe estar entre 0 y %02d:%02d",
                        duration / 60, duration % 60)).queue();
                return;
            }

            musicManager.getLink().createOrUpdatePlayer()
                    .setPosition(seekPosition)
                    .subscribe();

            long position = seekPosition / 1000;
            event.getChannel().sendMessage(String.format("Posición ajustada a: %02d:%02d",
                    position / 60, position % 60)).queue();

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            event.getChannel().sendMessage("Formato inválido. Usa `!seek 1:30` o `!seek 90`").queue();
        }
    }
}
