package com.pedrovh.tortuga.discord.exception;

import com.pedrovh.tortuga.discord.util.Constants;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public class ServerRequiredException extends BotException {

    private static final String MESSAGE = "This command can only be executed on a server";

    public ServerRequiredException() {
        super(MESSAGE);
    }

    public ServerRequiredException(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public EmbedBuilder getEmbed() {
        return new EmbedBuilder()
                .setTitle(Constants.TITLE_ERROR)
                .setDescription(MESSAGE)
                .setColor(Constants.RED);
    }

}
