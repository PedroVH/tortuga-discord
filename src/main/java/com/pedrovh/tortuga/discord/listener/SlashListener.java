package com.pedrovh.tortuga.discord.listener;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.slash.Slash;
import io.micronaut.context.ApplicationContext;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Singleton
public class SlashListener implements SlashCommandCreateListener {

    @Inject
    private ApplicationContext context;

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        final String name = interaction.getCommandName();

        interaction.getServer().ifPresentOrElse(
                s ->log.info("[{}] received slash interaction: {}", s.getName(), name),
                ()->log.info("received slash interaction: {}", name)
        );

        try {
            Optional<Slash> slash = Arrays.stream(Slash.values()).filter(s -> s.name.equals(name)).findFirst();

            if(slash.isPresent())
                context.getBean(slash.get().handler).handle(event);
            else
                log.warn("Unable to handle slash command create event for command {}", name);

        } catch (BotException e) {
            e.respond(interaction.createImmediateResponder());
        }
    }

}
