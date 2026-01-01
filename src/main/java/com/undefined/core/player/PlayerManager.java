package com.undefined.core.player;

import com.github.topi314.lavasrc.spotify.SpotifySourceManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.undefined.config.BotConfiguration;
import com.undefined.core.audio.GuildAudioService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

public class PlayerManager {

    private static PlayerManager instance;

    private final AudioPlayerManager audioPlayerManager;
    private final Map<Long, GuildAudioService> musicManagers;

    private PlayerManager(BotConfiguration config) {
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        this.musicManagers = new HashMap<>();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);

        SpotifySourceManager spotify = new SpotifySourceManager(
                new String[]{"ytsearch:\"%ISRC%\"", "ytsearch:%QUERY%"},
                config.getSpotifyClientId(),
                config.getSpotifyClientSecret(),
                "MX",
                audioPlayerManager
        );
        this.audioPlayerManager.registerSourceManager(spotify);
    }

    public static PlayerManager getInstance(BotConfiguration config) {
        if (instance == null) {
            instance = new PlayerManager(config);
        }
        return instance;
    }

    public GuildAudioService getGuildAudioService(Guild guild) {
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
            }
        });
    }

    public Map<Long, GuildAudioService> getMusicManagers() {
        return musicManagers;
    }

}
