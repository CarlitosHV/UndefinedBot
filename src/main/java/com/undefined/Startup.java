package com.undefined;

import com.undefined.config.BotConfiguration;
import com.undefined.core.player.PlayerManager;
import com.undefined.events.MessageListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Startup {
    public static void main(String[] args) {
        BotConfiguration config = new BotConfiguration();

        if (!config.isValid()) {
            System.err.println("Error: Variables de entorno no configuradas.");
            return;
        }

        PlayerManager playerManager = PlayerManager.getInstance(config);

        JDABuilder.createDefault(config.getDiscordToken())
                .enableIntents(
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_MESSAGES
                )
                .setActivity(Activity.listening(config.getCommandPrefix() + "play"))
                .addEventListeners(new MessageListener(config))
                .build();

        System.out.println("Bot iniciado correctamente");
    }
}
