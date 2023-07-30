package com.pedrovh.tortuga.discord.slash.command.voice;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.music.MusicService;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class Queue extends AbstractVoiceSlashCommand {

    private final MusicService service;

    public Queue(MusicService service) {
        this.service = service;
    }

    @Override
    protected void handle() throws BotException {
        service.queue(server, response);
    }
}
