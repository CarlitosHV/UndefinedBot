package com.undefined.commands.music;

import com.undefined.commands.Command;
import com.undefined.core.player.PlayerManager;
import dev.arbjerg.lavalink.client.player.LavalinkPlayer;
import dev.arbjerg.lavalink.client.player.Track;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.Color;

public class NowPlayingCommand implements Command {

    private final PlayerManager playerManager;

    public NowPlayingCommand(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getName() {
        return "nowplaying";
    }

    @Override
    public String getDescription() {
        return "Muestra la canción que se está reproduciendo actualmente";
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

        long position = player.getPosition() / 1000;
        long duration = track.getInfo().getLength() / 1000;

        int progressBarLength = 20;
        int progress = (duration > 0)
                ? (int) ((double) position / duration * progressBarLength)
                : 0;

        StringBuilder progressBar = new StringBuilder();
        for (int i = 0; i < progressBarLength; i++) {
            if (i == progress) {
                progressBar.append("🔘");
            } else {
                progressBar.append("▬");
            }
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle("Reproduciendo ahora")
                .addField("Título", track.getInfo().getTitle(), false)
                .addField("Autor", track.getInfo().getAuthor(), true)
                .addField("Duración", String.format("%02d:%02d / %02d:%02d",
                        position / 60, position % 60,
                        duration / 60, duration % 60), true)
                .addField("Progreso", progressBar.toString(), false)
                .setFooter("Solicitado por " + event.getAuthor().getName());

        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }
}
