package com.pedrovh.tortuga.discord.listener;

import com.pedrovh.tortuga.discord.guild.model.GuildPreferences;
import com.pedrovh.tortuga.discord.guild.service.GuildPreferencesService;
import com.pedrovh.tortuga.discord.voice.music.MusicService;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.Optional;

@Slf4j
@Singleton
public class MessageListener implements MessageCreateListener {

    private final MusicService musicService;
    private final GuildPreferencesService guildPreferencesService;

    public MessageListener(MusicService musicService, GuildPreferencesService guildPreferencesService) {
        this.musicService = musicService;
        this.guildPreferencesService = guildPreferencesService;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        Message message = event.getMessage();
        User author = message.getUserAuthor().orElse(null);
        String content = message.getContent();

        if(author == null || author.isYourself() || content.length() > 4_000) return;

        Optional<Server> server = message.getServer();
        Optional<ServerTextChannel> serverTextChannel = message.getServerTextChannel();

        if(serverTextChannel.isPresent() && server.isPresent()) {
            Server guild = server.get();
            ServerTextChannel textChannel = serverTextChannel.get();

            Optional<GuildPreferences> preferences = guildPreferencesService.findById(guild.getIdAsString());
            if(preferences.isEmpty()) {
                log.info("[{}] no guild preference configured for this guild", guild.getName());
                return;
            }
            if(textChannel.getIdAsString().equals(preferences.get().getMusicChannelId())) {
                log.info("[{}] {}: {}", guild.getName(), author.getDisplayName(guild), content);
                musicService.handle(event.getApi(), message);
            }
        }
    }

}
