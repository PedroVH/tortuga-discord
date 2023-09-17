package com.pedrovh.tortuga.discord.service.command.slash.channel;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.model.guild.GuildPreferences;
import com.pedrovh.tortuga.discord.service.command.slash.AbstractSlashCommand;
import com.pedrovh.tortuga.discord.service.command.slash.Slash;
import com.pedrovh.tortuga.discord.service.guild.GuildPreferencesService;
import com.pedrovh.tortuga.discord.util.Constants;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteractionOption;

@Slf4j
@Singleton
public class Channel extends AbstractSlashCommand {

    private final GuildPreferencesService preferencesService;

    public Channel(GuildPreferencesService preferencesService) {
        super(true);
        this.preferencesService = preferencesService;
    }

    @Override
    protected void handle() throws BotException {
        interaction.getOptionByName(Slash.OPTION_MUSIC).ifPresent(this::optionMusic);
    }

    protected void optionMusic(SlashCommandInteractionOption option) {

        boolean choice = option.getBooleanValue().orElse(false);
        String musicChannelId = choice ? serverTextChannel.getIdAsString() : null;

        GuildPreferences preferences = preferencesService.findById(server.getIdAsString()).orElse(new GuildPreferences());
        preferences.setGuildId(server.getIdAsString());
        preferences.setMusicChannelId(musicChannelId);
        preferencesService.save(preferences);

        log.info("[{}] music channel changed to {}", server.getName(), musicChannelId == null ? "none" : serverTextChannel);
        response.addEmbed(
                new EmbedBuilder()
                        .setTitle(String.format(
                                "%s Music channel changed to %s!",
                                Constants.EMOJI_SUCCESS,
                                musicChannelId == null ? "none" : serverTextChannel))
                        .setColor(Constants.GREEN))
                .respond();
    }

}
