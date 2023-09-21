package com.pedrovh.tortuga.discord.service.command.slash.music;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.exception.QueueEmptyException;
import com.pedrovh.tortuga.discord.service.i18n.MessageService;
import com.pedrovh.tortuga.discord.service.guild.GuildPreferencesService;
import com.pedrovh.tortuga.discord.service.music.MusicService;
import com.pedrovh.tortuga.discord.service.command.slash.Slash;
import com.pedrovh.tortuga.discord.util.Constants;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;

@Slf4j
@Singleton
public class Remove extends AbstractVoiceSlashCommand {

    private final MusicService service;

    protected Remove(GuildPreferencesService preferencesService, MusicService service, MessageService messages) {
        super(preferencesService, messages);
        this.service = service;
    }

    @Override
    protected void handle() throws BotException {
        int start = getLongOption(Slash.OPTION_START).intValue() - 1;
        int end = getLongOption(Slash.OPTION_END).intValue();

        if(start >= end) {
            throw new BotException() {
                @Override
                public EmbedBuilder getEmbed() {
                    return new EmbedBuilder()
                            .setTitle(messages.get(server.getIdAsString(), "command.music.remove.error.invalid-position.title"))
                            .setDescription(messages.get(server.getIdAsString(), "command.music.remove.error.invalid-position.description"))
                            .setColor(Constants.RED);
                }
            };
        }
        if(service.isQueueEmpty(server)) {
            throw new QueueEmptyException(server.getIdAsString(), messages);
        }

        service.remove(voiceChannel, response, start, end);
    }

    protected Long getLongOption(String name) {
        return interaction.getOptionByName(name).map(option -> option.getLongValue().orElse(1L)).orElse(1L);
    }

}
