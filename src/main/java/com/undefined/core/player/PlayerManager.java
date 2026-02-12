package com.undefined.core.player;

import com.undefined.config.BotConfiguration;
import com.undefined.core.audio.GuildAudioService;
import dev.arbjerg.lavalink.client.*;
import dev.arbjerg.lavalink.client.loadbalancing.builtin.VoiceRegionPenaltyProvider;
import dev.arbjerg.lavalink.client.player.*;
import dev.arbjerg.lavalink.client.player.Track;
import dev.arbjerg.lavalink.protocol.v4.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

import java.lang.Exception;
import java.net.URI;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class PlayerManager {

    private static PlayerManager instance;

    private final LavalinkClient lavalinkClient;
    private final Map<Long, GuildAudioService> musicManagers;

    private PlayerManager(BotConfiguration config) {
        final String lavalinkHost = System.getenv().get("LAVALINK_HOST");
        final String lavalinkPort = System.getenv().get("LAVALINK_PORT");
        final String lavapassword = System.getenv().get("LAVALINK_PASSWORD");

        this.musicManagers = new HashMap<>();

        this.lavalinkClient = new LavalinkClient(
                getUserIdFromConfig(config)
        );

        this.lavalinkClient.getLoadBalancer().addPenaltyProvider(new VoiceRegionPenaltyProvider());

        this.lavalinkClient.addNode(new NodeOptions.Builder()
                .setName("main-node")
                .setServerUri(URI.create("http://" + lavalinkHost + ":" + lavalinkPort))
                .setPassword(lavapassword)
                .build()
        );

        this.lavalinkClient.on(dev.arbjerg.lavalink.client.event.TrackStartEvent.class).subscribe(event -> {
            GuildAudioService service = musicManagers.get(event.getGuildId());
            if (service != null) {
                service.getScheduler().onTrackStart(event.getTrack());
            }
        });

        this.lavalinkClient.on(dev.arbjerg.lavalink.client.event.TrackEndEvent.class).subscribe(event -> {
            GuildAudioService service = musicManagers.get(event.getGuildId());
            if (service != null) {
                service.getScheduler().onTrackEnd(event.getTrack(), event.getEndReason());
            }
        });
    }

    private long getUserIdFromConfig(BotConfiguration config) {
        try {
            String token = config.getDiscordToken();
            String[] parts = token.split("\\.");
            String idString = new String(Base64.getDecoder().decode(parts[0]));
            return Long.parseLong(idString);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo extraer el ID del bot desde el token. Verifica tu configuración.", e);
        }
    }

    public static synchronized PlayerManager getInstance(BotConfiguration config) {
        if (instance == null) {
            instance = new PlayerManager(config);
        }
        return instance;
    }

    public synchronized GuildAudioService getGuildAudioService(Guild guild) {
        return musicManagers.computeIfAbsent(guild.getIdLong(), id -> {
            GuildAudioService service = new GuildAudioService(lavalinkClient, guild.getIdLong());
            guild.getAudioManager().setSendingHandler(service.getSendHandler());
            return service;
        });
    }

    public void loadAndPlay(Guild guild, TextChannel channel, String identifier, Member member) {
        GuildVoiceState voiceState = member.getVoiceState();
        if (voiceState == null || !voiceState.inAudioChannel()) {
            channel.sendMessage("¡Necesitas estar en un canal de voz para reproducir música!").queue();
            return;
        }

        AudioChannel voiceChannel = voiceState.getChannel();
        guild.getAudioManager().openAudioConnection(voiceChannel);

        GuildAudioService musicManager = getGuildAudioService(guild);
        Link link = lavalinkClient.getOrCreateLink(guild.getIdLong());

        link.loadItem(identifier).subscribe(itemLoadResult -> {
            if (itemLoadResult instanceof TrackLoaded) {
                TrackLoaded loaded = (TrackLoaded) itemLoadResult;
                handleTrackLoaded(channel, musicManager, loaded.getTrack());
            } else if (itemLoadResult instanceof PlaylistLoaded) {
                PlaylistLoaded loaded = (PlaylistLoaded) itemLoadResult;
                handlePlaylistLoaded(channel, musicManager, loaded);
            } else if (itemLoadResult instanceof SearchResult) {
                SearchResult search = (SearchResult) itemLoadResult;
                if (!search.getTracks().isEmpty()) {
                    handleTrackLoaded(channel, musicManager, search.getTracks().get(0));
                } else {
                    channel.sendMessage("Lo siento, no he encontrado resultados para: " + identifier).queue();
                }
            } else if (itemLoadResult instanceof NoMatches) {
                channel.sendMessage("Lo siento, no he encontrado resultados para: " + identifier).queue();
            } else if (itemLoadResult instanceof LoadFailed) {
                LoadFailed failed = (LoadFailed) itemLoadResult;
                channel.sendMessage("Error de carga: " + failed.getException().getMessage()).queue();
            }
        });
    }

    private void handleTrackLoaded(TextChannel channel, GuildAudioService musicManager, Track track) {
        musicManager.getScheduler().queue(track);

        int position = musicManager.getScheduler().getQueue().size();
        if (position > 0) {
            channel.sendMessage("Agregado a la cola: **" + track.getInfo().getTitle() + "** (posición " + position + ")").queue();
        } else {
            channel.sendMessage("Reproduciendo ahora: **" + track.getInfo().getTitle() + "**").queue();
        }
    }

    private void handlePlaylistLoaded(TextChannel channel, GuildAudioService musicManager, PlaylistLoaded playlist) {
        int trackCount = playlist.getTracks().size();

        for (Track track : playlist.getTracks()) {
            musicManager.getScheduler().queue(track);
        }

        channel.sendMessage("Reproduciendo playlist: **" + playlist.getInfo().getName() + "** (" + trackCount + " canciones)").queue();
    }

    public Map<Long, GuildAudioService> getMusicManagers() {
        return musicManagers;
    }

    public LavalinkClient getLavalinkClient() {
        return lavalinkClient;
    }
}
