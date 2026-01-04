package com.undefined.core.player;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.undefined.config.BotConfiguration;
import com.undefined.core.audio.GuildAudioService;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {

    private static PlayerManager instance;

    private final AudioPlayerManager audioPlayerManager;
    private final Map<Long, GuildAudioService> musicManagers;

    private PlayerManager(BotConfiguration config) {
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        this.musicManagers = new HashMap<>();

        YoutubeAudioSourceManager youtubeSource = new YoutubeAudioSourceManager();
        this.audioPlayerManager.registerSourceManager(youtubeSource);

        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public static synchronized PlayerManager getInstance(BotConfiguration config) {
        if (instance == null) {
            instance = new PlayerManager(config);
        }
        return instance;
    }

    public synchronized GuildAudioService getGuildAudioService(Guild guild) {
        return musicManagers.computeIfAbsent(guild.getIdLong(), id -> {
            GuildAudioService service = new GuildAudioService(audioPlayerManager);
            guild.getAudioManager().setSendingHandler(service.getSendHandler());
            return service;
        });
    }

    public void loadAndPlay(Guild guild, TextChannel channel, String identifier) {
        GuildAudioService musicManager = getGuildAudioService(guild);
        audioPlayerManager.loadItem(identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.getScheduler().queue(track);
                channel.sendMessage("Estoy reproduciendo: " + track.getInfo().title).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (AudioTrack track : playlist.getTracks()) {
                    musicManager.getScheduler().queue(track);
                }
                channel.sendMessage("Voy a reproducir la playlist: " + playlist.getName()
                        + " (" + playlist.getTracks().size() + " canciones)").queue();
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Lo siento, no he encontrado resultados para: " + identifier).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("Lo siento, he tenido un error de carga: " + exception.getMessage()).queue();
                exception.printStackTrace();
            }
        });
    }

    public Map<Long, GuildAudioService> getMusicManagers() {
        return musicManagers;
    }
}
