package com.pedrovh.tortuga.discord.exception;

import lombok.NoArgsConstructor;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;

@NoArgsConstructor
public abstract class BotException extends Exception {

    public BotException(String message) {
        super(message);
    }

    public BotException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract EmbedBuilder getEmbed();

    public void respond(InteractionImmediateResponseBuilder responder) {
        responder
                .addEmbed(getEmbed())
                .setFlags(getMessageFlags())
                .respond();
    }

    public void respond(TextChannel channel) {
        MessageBuilder mb = new MessageBuilder();
        mb.addEmbed(getEmbed())
                .send(channel);
    }

    protected MessageFlag[] getMessageFlags() {
        return new MessageFlag[0];
    }

}
