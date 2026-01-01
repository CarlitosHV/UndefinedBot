package com.undefined.commands.music;

import com.undefined.commands.Command;
import com.undefined.core.player.PlayerManager;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class JoinCommand implements Command {

    private final PlayerManager playerManager;

    public JoinCommand(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getDescription() {
        return "Permite al bot ingresar a un canal de voz";
    }

    @Override
    public void execute(MessageReceivedEvent event, String args) {
        var member = event.getMember();
        if (member == null || member.getVoiceState() == null) {
            event.getChannel().sendMessage("No pude obtener el estatus de tu canal de voz :c").queue();
            return;
        }

        AudioChannel voiceChannel = member.getVoiceState().getChannel();
        if (voiceChannel == null) {
            event.getChannel().sendMessage("Debes estar en un canal de voz para usar este comando.").queue();
            return;
        }

        var guild = event.getGuild();
        var audioManager = event.getGuild().getAudioManager();
        audioManager.openAudioConnection(voiceChannel);

        playerManager.getGuildAudioService(guild);

        event.getChannel().sendMessage("Me conecté al canal de voz: " + voiceChannel.getName()).queue();
    }
}
