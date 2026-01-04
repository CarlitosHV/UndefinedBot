package com.undefined.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.undefined.commands.Command;
import com.undefined.core.player.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class QueueCommand implements Command {

    private final PlayerManager playerManager;

    public QueueCommand(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getName() {
        return "queue";
    }

    @Override
    public String getDescription() {
        return "Muestra la cola de reproducción actual";
    }

    @Override
    public void execute(MessageReceivedEvent event, String args) {
        var guild = event.getGuild();
        var musicManager = playerManager.getMusicManagers().get(guild.getIdLong());

        if (musicManager == null) {
            event.getChannel().sendMessage("No hay música en cola.").queue();
            return;
        }

        AudioTrack currentTrack = musicManager.getPlayer().getPlayingTrack();
        BlockingQueue<AudioTrack> queue = musicManager.getScheduler().getQueue();

        if (currentTrack == null && queue.isEmpty()) {
            event.getChannel().sendMessage("No hay música en cola.").queue();
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.CYAN)
                .setTitle("Cola de reproducción");

        if (currentTrack != null) {
            long position = currentTrack.getPosition() / 1000;
            long duration = currentTrack.getDuration() / 1000;
            embed.addField("Reproduciendo ahora",
                    String.format("**%s**\n`%02d:%02d / %02d:%02d`",
                            currentTrack.getInfo().title,
                            position / 60, position % 60,
                            duration / 60, duration % 60),
                    false);
        }

        if (!queue.isEmpty()) {
            List<AudioTrack> trackList = new ArrayList<>(queue);
            StringBuilder queueText = new StringBuilder();

            int max = Math.min(10, trackList.size());
            for (int i = 0; i < max; i++) {
                AudioTrack track = trackList.get(i);
                long duration = track.getDuration() / 1000;
                queueText.append(String.format("`%d.` **%s** `[%02d:%02d]`\n",
                        i + 1,
                        track.getInfo().title,
                        duration / 60, duration % 60));
            }

            if (trackList.size() > 10) {
                queueText.append(String.format("\n*...y %d canciones más*", trackList.size() - 10));
            }

            embed.addField(String.format("Próximas canciones (%d total)", trackList.size()),
                    queueText.toString(), false);
        }

        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }
}
