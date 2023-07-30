package com.pedrovh.tortuga.discord.exception;

import com.pedrovh.tortuga.discord.util.Constants;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public class ServerRequiredException extends BotException {

    private static final String friendlyMessage = "This command can only be executed on a server";

    public ServerRequiredException() {
        super(friendlyMessage);
    }

    public ServerRequiredException(Throwable cause) {
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
