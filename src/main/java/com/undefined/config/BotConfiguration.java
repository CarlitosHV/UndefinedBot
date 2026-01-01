package com.undefined.config;

public class BotConfiguration {
    private final String discordToken;
    private final String spotifyClientId;
    private final String spotifyClientSecret;
    private final String commandPrefix;

    public BotConfiguration() {
        this.discordToken = System.getenv("DISCORD_TOKEN");
        this.spotifyClientId = System.getenv("SPOTIFY_CLIENT_ID");
        this.spotifyClientSecret = System.getenv("SPOTIFY_CLIENT_SECRET");
        this.commandPrefix = System.getenv("COMMAND_PREFIX");
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

    public boolean isValid() {
        return discordToken != null && !discordToken.isEmpty() &&
                spotifyClientId != null && !spotifyClientId.isEmpty() &&
                spotifyClientSecret != null && !spotifyClientSecret.isEmpty();
    }
}
