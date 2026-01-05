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
import dev.lavalink.youtube.clients.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {

    private static PlayerManager instance;

    private final AudioPlayerManager audioPlayerManager;
    private final Map<Long, GuildAudioService> musicManagers;

    private PlayerManager(BotConfiguration config) {
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        this.musicManagers = new HashMap<>();

        YoutubeAudioSourceManager youtubeSource = new YoutubeAudioSourceManager(
                true,
                new Music(),
                new Ios(),
                new AndroidMusic()
        );

        String refreshToken = config.getYoutubeRefreshToken();
        System.out.println("=== YOUTUBE OAUTH ===");
        System.out.println("Token presente: " + (refreshToken != null && !refreshToken.isEmpty()));

        if (refreshToken != null && !refreshToken.isEmpty()) {
            try {
                youtubeSource.useOauth2(refreshToken, true);
                System.out.println("OAuth2 activado");
            } catch (Exception e) {
                System.err.println("Error OAuth2: " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("=====================");

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

    public void loadAndPlay(Guild guild, TextChannel channel, String identifier, Member member) {
        AudioManager audioManager = guild.getAudioManager();

        if (!audioManager.isConnected()) {
            GuildVoiceState voiceState = member.getVoiceState();

            if (voiceState == null || !voiceState.inAudioChannel()) {
                channel.sendMessage("¡Necesitas estar en un canal de voz para reproducir música!").queue();
                return;
            }

            audioManager.openAudioConnection(voiceState.getChannel());
        }

        GuildAudioService musicManager = getGuildAudioService(guild);

        audioPlayerManager.loadItem(identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                boolean isPlaying = musicManager.getPlayer().getPlayingTrack() != null;
                musicManager.getScheduler().queue(track);

                if (isPlaying) {
                    int position = musicManager.getScheduler().getQueue().size();
                    channel.sendMessage("Agregado a la cola: **" + track.getInfo().title +
                            "** (posición " + position + ")").queue();
                } else {
                    channel.sendMessage("Reproduciendo ahora: **" + track.getInfo().title + "**").queue();
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                boolean isPlaying = musicManager.getPlayer().getPlayingTrack() != null;
                int trackCount = playlist.getTracks().size();

                for (AudioTrack track : playlist.getTracks()) {
                    musicManager.getScheduler().queue(track);
                }

                if (isPlaying) {
                    channel.sendMessage("Agregadas " + trackCount + " canciones de la playlist: **" +
                            playlist.getName() + "** a la cola").queue();
                } else {
                    channel.sendMessage("Reproduciendo playlist: **" + playlist.getName() +
                            "** (" + trackCount + " canciones)").queue();
                }
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Lo siento, no he encontrado resultados para: " + identifier).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                String errorMessage = "Lo siento, he tenido un error de carga: " + exception.getMessage();

                if (exception.getCause() != null) {
                    errorMessage += "\nCausa: " + exception.getCause().getMessage();
                }

                channel.sendMessage(errorMessage).queue();

                System.err.println("=== ERROR DE CARGA ===");
                System.err.println("Mensaje: " + exception.getMessage());
                System.err.println("Severity: " + exception.severity);
                exception.printStackTrace();
                System.err.println("======================");
            }

        });
    }

    public Map<Long, GuildAudioService> getMusicManagers() {
        return musicManagers;
    }
}
