package com.pedrovh.tortuga.discord.exception;

import com.pedrovh.tortuga.discord.util.Constants;
import lombok.NoArgsConstructor;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;

@NoArgsConstructor
public class MusicChannelRequiredException extends BotException {

    private String channelMention = "the configured music channel";

    public MusicChannelRequiredException(String channelMention) {
        this.channelMention = channelMention;
    }

    @Override
    public EmbedBuilder getEmbed() {
        return new EmbedBuilder()
                .setTitle(Constants.TITLE_ERROR)
                .setDescription(String.format("Please, use this command in %s!", channelMention))
                .setFooter("Use '/channel music True' to configure a text channel")
                .setColor(Constants.RED);
    }

}
