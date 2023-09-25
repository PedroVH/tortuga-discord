package com.pedrovh.tortuga.discord.exception;

import lombok.NoArgsConstructor;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;

@NoArgsConstructor
public abstract class BotException extends Exception {

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

    public abstract EmbedBuilder getEmbed();

    protected MessageFlag[] getMessageFlags() {
        return new MessageFlag[] {MessageFlag.EPHEMERAL};
    }

}
