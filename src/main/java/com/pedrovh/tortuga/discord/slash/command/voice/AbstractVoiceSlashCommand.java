package com.pedrovh.tortuga.discord.slash.command.voice;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.exception.ServerVCRequiredException;
import com.pedrovh.tortuga.discord.slash.AbstractSlashCommand;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;

import java.util.Optional;

public abstract class AbstractVoiceSlashCommand extends AbstractSlashCommand {

    protected ServerVoiceChannel voiceChannel;

    protected AbstractVoiceSlashCommand() {
        super(true);
    }

    @Override
    protected void load(SlashCommandCreateEvent event) throws BotException {
        super.load(event);
        voiceChannel = getVoiceChannel();
    }

    protected ServerVoiceChannel getVoiceChannel() throws BotException {
        Optional<ServerVoiceChannel> connectedChannel = server.getConnectedVoiceChannel(api.getYourself());
        if(connectedChannel.isEmpty()) {
            Optional<ServerVoiceChannel> userChannel = server.getConnectedVoiceChannel(user);
            return userChannel.orElseThrow(ServerVCRequiredException::new);
        } else
            return connectedChannel.get();
    }

}
