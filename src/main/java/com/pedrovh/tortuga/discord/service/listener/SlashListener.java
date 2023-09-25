package com.pedrovh.tortuga.discord.service.listener;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.service.i18n.MessageService;
import com.pedrovh.tortuga.discord.service.command.slash.Slash;
import com.pedrovh.tortuga.discord.util.Constants;
import io.micronaut.context.ApplicationContext;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
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
        String serverId = interaction.getServer().map(Server::getIdAsString).orElse(null);
        final String name = interaction.getCommandName();

        try {
            Optional<Slash> slash = Arrays.stream(Slash.values()).filter(s -> s.name.equals(name)).findFirst();

            if(slash.isPresent())
                context.getBean(slash.get().handler).handle(event);
            else
                log.warn("Unable to handle slash command create event for command {}", name);

        } catch (BotException e) {
            log.warn("Caught BotException: ", e);
            e.respond(interaction.createImmediateResponder());
        } catch (Exception e) {
            log.error("Caught Exception handling Slash command: ", e);
            interaction.createImmediateResponder()
                    .addEmbed(new EmbedBuilder()
                            .setTitle(context.getBean(MessageService.class).get(serverId, "command.error.title"))
                            .setDescription(e.getMessage())
                            .setColor(Constants.RED))
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
        }
    }

}
