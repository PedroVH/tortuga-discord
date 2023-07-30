package com.pedrovh.tortuga.discord.guild.service;

import com.pedrovh.tortuga.discord.dao.DAO;
import com.pedrovh.tortuga.discord.guild.model.GuildPreferences;
import io.micronaut.core.util.StringUtils;
import jakarta.inject.Singleton;

import java.util.Optional;

@Singleton
public class GuildPreferencesService {

    private final DAO<GuildPreferences, String> dao = new DAO<>(GuildPreferences.class);

    public boolean exists(String id) {
        return dao.exists(id);
    }

    public Optional<GuildPreferences> findById(String id) {
        return Optional.ofNullable(dao.findById(id));
    }

    public void save(GuildPreferences guildPreferences) {
        if(StringUtils.isEmpty(guildPreferences.getGuildId()))
            throw new IllegalArgumentException("guildId is not set!");

        if(dao.exists(guildPreferences.getGuildId()))
            dao.save(guildPreferences);
        else
            dao.insert(guildPreferences);
    }

    public void remove(GuildPreferences guildPreferences) {
        dao.remove(guildPreferences);
    }

}
