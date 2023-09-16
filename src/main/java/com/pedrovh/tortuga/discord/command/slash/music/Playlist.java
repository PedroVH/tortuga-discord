package com.pedrovh.tortuga.discord.command.slash.music;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.guild.service.GuildPreferencesService;
import com.pedrovh.tortuga.discord.playlist.service.PlaylistService;
import com.pedrovh.tortuga.discord.command.slash.Slash;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.Optional;

@Slf4j
@Singleton
public class Playlist extends AbstractVoiceSlashCommand {

    private final PlaylistService service;

    protected Playlist(GuildPreferencesService preferencesService, PlaylistService service) {
        super(preferencesService);
        this.service = service;
    }

    @Override
    protected void handle() throws BotException {
        log.info("used options: " + interaction.getOptions().stream().map(SlashCommandInteractionOption::getName).toList());
        interaction.getOptionByName(Slash.OPTION_SAVE).ifPresent(this::optionSave);
        interaction.getOptionByName(Slash.OPTION_LOAD).ifPresent(this::optionLoad);
        interaction.getOptionByName(Slash.OPTION_LIST).ifPresent(this::optionList);
    }

    private void optionSave(SlashCommandInteractionOption option) {
        String value = option.getOptions().get(0).getStringValue().orElseThrow();
        service.save(server, value, response);
    }

    private void optionLoad(SlashCommandInteractionOption option) {
        String value = option.getOptions().get(0).getStringValue().orElseThrow();
        service.load(value, voiceChannel, response);
    }
    
    private void optionList(SlashCommandInteractionOption option) {
        Optional<SlashCommandInteractionOption> name = option.getOptionByName(Slash.OPTION_NAME);
        if(name.isPresent() && name.get().getStringValue().isPresent()) {
            service.list(server, name.get().getStringValue().get(), response);
        } else {
            service.list(server, response);
        }
    }

}
