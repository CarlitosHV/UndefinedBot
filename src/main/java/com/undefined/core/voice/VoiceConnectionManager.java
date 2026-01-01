package com.undefined.core.voice;

public interface VoiceConnectionManager {
    void disconnectIfIdle(long guildId, long lastActivityMillis);
}
