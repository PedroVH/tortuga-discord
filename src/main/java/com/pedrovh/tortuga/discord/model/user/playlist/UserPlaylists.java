package com.pedrovh.tortuga.discord.model.user.playlist;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = UserPlaylists.COLLECTION, schemaVersion = "1.1")
public class UserPlaylists implements Serializable {

    public static final String COLLECTION = "USER_PLAYLISTS";

    @Id
    private String userId;
    private List<Playlist> playlists;

}
