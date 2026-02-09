package com.undefined.core.audio;

import net.dv8tion.jda.api.audio.AudioSendHandler;
import java.nio.ByteBuffer;

public class AudioPlayerSendHandler implements AudioSendHandler {

    public AudioPlayerSendHandler() {
    }

    @Override
    public boolean canProvide() {
        return false;
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return null;
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
