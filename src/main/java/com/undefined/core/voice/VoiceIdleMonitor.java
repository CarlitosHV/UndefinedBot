package com.undefined.core.voice;

import com.undefined.core.audio.GuildAudioService;
import com.undefined.core.player.PlayerManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VoiceIdleMonitor {

    private final PlayerManager playerManager;
    private final VoiceConnectionManager connectionManager;
    private final ScheduledExecutorService scheduler;

    public VoiceIdleMonitor(PlayerManager playerManager,
                            VoiceConnectionManager connectionManager) {
        this.playerManager = playerManager;
        this.connectionManager = connectionManager;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::checkAllGuilds, 30, 30, TimeUnit.SECONDS);
    }

    public void stop() {
        scheduler.shutdownNow();
    }

    private void checkAllGuilds() {
        for (var entry : playerManager.getMusicManagers().entrySet()) {
            long guildId = entry.getKey();
            GuildAudioService service = entry.getValue();
            long lastActivity = service.getLastActivityTimeMillis();

            connectionManager.disconnectIfIdle(guildId, lastActivity);
        }
    }
}
