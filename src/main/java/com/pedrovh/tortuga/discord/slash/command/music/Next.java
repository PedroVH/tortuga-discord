package com.pedrovh.tortuga.discord.slash.command.music;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.music.MusicService;
import com.pedrovh.tortuga.discord.slash.Slash;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.interaction.SlashCommandInteractionOption;

@Slf4j
@Singleton
public class Next extends AbstractVoiceSlashCommand {

    private final MusicService service;

    public Next(MusicService service) {
        this.service = service;
    }

    @Override
    protected void handle() throws BotException {
        interaction.getOptionByName(Slash.OPTION_QUERY).ifPresent(this::optionQuery);
    }

    protected void optionQuery(SlashCommandInteractionOption option) {
        String value = option.getStringValue().orElseThrow();
        service.next(api, voiceChannel, value, response);
    }

}