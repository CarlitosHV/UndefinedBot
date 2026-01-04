package com.undefined.commands.music;

import com.undefined.commands.Command;
import com.undefined.core.player.PlayerManager;
import com.undefined.core.audio.TrackScheduler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class LoopCommand implements Command {

    private final PlayerManager playerManager;

    public LoopCommand(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getName() {
        return "loop";
    }

    @Override
    public String getDescription() {
        return "Configura el modo de repetición. Uso: !loop [off|track|queue]";
    }

    @Override
    public void execute(MessageReceivedEvent event, String args) {
        var guild = event.getGuild();
        var musicManager = playerManager.getMusicManagers().get(guild.getIdLong());

        if (musicManager == null) {
            event.getChannel().sendMessage("No hay música reproduciéndose.").queue();
            return;
        }

        TrackScheduler scheduler = musicManager.getScheduler();

        if (args.isEmpty()) {
            String currentMode = scheduler.isRepeating() ? "track" :
                    scheduler.isRepeatingQueue() ? "queue" : "off";
            event.getChannel().sendMessage("🔁 Modo de repetición actual: **" + currentMode + "**\n" +
                    "Usa `!loop [off|track|queue]` para cambiar").queue();
            return;
        }

        String mode = args.trim().toLowerCase();

        switch (mode) {
            case "off":
                scheduler.setRepeating(false);
                scheduler.setRepeatingQueue(false);
                event.getChannel().sendMessage("🔁 Repetición desactivada.").queue();
                break;

            case "track":
            case "song":
                scheduler.setRepeating(true);
                scheduler.setRepeatingQueue(false);
                event.getChannel().sendMessage("🔂 Repitiendo la canción actual.").queue();
                break;

            case "queue":
            case "all":
                scheduler.setRepeating(false);
                scheduler.setRepeatingQueue(true);
                event.getChannel().sendMessage("🔁 Repitiendo toda la cola.").queue();
                break;

            default:
                event.getChannel().sendMessage("Modo inválido. Usa: `!loop [off|track|queue]`").queue();
                break;
        }
    }
}
