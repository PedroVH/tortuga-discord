package com.pedrovh.tortuga.discord.service.command.slash.health;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.service.i18n.MessageService;
import com.pedrovh.tortuga.discord.service.command.slash.AbstractSlashCommand;
import com.pedrovh.tortuga.discord.util.Constants;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;

@Slf4j
@Singleton
public class Ping extends AbstractSlashCommand {

    protected Ping(MessageService messages) {
        super(messages);
    }

    @Override
    protected void handle() throws BotException {
        response
                .addEmbed(new EmbedBuilder()
                        .setTitle(String.format("%s Pong!", Constants.EMOJI_PONG))
                        .setColor(Constants.GREEN))
                .respond();
    }

}
