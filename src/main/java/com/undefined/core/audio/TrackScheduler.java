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
        player.startTrack(queue.poll(), false);
        markActivity();
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        markActivity();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        } else {
            markActivity();
        }
    }

    public BlockingQueue<AudioTrack> getQueue() {
        return queue;
    }
}
