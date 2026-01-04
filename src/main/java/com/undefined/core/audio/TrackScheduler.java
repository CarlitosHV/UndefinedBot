package com.undefined.core.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private final Runnable activityCallback;
    private boolean repeating = false;
    private boolean repeatingQueue = false;
    private AudioTrack lastTrack = null;

    public TrackScheduler(AudioPlayer player, Runnable activityCallback) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        this.activityCallback = activityCallback;
    }

    private void markActivity() {
        if (activityCallback != null) {
            activityCallback.run();
        }
    }

    public void queue(AudioTrack track) {
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
        markActivity();
    }

    public void nextTrack() {
        if (repeating && lastTrack != null) {
            player.startTrack(lastTrack.makeClone(), false);
            markActivity();
            return;
        }

        AudioTrack nextTrack = queue.poll();
        if (nextTrack != null) {
            player.startTrack(nextTrack, false);
        } else {
            player.stopTrack();
        }
        markActivity();
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        markActivity();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        this.lastTrack = track;

        if (endReason.mayStartNext) {
            if (repeating) {
                player.startTrack(track.makeClone(), false);
                markActivity();
            } else if (repeatingQueue) {
                queue.offer(track.makeClone());
                nextTrack();
            } else {
                nextTrack();
            }
        } else {
            markActivity();
        }
    }

    public BlockingQueue<AudioTrack> getQueue() {
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
