package com.pedrovh.tortuga.discord.service.command.slash.music;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.service.command.slash.Slash;
import com.pedrovh.tortuga.discord.service.guild.GuildPreferencesService;
import com.pedrovh.tortuga.discord.service.playlist.UserPlaylistService;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.Optional;

@Slf4j
@Singleton
public class Playlist extends AbstractVoiceSlashCommand {

    private final UserPlaylistService service;

    protected Playlist(GuildPreferencesService preferencesService, UserPlaylistService service) {
        super(preferencesService);
        this.service = service;
    }

    @Override
    protected void handle() throws BotException {
        interaction.getOptionByName(Slash.OPTION_SAVE).ifPresent(this::optionSave);
        interaction.getOptionByName(Slash.OPTION_LOAD).ifPresent(this::optionLoad);
        interaction.getOptionByName(Slash.OPTION_DELETE).ifPresent(this::optionDelete);
        interaction.getOptionByName(Slash.OPTION_LIST).ifPresent(this::optionList);
    }

    private void optionSave(SlashCommandInteractionOption option) {
        String value = option.getOptions().get(0).getStringValue().orElseThrow();
        service.save(user.getIdAsString(), server, value, response, false);
    }

    private void optionLoad(SlashCommandInteractionOption option) {
        String value = option.getOptions().get(0).getStringValue().orElseThrow();
        service.load(user.getIdAsString(), voiceChannel, value, response);
    }

    private void optionDelete(SlashCommandInteractionOption option) {
        String value = option.getOptions().get(0).getStringValue().orElseThrow();
        service.delete(user.getIdAsString(), server, value, response);
    }

    private void optionList(SlashCommandInteractionOption option) {
        Optional<SlashCommandInteractionOption> name = option.getOptionByName(Slash.OPTION_NAME);
        if(name.isPresent() && name.get().getStringValue().isPresent()) {
            service.list(user.getIdAsString(), server, name.get().getStringValue().get(), response);
        } else {
            service.list(user.getIdAsString(), server, response);
        }
    }

}
