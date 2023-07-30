package com.pedrovh.tortuga.discord.slash.command;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.slash.AbstractSlashCommand;
import com.pedrovh.tortuga.discord.slash.Slash;
import com.pedrovh.tortuga.discord.util.Constants;
import jakarta.inject.Singleton;
import org.javacord.api.entity.message.embed.EmbedBuilder;

@Singleton
public class Help extends AbstractSlashCommand {

    protected Help() {
        super(false);
    }

    @Override
    protected void handle() throws BotException {
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Constants.GREEN)
                .setTitle(String.format("%s How to use", Constants.EMOJI_INFO))
                .setDescription(
"""
To play music, first an admin needs to set a channel as the server's music channel(/channel music True). This channel will be exclusive to music commands, so it's recommended to create a new one.

After you've done that, simply send a song's URL or a search query on the channel, and I'll try to connect to your voice chat and play it to you!

Have a look at the command list to know more about me:""");

        for (Slash slash : Slash.values()) {
            embed.addInlineField(slash.name, slash.description);
        }
        response.addEmbed(embed).respond();
    }

}
