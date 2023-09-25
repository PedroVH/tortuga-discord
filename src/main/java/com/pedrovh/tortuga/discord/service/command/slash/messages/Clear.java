package com.pedrovh.tortuga.discord.service.command.slash.messages;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.service.command.slash.AbstractSlashCommand;
import com.pedrovh.tortuga.discord.service.command.slash.Slash;
import com.pedrovh.tortuga.discord.service.i18n.MessageService;
import com.pedrovh.tortuga.discord.util.Constants;
import jakarta.inject.Singleton;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommandInteractionOption;

@Singleton
public class Clear extends AbstractSlashCommand {

    protected Clear(MessageService messages) {
        super(messages);
    }

    @Override
    protected void handle() throws BotException {
        interaction.getOptionByName(Slash.OPTION_NUMBER).ifPresent(this::optionNumber);
    }

    protected void optionNumber(SlashCommandInteractionOption option) {
        Long limit = option.getLongValue().orElse(1L);

        textChannel.bulkDelete(textChannel.getMessages(limit.intValue()).join());

        interaction.createImmediateResponder()
                .addEmbed(new EmbedBuilder()
                        .setTitle(messages.get(interaction.getServer().map(Server::getIdAsString).orElse(null), "command.clear.title", limit))
                        .setColor(Constants.GREEN))
                .setFlags(MessageFlag.EPHEMERAL)
                .respond();
    }

}
