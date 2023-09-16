package com.pedrovh.tortuga.discord.listener;

import com.pedrovh.tortuga.discord.playlist.service.PlaylistService;
import com.pedrovh.tortuga.discord.util.Constants;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;

import java.util.Optional;

@Slf4j
@Singleton
public class MessageComponentListener implements MessageComponentCreateListener {

    private final PlaylistService playlistService;

    public MessageComponentListener(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @Override
    public void onComponentCreate(MessageComponentCreateEvent event) {
        MessageComponentInteraction messageComponentInteraction = event.getMessageComponentInteraction();
        String customId = messageComponentInteraction.getCustomId();
        Optional<Server> server = messageComponentInteraction.getServer();

        if (customId.equalsIgnoreCase(Constants.EVENT_CANCEL)) {
            messageComponentInteraction.getMessage().delete();
        }
        if (server.isPresent() && customId.startsWith(Constants.EVENT_PLAYLIST_REPLACE))
            playlistService.save(
                    server.get(),
                    customId.replace(Constants.EVENT_PLAYLIST_REPLACE, ""),
                    messageComponentInteraction.createImmediateResponder(),
                    true);
    }

}