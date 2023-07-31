package com.pedrovh.tortuga.discord.slash.command.voice;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.voice.music.MusicService;
import jakarta.inject.Singleton;

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
