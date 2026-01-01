package com.undefined.core.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class GuildAudioService {
    private final AudioPlayer player;
    private final TrackScheduler scheduler;
    private final AudioPlayerSendHandler sendHandler;

    private volatile long lastActivityTimeMillis;

    public GuildAudioService(AudioPlayerManager manager) {
        this.player = manager.createPlayer();
        this.lastActivityTimeMillis = System.currentTimeMillis();

        this.scheduler = new TrackScheduler(this.player, this::updateActivity);
        this.player.addListener(this.scheduler);

        this.sendHandler = new AudioPlayerSendHandler(this.player);
    }

    private void updateActivity() {
        lastActivityTimeMillis = System.currentTimeMillis();
    }

    public long getLastActivityTimeMillis() {
        return lastActivityTimeMillis;
    }

    public AudioPlayerSendHandler getSendHandler() {
        return sendHandler;
    }

    public TrackScheduler getScheduler() {
        return scheduler;
    }

    public AudioPlayer getPlayer() {
        return player;
    }
}
