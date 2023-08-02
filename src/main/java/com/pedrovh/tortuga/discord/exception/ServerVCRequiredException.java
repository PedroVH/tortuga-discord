package com.pedrovh.tortuga.discord.exception;

import com.pedrovh.tortuga.discord.util.Constants;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public class ServerVCRequiredException extends BotException {

    private static final String MESSAGE = "You have to be in a voice channel!";

    public ServerVCRequiredException() {
        super(MESSAGE);
    }

    public ServerVCRequiredException(Throwable cause) {
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
