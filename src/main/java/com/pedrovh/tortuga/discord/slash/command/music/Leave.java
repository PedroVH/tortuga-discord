package com.pedrovh.tortuga.discord.slash.command.music;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.guild.service.GuildPreferencesService;
import com.pedrovh.tortuga.discord.util.Constants;
import com.pedrovh.tortuga.discord.music.VoiceConnectionService;
import jakarta.inject.Singleton;
import org.javacord.api.entity.message.embed.EmbedBuilder;

@Singleton
public class Leave extends AbstractVoiceSlashCommand {

    private final VoiceConnectionService service;

    public Leave(GuildPreferencesService preferencesService, VoiceConnectionService service) {
        super(preferencesService);
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
