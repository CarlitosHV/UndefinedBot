package com.undefined;

import com.undefined.commands.CommandHandler;
import com.undefined.commands.basics.PingCommand;
import com.undefined.commands.music.JoinCommand;
import com.undefined.config.BotConfiguration;
import com.undefined.core.player.PlayerManager;
import com.undefined.events.MessageListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.List;

public class Startup {
    public static void main(String[] args) {
        BotConfiguration config = new BotConfiguration();

        if (!config.isValid()) {
            System.err.println("Error: Variables de entorno no configuradas.");
            return;
        }

        PlayerManager playerManager = PlayerManager.getInstance(config);

        CommandHandler commandHandler = new CommandHandler(
                List.of(
                        new PingCommand(),
                        new JoinCommand()
                )
        );

        JDABuilder.createDefault(config.getDiscordToken())
                .enableIntents(
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_MESSAGES
                )
                .setActivity(Activity.listening(config.getCommandPrefix() + "play"))
                .addEventListeners(new MessageListener(config, commandHandler))
                .build();

        System.out.println("Bot iniciado correctamente");
    }
}
