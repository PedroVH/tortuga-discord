package com.pedrovh.tortuga.discord.model.user.playlist;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Track implements Serializable {

    private String name;
    private String url;

    public Track(AudioTrackInfo info) {
        this.name = info.title;
        this.url = info.uri;
    }

}
