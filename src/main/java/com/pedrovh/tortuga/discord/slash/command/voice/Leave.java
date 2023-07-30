package com.pedrovh.tortuga.discord.slash.command.voice;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.util.Constants;
import com.pedrovh.tortuga.discord.voice.VoiceConnectionService;
import jakarta.inject.Singleton;
import org.javacord.api.entity.message.embed.EmbedBuilder;

@Singleton
public class Leave extends AbstractVoiceSlashCommand {

    private final VoiceConnectionService service;

    public Leave(VoiceConnectionService service) {
        this.service = service;
    }

    @Override
    protected void handle() throws BotException {
        service.leaveVoiceChannel(voiceChannel);

        response
                .addEmbed(new EmbedBuilder()
                        .setTitle("\uD83C\uDF42 Leaving...")
                        .setColor(Constants.GREEN))
                .respond()
                .join();
    }

}
