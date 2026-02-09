package com.undefined.core.audio;

import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.player.Track;
import dev.arbjerg.lavalink.protocol.v4.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler {
    private final Link link;
    private final BlockingQueue<Track> queue;
    private final Runnable activityCallback;
    private boolean repeating = false;
    private boolean repeatingQueue = false;
    private Track lastTrack = null;

    public TrackScheduler(Link link, Runnable activityCallback) {
        this.link = link;
        this.queue = new LinkedBlockingQueue<>();
        this.activityCallback = activityCallback;
    }

    private void markActivity() {
        if (activityCallback != null) {
            activityCallback.run();
        }
    }

    public void queue(Track track) {
        boolean isPlaying = link.getPlayer().block().getTrack() != null;

        if (!isPlaying) {
            play(track);
        } else {
            queue.offer(track);
        }
        markActivity();
    }

    public void nextTrack() {
        if (repeating && lastTrack != null) {
            play(lastTrack);
            markActivity();
            return;
        }

        Track nextTrack = queue.poll();
        if (nextTrack != null) {
            play(nextTrack);
        } else {
            stop();
        }
        markActivity();
    }

    private void play(Track track) {
        link.createOrUpdatePlayer()
                .setTrack(track)
                .subscribe();
    }

    private void stop() {
        link.createOrUpdatePlayer()
                .setTrack(null)
                .subscribe();
    }

    public void onTrackStart(Track track) {
        markActivity();
    }

    public void onTrackEnd(Track track, Message.EmittedEvent.TrackEndEvent.AudioTrackEndReason endReason) {
        this.lastTrack = track;

        if (endReason.getMayStartNext()) {
            if (repeating) {
                play(track);
                markActivity();
            } else if (repeatingQueue) {
                queue.offer(track);
                nextTrack();
            } else {
                nextTrack();
            }
        } else {
            markActivity();
        }
    }

    public BlockingQueue<Track> getQueue() {
        return queue;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public boolean isRepeatingQueue() {
        return repeatingQueue;
    }

    public void setRepeatingQueue(boolean repeatingQueue) {
        this.repeatingQueue = repeatingQueue;
    }
}
