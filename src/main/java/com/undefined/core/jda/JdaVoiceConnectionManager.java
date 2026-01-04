package com.undefined.core.jda;

import com.undefined.core.player.PlayerManager;
import com.undefined.core.voice.VoiceConnectionManager;
import com.undefined.core.audio.GuildAudioService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.time.Duration;

public class JdaVoiceConnectionManager implements VoiceConnectionManager {

    private final JDA jda;
    private final long idleTimeoutMillis;
    private final PlayerManager playerManager;

    public JdaVoiceConnectionManager(JDA jda, Duration idleTimeout, PlayerManager playerManager) {
        this.jda = jda;
        this.idleTimeoutMillis = idleTimeout.toMillis();
        this.playerManager = playerManager;
    }

    @Override
    public void disconnectIfIdle(long guildId, long lastActivityMillis) {
        long now = System.currentTimeMillis();
        if (now - lastActivityMillis < idleTimeoutMillis) {
            return;
        }

        Guild guild = jda.getGuildById(guildId);
        if (guild == null) {
            return;
        }

        GuildAudioService musicManager = playerManager.getMusicManagers().get(guildId);
        if (musicManager != null) {
            var player = musicManager.getPlayer();
            if (player.getPlayingTrack() != null || !musicManager.getScheduler().getQueue().isEmpty()) {
                return;
            }
        }

        var audioManager = guild.getAudioManager();
        if (audioManager.isConnected()) {
            audioManager.closeAudioConnection();
            playerManager.getMusicManagers().remove(guildId);
        }
    }
}
