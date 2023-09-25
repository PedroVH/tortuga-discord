package com.pedrovh.tortuga.discord.exception;

import com.pedrovh.tortuga.discord.service.i18n.MessageService;
import com.pedrovh.tortuga.discord.util.Constants;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public class ServerRequiredException extends BotException {

    private final MessageService messages;

    public ServerRequiredException(MessageService messages) {
        this.messages = messages;
    }

    @Override
    public EmbedBuilder getEmbed() {
        return new EmbedBuilder()
                .setTitle(messages.get("command.error.server-required.title"))
                .setDescription(messages.get("command.error.server-required.description"))
                .setColor(Constants.RED);
    }

}
