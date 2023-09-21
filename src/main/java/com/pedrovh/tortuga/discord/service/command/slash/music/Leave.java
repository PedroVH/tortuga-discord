package com.pedrovh.tortuga.discord.service.command.slash.music;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.service.i18n.MessageService;
import com.pedrovh.tortuga.discord.service.guild.GuildPreferencesService;
import com.pedrovh.tortuga.discord.util.Constants;
import com.pedrovh.tortuga.discord.service.music.VoiceConnectionService;
import jakarta.inject.Singleton;
import org.javacord.api.entity.message.embed.EmbedBuilder;

@Singleton
public class Leave extends AbstractVoiceSlashCommand {

    private final VoiceConnectionService service;

    public Leave(GuildPreferencesService preferencesService, VoiceConnectionService service, MessageService messages) {
        super(preferencesService, messages);
        this.service = service;
    }

    @Override
    protected void handle() throws BotException {
        service.leaveVoiceChannel(voiceChannel);

        response
                .addEmbed(new EmbedBuilder()
                        .setTitle(messages.get(server.getIdAsString(), "command.music.leave.title"))
                        .setColor(Constants.GREEN))
                .respond()
                .join();
    }

}
