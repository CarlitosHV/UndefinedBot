package com.undefined.core.jda;

import com.undefined.core.voice.VoiceConnectionManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.time.Duration;

public class JdaVoiceConnectionManager implements VoiceConnectionManager {

    private final JDA jda;
    private final long idleTimeoutMillis;

    public JdaVoiceConnectionManager(JDA jda, Duration idleTimeout) {
        this.jda = jda;
        this.idleTimeoutMillis = idleTimeout.toMillis();
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

        var audioManager = guild.getAudioManager();
        if (audioManager.isConnected()) {
            audioManager.closeAudioConnection();
        }
    }
}
