package com.pedrovh.tortuga.discord.service.command.slash.music;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.service.guild.GuildPreferencesService;
import com.pedrovh.tortuga.discord.service.music.MusicService;
import com.pedrovh.tortuga.discord.service.command.slash.Slash;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
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
        Long pos = interaction.getOptionByName(Slash.OPTION_TRACK).flatMap(SlashCommandInteractionOption::getLongValue).orElseThrow();
        String q = interaction.getOptionByName(Slash.OPTION_QUERY).flatMap(SlashCommandInteractionOption::getStringValue).orElseThrow();

        service.replace(voiceChannel, pos, q, response);
    }

}
