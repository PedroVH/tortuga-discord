package com.pedrovh.tortuga.discord.service.command.slash.channel;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.model.guild.GuildPreferences;
import com.pedrovh.tortuga.discord.service.command.slash.AbstractSlashServerCommand;
import com.pedrovh.tortuga.discord.service.command.slash.Slash;
import com.pedrovh.tortuga.discord.service.guild.GuildPreferencesService;
import com.pedrovh.tortuga.discord.service.i18n.MessageService;
import com.pedrovh.tortuga.discord.util.Constants;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteractionOption;

@Slf4j
@Singleton
public class Channel extends AbstractSlashServerCommand {

    private final GuildPreferencesService preferencesService;

    public Channel(GuildPreferencesService preferencesService, MessageService messages) {
        super(messages);
        this.preferencesService = preferencesService;
    }

    @Override
    protected void handle() throws BotException {
        interaction.getOptionByName(Slash.OPTION_MUSIC).ifPresent(this::optionMusic);
    }

    protected void optionMusic(SlashCommandInteractionOption option) {
        String musicChannelId = serverTextChannel.getIdAsString();

        GuildPreferences preferences = preferencesService.findById(server.getIdAsString()).orElse(new GuildPreferences(server.getIdAsString()));
        preferences.setMusicChannelId(musicChannelId);
        preferencesService.save(preferences);

        log.info("[{}] music channel changed to {}", server.getName(), serverTextChannel);
        response.addEmbed(
                new EmbedBuilder()
                        .setTitle(messages.get(
                                server.getIdAsString(),
                                "command.channel.music.title",
                                serverTextChannel.getName()))
                        .setColor(Constants.GREEN))
                .respond();
    }

}
