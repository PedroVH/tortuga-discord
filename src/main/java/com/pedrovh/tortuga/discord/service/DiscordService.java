package com.pedrovh.tortuga.discord.service;

import com.pedrovh.tortuga.discord.listener.MessageComponentListener;
import com.pedrovh.tortuga.discord.listener.MessageListener;
import com.pedrovh.tortuga.discord.listener.SlashListener;
import com.pedrovh.tortuga.discord.command.slash.Slash;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.interaction.ApplicationCommand;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Context
public class DiscordService {

    @Value("${discord.token}")
    private String token;
    @Value("${commands.updateAll}")
    private boolean updateCommands;
    private DiscordApi api;

    private final MessageListener messageListener;
    private final SlashListener slashListener;
    private final MessageComponentListener messageComponentListener;

    public DiscordService(MessageListener messageListener, SlashListener slashListener, MessageComponentListener messageComponentListener) {
        this.messageListener = messageListener;
        this.slashListener = slashListener;
        this.messageComponentListener = messageComponentListener;
    }

    @PostConstruct
    void start() {
        log.info("starting discord service...");

        api = new DiscordApiBuilder()
                .setToken(token)
                .setAllIntents()
                .addListener(messageListener)
                .addListener(slashListener)
                .addListener(messageComponentListener)
                .login()
                .join();

        log.info("bot ready!");
        log.info("current global slash commands: {}", api.getGlobalSlashCommands().join().stream().map(ApplicationCommand::getName).toList());

        updateGlobalSlashCommands();
    }

    void updateGlobalSlashCommands() {
        final Set<SlashCommand> global = api.getGlobalSlashCommands().join();
        final Set<String> newCommands = Arrays.stream(Slash.values())
                .map(s -> s.name)
                .filter(s -> global.stream().noneMatch(g->g.getName().equalsIgnoreCase(s)))
                .collect(Collectors.toSet());

        if(!newCommands.isEmpty() || updateCommands) {
            log.info("adding the following global slash commands: {}", newCommands);
            final Set<SlashCommandBuilder> toAdd = Arrays.stream(Slash.values()).map(Slash::build).collect(Collectors.toSet());
            Set<ApplicationCommand> applicationCommands = api.bulkOverwriteGlobalApplicationCommands(toAdd).join();
            log.info("updated all global slash commands: {}", applicationCommands.stream().map(ApplicationCommand::getName).collect(Collectors.toSet()));
        }
    }

}
