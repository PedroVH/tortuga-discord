package com.pedrovh.tortuga.discord.service.command.slash.music;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.exception.MusicChannelRequiredException;
import com.pedrovh.tortuga.discord.exception.ServerVCRequiredException;
import com.pedrovh.tortuga.discord.model.guild.GuildPreferences;
import com.pedrovh.tortuga.discord.service.command.slash.AbstractSlashServerCommand;
import com.pedrovh.tortuga.discord.service.guild.GuildPreferencesService;
import com.pedrovh.tortuga.discord.service.i18n.MessageService;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;

import java.util.Optional;

public abstract class AbstractVoiceSlashCommand extends AbstractSlashServerCommand {

    protected final GuildPreferencesService preferencesService;
    protected ServerVoiceChannel voiceChannel;

    protected AbstractVoiceSlashCommand(GuildPreferencesService preferencesService,  MessageService messages) {
        super(messages);
        this.preferencesService = preferencesService;
    }

    @Override
    protected void load(SlashCommandCreateEvent event) throws BotException {
        super.load(event);

        Optional<GuildPreferences> preferences = preferencesService.findById(server.getIdAsString());
        if(preferences.isEmpty()) {
            throw new MusicChannelRequiredException(server.getIdAsString(), messages);
        }
        if(!textChannel.getIdAsString().equalsIgnoreCase(preferences.get().getMusicChannelId())) {
            Optional<ServerTextChannel> serverTextChannel = server.getTextChannelById(preferences.get().getMusicChannelId());
            if(serverTextChannel.isEmpty()) {
                throw new MusicChannelRequiredException();
            }
            throw new MusicChannelRequiredException(server.getIdAsString(), serverTextChannel.get().getMentionTag(), messages);
        }

        voiceChannel = getVoiceChannel();
    }

    protected ServerVoiceChannel getVoiceChannel() throws BotException {
        Optional<ServerVoiceChannel> connectedChannel = server.getConnectedVoiceChannel(api.getYourself());
        if(connectedChannel.isEmpty()) {
            Optional<ServerVoiceChannel> userChannel = server.getConnectedVoiceChannel(user);
            return userChannel.orElseThrow(() -> new ServerVCRequiredException(server.getIdAsString(), messages));
        } else
            return connectedChannel.get();
    }

}
