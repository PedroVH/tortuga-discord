package com.pedrovh.tortuga.discord.guild.model;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = GuildPreferences.COLLECTION, schemaVersion = "1.0")
public class GuildPreferences implements Serializable {

    public static final String COLLECTION = "GUILD_PREFERENCES";
    @Id
    private String guildId;
    private String musicChannelId;

}
