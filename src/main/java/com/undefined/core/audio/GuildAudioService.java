package com.undefined.core.audio;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.player.LavalinkPlayer;

public class GuildAudioService {
    private final Link link;
    private final TrackScheduler scheduler;
    private final AudioPlayerSendHandler sendHandler;

    private volatile long lastActivityTimeMillis;

    public GuildAudioService(LavalinkClient lavalinkClient, long guildId) {
        this.link = lavalinkClient.getOrCreateLink(guildId);
        this.lastActivityTimeMillis = System.currentTimeMillis();

        this.scheduler = new TrackScheduler(this.link, this::updateActivity);

        this.sendHandler = new AudioPlayerSendHandler();
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

    public LavalinkPlayer getPlayer() {
        return link.getPlayer().block();
    }

    public Link getLink() {
        return link;
    }
}
