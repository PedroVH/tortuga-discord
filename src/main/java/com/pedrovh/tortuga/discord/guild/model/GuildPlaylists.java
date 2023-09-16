package com.pedrovh.tortuga.discord.guild.model;

import com.pedrovh.tortuga.discord.playlist.model.Playlist;
import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = GuildPlaylists.COLLECTION, schemaVersion = "1.0")
public class GuildPlaylists {

    public static final String COLLECTION = "GUILD_PLAYLISTS";

    @Id
    private String guildId;
    private List<Playlist> playlists;

}
