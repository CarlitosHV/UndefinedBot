package com.undefined.commands.basics;

import com.undefined.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.awt.Color;
import java.time.Instant;
import java.util.List;

public class HelpCommand implements Command {

    final List<Command> allCommands;
    final String prefix;

    public HelpCommand(List<Command> allCommands, String prefix) {
        this.allCommands = allCommands;
        this.prefix = prefix;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Muestra esta lista de comandos.";
    }

    @Override
    public void execute(MessageReceivedEvent event, String args) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Lista de comandos")
                .setColor(Color.BLUE)
                .setDescription("Usa `" + event.getJDA().getSelfUser().getName() + "`")
                .setFooter("Comandos disponibles: ", event.getJDA().getSelfUser().getAvatarUrl())
                .setTimestamp(Instant.now());

        StringBuilder description = new StringBuilder();
        for (Command command : allCommands) {
            description.append("**`")
                    .append(prefix)
                    .append(command.getName())
                    .append("`** - ")
                    .append(command.getDescription())
                    .append("\n");
        }
        embed.addField("Comandos", description.toString(), false);

        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }
}
