package com.pedrovh.tortuga.discord.service.command.slash;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.service.i18n.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;

@Slf4j
public abstract class AbstractSlashCommand implements SlashCommand {


    protected MessageService messages;
    protected SlashCommandCreateEvent event;
    protected DiscordApi api;
    protected SlashCommandInteraction interaction;
    protected InteractionImmediateResponseBuilder response;
    protected TextChannel textChannel;
    protected User user;

    protected AbstractSlashCommand(MessageService messages) {
        this.messages = messages;
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
        user = interaction.getUser();

        log(interaction.getFullCommandName());
    }

    protected void log(String commandName) {
        log.info("user {} sent slash command {} in {}",
                user.getName(),
                commandName,
                textChannel);
    }

    protected abstract void handle() throws BotException;

}
