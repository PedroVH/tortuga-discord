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
import org.javacord.api.interaction.SlashCommandInteractionOption;

@Slf4j
@Singleton
public class Replace extends AbstractVoiceSlashCommand {

    private final MusicService service;

    public Replace(GuildPreferencesService preferencesService, MusicService service, MessageService messages) {
        super(preferencesService, messages);
        this.service = service;
    }

    @Override
    protected void handle() throws BotException {
        long pos = interaction.getOptionByName(Slash.OPTION_TRACK).flatMap(SlashCommandInteractionOption::getLongValue).orElseThrow() - 1;
        String q = interaction.getOptionByName(Slash.OPTION_QUERY).flatMap(SlashCommandInteractionOption::getStringValue).orElseThrow();

        if(pos < -1) {
            throw new BotException() {
                @Override
                public EmbedBuilder getEmbed() {
                    return new EmbedBuilder()
                            .setTitle(messages.get(server.getIdAsString(), "command.music.replace.error.invalid-position.title"))
                            .setDescription(messages.get(server.getIdAsString(), "command.music.replace.error.invalid-position.description"))
                            .setColor(Constants.RED);
                }
            };
        }
        if(service.isQueueEmpty(server)) {
            throw new QueueEmptyException(server.getIdAsString(), messages);
        }

        service.replace(voiceChannel, pos, q, response);
    }

}
