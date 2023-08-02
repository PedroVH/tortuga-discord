package com.pedrovh.tortuga.discord.slash.command.music;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.exception.QueueEmptyException;
import com.pedrovh.tortuga.discord.guild.service.GuildPreferencesService;
import com.pedrovh.tortuga.discord.music.MusicService;
import com.pedrovh.tortuga.discord.slash.Slash;
import com.pedrovh.tortuga.discord.util.Constants;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;

@Slf4j
@Singleton
public class Remove extends AbstractVoiceSlashCommand {

    private final MusicService service;

    protected Remove(GuildPreferencesService preferencesService, MusicService service) {
        super(preferencesService);
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
                            .setTitle(Constants.TITLE_ERROR)
                            .setDescription("'Start' position must be smaller than 'end' position!");
                }
            };
        }
        if(service.isQueueEmpty(server)) {
            throw new QueueEmptyException();
        }

        service.remove(event.getApi(), voiceChannel, response, start, end);
    }

    protected Long getLongOption(String name) {
        return interaction.getOptionByName(name).map(option -> option.getLongValue().orElse(1L)).orElse(1L);
    }

}
