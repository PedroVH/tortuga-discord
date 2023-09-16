package com.pedrovh.tortuga.discord.playlist.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Playlist {

    private String name;
    private List<String> urls;

}
