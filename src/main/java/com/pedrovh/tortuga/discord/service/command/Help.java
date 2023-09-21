package com.pedrovh.tortuga.discord.service.command;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.service.i18n.MessageService;
import com.pedrovh.tortuga.discord.service.command.slash.AbstractSlashCommand;
import com.pedrovh.tortuga.discord.service.command.slash.Slash;
import com.pedrovh.tortuga.discord.util.Constants;
import jakarta.inject.Singleton;
import org.javacord.api.entity.message.embed.EmbedBuilder;

@Singleton
public class Help extends AbstractSlashCommand {

    protected Help(MessageService messages) {
        super(messages);
    }

    @Override
    protected void handle() throws BotException {
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Constants.GREEN)
                .setTitle(messages.get("command.help.title"))
                .setDescription(messages.get("command.help.description"));

        for (Slash slash : Slash.values()) {
            embed.addInlineField(slash.name, slash.description);
        }
        response.addEmbed(embed).respond();
    }

}
