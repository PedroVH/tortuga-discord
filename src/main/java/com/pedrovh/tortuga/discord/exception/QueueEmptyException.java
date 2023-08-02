package com.pedrovh.tortuga.discord.exception;

import com.pedrovh.tortuga.discord.util.Constants;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public class QueueEmptyException extends BotException {

    private static final String MESSAGE = "Queue is empty!";

    public QueueEmptyException() {
        super(MESSAGE);
    }

    public QueueEmptyException(Throwable cause) {
        super(MESSAGE, cause);
    }


    @Override
    public EmbedBuilder getEmbed() {
        return new EmbedBuilder()
                .setTitle(String.format("%s %s", Constants.EMOJI_WARNING, MESSAGE))
                .setColor(Constants.YELLOW);
    }

}
