package com.undefined.config;

public class BotConfiguration {
    private final String discordToken;
    private final String spotifyClientId;
    private final String spotifyClientSecret;
    private final String commandPrefix;
    private final int idleTimeoutMinutes;

    public BotConfiguration() {
        this.discordToken = System.getenv("DISCORD_TOKEN");
        this.spotifyClientId = System.getenv("SPOTIFY_CLIENT_ID");
        this.spotifyClientSecret = System.getenv("SPOTIFY_CLIENT_SECRET");
        this.commandPrefix = System.getenv().getOrDefault("COMMAND_PREFIX", "!");

        String idleEnv = System.getenv("TIMEOUT_MINUTES");
        int idleMinutes;
        try {
            idleMinutes = idleEnv != null ? Integer.parseInt(idleEnv) : 2;
        } catch (NumberFormatException ex) {
            idleMinutes = 2;
        }
        this.idleTimeoutMinutes = idleMinutes;
    }

    public String getDiscordToken() {
        return discordToken;
    }

    public String getSpotifyClientId() {
        return spotifyClientId;
    }

    public String getSpotifyClientSecret() {
        return spotifyClientSecret;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }

    public int getIdleTimeoutMinutes() {
        return idleTimeoutMinutes;
    }

    public boolean isValid() {
        return discordToken != null && !discordToken.isEmpty() &&
                spotifyClientId != null && !spotifyClientId.isEmpty() &&
                spotifyClientSecret != null && !spotifyClientSecret.isEmpty();
    }
}
