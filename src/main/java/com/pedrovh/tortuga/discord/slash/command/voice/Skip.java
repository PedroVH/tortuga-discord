package com.pedrovh.tortuga.discord.slash.command.voice;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.music.MusicService;
import com.pedrovh.tortuga.discord.util.Constants;
import jakarta.inject.Singleton;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;

@Singleton
public class Skip extends AbstractVoiceSlashCommand {

    private final MusicService service;

    public Skip(MusicService service) {
        this.service = service;
    }

    @Override
    protected void handle() throws BotException {
        service.next(event.getApi(), voiceChannel, response);
    }
}
