package com.pedrovh.tortuga.discord.exception;

import com.pedrovh.tortuga.discord.util.Constants;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public class ServerVCRequiredException extends BotException {

    private static final String friendlyMessage = "You have to be in a voice channel!";

    public ServerVCRequiredException() {
        super(friendlyMessage);
    }

    public ServerVCRequiredException(Throwable cause) {
        super(friendlyMessage, cause);
    }

    @Override
    public EmbedBuilder getEmbed() {
        return new EmbedBuilder()
                .setTitle(Constants.TITLE_ERROR)
                .setDescription(friendlyMessage)
                .setColor(Constants.RED);
    }

    @Override
    protected MessageFlag[] getMessageFlags() {
        return new MessageFlag[] {MessageFlag.EPHEMERAL};
    }

}
