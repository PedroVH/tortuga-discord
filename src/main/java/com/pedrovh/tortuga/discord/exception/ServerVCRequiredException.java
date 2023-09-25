package com.pedrovh.tortuga.discord.exception;

import com.pedrovh.tortuga.discord.service.i18n.MessageService;
import com.pedrovh.tortuga.discord.util.Constants;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public class ServerVCRequiredException extends BotException {

    private final String guildId;
    private final MessageService messages;

    public ServerVCRequiredException(String guildId, MessageService messages) {
        this.guildId = guildId;
        this.messages = messages;
    }

    @Override
    public EmbedBuilder getEmbed() {
        return new EmbedBuilder()
                .setTitle(messages.get(guildId, "command.music.error.vc-required.title"))
                .setDescription(messages.get(guildId, "command.music.error.vc-required.description"))
                .setColor(Constants.RED);
    }

}
