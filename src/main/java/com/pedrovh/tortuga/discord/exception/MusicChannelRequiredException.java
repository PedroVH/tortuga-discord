package com.pedrovh.tortuga.discord.exception;

import com.pedrovh.tortuga.discord.service.i18n.MessageService;
import com.pedrovh.tortuga.discord.util.Constants;
import lombok.NoArgsConstructor;
import org.javacord.api.entity.message.embed.EmbedBuilder;

@NoArgsConstructor
public class MusicChannelRequiredException extends BotException {

    private String guildId;
    private String channelMention = "the configured music channel";
    private MessageService messages;

    public MusicChannelRequiredException(String guildId, MessageService messages) {
        this.guildId = guildId;
        this.messages = messages;
    }

    public MusicChannelRequiredException(String guildId, String channelMention, MessageService messages) {
        this.guildId = guildId;
        this.channelMention = channelMention;
        this.messages = messages;
    }

    @Override
    public EmbedBuilder getEmbed() {
        return new EmbedBuilder()
                .setTitle(messages.get(guildId, "command.music.error.vc-config-required.title"))
                .setDescription(messages.get(guildId, "command.music.error.vc-config-required.description", channelMention))
                .setFooter(messages.get(guildId, "command.music.error.vc-config-required.footer"))
                .setColor(Constants.RED);
    }

}
