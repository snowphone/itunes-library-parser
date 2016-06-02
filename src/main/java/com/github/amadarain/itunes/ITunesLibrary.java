package com.github.amadarain.itunes;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.amadarain.itunes.parser.Parser;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class ITunesLibrary {
    private final List<Track> tracks;
    private final List<Playlist> playlists;

    @Builder
    public ITunesLibrary(@Singular List<Track> tracks,
                         @Singular List<Playlist> playlists) {
        this.tracks =
            tracks != null
                ? new ArrayList<>(tracks)
                : new ArrayList<>(0);
        this.playlists =
            playlists != null
                ? new ArrayList<>(playlists)
                : new ArrayList<>(0);
    }

    public static ITunesLibrary parse(Path path) {
        Parser parser = new Parser();
        return parser.parse(path);
    }

    public List<Track> getTracks() {
        return Collections.unmodifiableList(tracks);
    }
    public List<Playlist> getPlaylists() {
        return Collections.unmodifiableList(playlists);
    }
}

