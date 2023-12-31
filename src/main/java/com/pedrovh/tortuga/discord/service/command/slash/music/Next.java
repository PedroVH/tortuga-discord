package com.pedrovh.tortuga.discord.service.command.slash.music;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.service.i18n.MessageService;
import com.pedrovh.tortuga.discord.service.guild.GuildPreferencesService;
import com.pedrovh.tortuga.discord.service.music.MusicService;
import com.pedrovh.tortuga.discord.service.command.slash.Slash;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.interaction.SlashCommandInteractionOption;

@Slf4j
@Singleton
public class Next extends AbstractVoiceSlashCommand {

    private final MusicService service;

    public Next(GuildPreferencesService preferencesService, MessageService messaages, MusicService service) {
        super(preferencesService, messaages);
        this.service = service;
    }

    @Override
    protected void handle() throws BotException {
        interaction.getOptionByName(Slash.OPTION_QUERY).ifPresent(this::optionQuery);
    }

    protected void optionQuery(SlashCommandInteractionOption option) {
        String value = option.getStringValue().orElseThrow();
        service.next(voiceChannel, value, response);
    }

}
