package com.pedrovh.tortuga.discord.service.command.slash.music;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.service.i18n.MessageService;
import com.pedrovh.tortuga.discord.service.guild.GuildPreferencesService;
import com.pedrovh.tortuga.discord.service.music.MusicService;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class Loop extends AbstractVoiceSlashCommand {

    private final MusicService service;

    public Loop(GuildPreferencesService preferencesService, MessageService messages, MusicService service) {
        super(preferencesService, messages);
        this.service = service;
    }

    @Override
    protected void handle() throws BotException {
        service.loop(voiceChannel, response);
    }

}
