package com.pedrovh.tortuga.discord.service.command.slash;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.exception.ServerRequiredException;
import com.pedrovh.tortuga.discord.service.i18n.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;

@Slf4j
public abstract class AbstractSlashServerCommand extends AbstractSlashCommand {

    protected Server server;
    protected ServerTextChannel serverTextChannel;

    protected AbstractSlashServerCommand(MessageService messages) {
        super(messages);
    }

    @Override
    protected void load(SlashCommandCreateEvent event) throws BotException {
        server = event.getSlashCommandInteraction().getServer().orElseThrow(() -> new ServerRequiredException(messages));
        serverTextChannel = event.getSlashCommandInteraction().getChannel().map(Channel::asServerTextChannel).orElseThrow().orElseThrow();
        super.load(event);
    }

    @Override
    protected void log(String commandName) {
        log.info("[{}] user {} sent slash command {} in {}",
                server.getName(),
                user.getName(),
                commandName,
                textChannel);
    }
}
