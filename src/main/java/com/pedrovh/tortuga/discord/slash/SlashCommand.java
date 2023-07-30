package com.pedrovh.tortuga.discord.slash;

import com.pedrovh.tortuga.discord.exception.BotException;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;

public interface SlashCommand {

    void handle(final SlashCommandCreateEvent event) throws BotException;

}
