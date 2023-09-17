package com.pedrovh.tortuga.discord.service.command.slash.music;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.exception.QueueEmptyException;
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

    public Replace(GuildPreferencesService preferencesService, MusicService service) {
        super(preferencesService);
        this.service = service;
    }

    @Override
    protected void handle() throws BotException {
        long pos = interaction.getOptionByName(Slash.OPTION_TRACK).flatMap(SlashCommandInteractionOption::getLongValue).orElseThrow() - 1;
        String q = interaction.getOptionByName(Slash.OPTION_QUERY).flatMap(SlashCommandInteractionOption::getStringValue).orElseThrow();

        if(pos < 0) {
            throw new BotException() {
                @Override
                public EmbedBuilder getEmbed() {
                    return new EmbedBuilder()
                            .setTitle(Constants.TITLE_ERROR)
                            .setDescription("Track position must be >= 0!")
                            .setColor(Constants.RED);
                }
            };
        }
        if(service.isQueueEmpty(server)) {
            throw new QueueEmptyException();
        }

        service.replace(voiceChannel, pos, q, response);
    }

}
