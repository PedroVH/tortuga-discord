package com.pedrovh.tortuga.discord.service.command.slash;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.exception.ServerRequiredException;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;

@Slf4j
public abstract class AbstractSlashCommand implements SlashCommand {

    private final boolean isServerOnly;

    protected SlashCommandCreateEvent event;
    protected SlashCommandInteraction interaction;
    protected DiscordApi api;
    protected Server server;
    protected InteractionImmediateResponseBuilder response;
    protected TextChannel textChannel;
    protected ServerTextChannel serverTextChannel;
    protected User user;

    protected AbstractSlashCommand(boolean isServerOnly) {
        this.isServerOnly = isServerOnly;
    }

    @Override
    public void handle(SlashCommandCreateEvent event) throws BotException {
        load(event);
        handle();
    }

    protected void load(SlashCommandCreateEvent event) throws BotException {
        this.event = event;
        interaction = event.getSlashCommandInteraction();
        api = interaction.getApi();
        response = interaction.createImmediateResponder();
        textChannel = interaction.getChannel().orElseThrow();

        // server only
        if(isServerOnly)
            server = interaction.getServer().orElseThrow(ServerRequiredException::new);
        serverTextChannel = textChannel.asServerTextChannel().orElse(null);
        //

        user = interaction.getUser();
        String commandName = interaction.getFullCommandName();

        log.info("[{}] user {} sent slash command {} in {}",
                server.getName(),
                user.getName(),
                commandName,
                textChannel);
    }

    protected abstract void handle() throws BotException;

}
