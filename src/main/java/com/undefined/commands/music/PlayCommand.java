package com.undefined.commands.music;

import com.undefined.commands.Command;
import com.undefined.core.player.PlayerManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PlayCommand implements Command {

    private final PlayerManager playerManager;

    public PlayCommand(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Reproduce música de YouTube o Spotify. Uso: !play <url o nombre>";
    }

    @Override
    public void execute(MessageReceivedEvent event, String args) {
        if (args.isEmpty()) {
            event.getChannel().sendMessage("Debes proporcionar una URL o nombre de canción.").queue();
            return;
        }

        Member member = event.getMember();
        if (member == null) {
            event.getChannel().sendMessage("No pude obtener tu información.").queue();
            return;
        }

        GuildVoiceState voiceState = member.getVoiceState();
        if (voiceState == null || !voiceState.inAudioChannel()) {
            event.getChannel().sendMessage("¡Necesitas estar en un canal de voz para reproducir música!").queue();
            return;
        }

        AudioManager audioManager = event.getGuild().getAudioManager();
        if (!audioManager.isConnected()) {
            audioManager.openAudioConnection(voiceState.getChannel());
        }

        var guild = event.getGuild();
        var channel = event.getChannel().asTextChannel();

        playerManager.loadAndPlay(guild, channel, args, member);
    }
}
