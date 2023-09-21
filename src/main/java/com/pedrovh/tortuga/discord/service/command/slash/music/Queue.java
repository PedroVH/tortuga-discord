package com.pedrovh.tortuga.discord.service.command.slash.music;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.exception.QueueEmptyException;
import com.pedrovh.tortuga.discord.service.i18n.MessageService;
import com.pedrovh.tortuga.discord.service.guild.GuildPreferencesService;
import com.pedrovh.tortuga.discord.service.music.MusicService;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class Queue extends AbstractVoiceSlashCommand {

    private final MusicService service;

    public Queue(GuildPreferencesService preferencesService, MessageService messages, MusicService service) {
        super(preferencesService, messages);
        this.service = service;
    }

    @Override
    protected void handle() throws BotException {
        if(service.isQueueEmpty(server)) {
            throw new QueueEmptyException(server.getIdAsString(), messages);
        }
        service.queue(server, response);
    }
}
