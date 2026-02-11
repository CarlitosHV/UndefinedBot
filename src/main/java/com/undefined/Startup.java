package com.undefined;

import com.undefined.commands.CommandHandler;
import com.undefined.commands.basics.HelpCommand;
import com.undefined.commands.basics.PingCommand;
import com.undefined.commands.music.*;
import com.undefined.config.BotConfiguration;
import com.undefined.core.player.PlayerManager;
import com.undefined.core.voice.VoiceIdleMonitor;
import com.undefined.core.voice.VoiceConnectionManager;
import com.undefined.events.MessageListener;
import com.undefined.core.jda.JdaVoiceConnectionManager;
import dev.arbjerg.lavalink.client.Helpers;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.libraries.jda.JDAVoiceUpdateListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;

public class Startup {
    public static void main(String[] args) throws Exception {
        BotConfiguration config = new BotConfiguration();

        if (!config.isValid()) {
            System.err.println("Error: Variables de entorno no configuradas.");
            return;
        }

        PlayerManager playerManager = PlayerManager.getInstance(config);

        JDA jda = JDABuilder.createDefault(config.getDiscordToken())
                .enableIntents(
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_MESSAGES
                )
                .setVoiceDispatchInterceptor(new JDAVoiceUpdateListener(playerManager.getLavalinkClient()))
                .setActivity(Activity.listening(config.getCommandPrefix() + "play"))
                .build()
                .awaitReady();

        playerManager.connectLavalinkNodes();

        CommandHandler commandHandler = getCommandHandler(playerManager, config);

        jda.addEventListener(new MessageListener(config, commandHandler));

        System.out.println("Bot iniciado correctamente");
    }

    @NotNull
    private static CommandHandler getCommandHandler(PlayerManager playerManager, BotConfiguration config) {
        CommandHandler commandHandler = new CommandHandler(
                List.of(
                        new PingCommand(),
                        new LeaveCommand(playerManager),
                        new PlayCommand(playerManager),
                        new StopCommand(playerManager),
                        new JoinCommand(playerManager),
                        new SkipCommand(playerManager),
                        new QueueCommand(playerManager),
                        new NowPlayingCommand(playerManager),
                        new PauseCommand(playerManager),
                        new ResumeCommand(playerManager),
                        new SeekCommand(playerManager),
                        new VolumeCommand(playerManager),
                        new LoopCommand(playerManager)
                )
        );

        String prefix = config.getCommandPrefix();
        HelpCommand helpCommand = new HelpCommand(commandHandler.getAllCommands(), prefix);
        commandHandler.registerCommand(helpCommand);
        return commandHandler;
    }
}
