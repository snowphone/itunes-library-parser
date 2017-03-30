package com.github.am4dr.itunes;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Playlist {
    private final String persistentId;
    private final String name;
    private final List<Track> tracks;

    @Builder
    public Playlist(String persistentId, String name,
                    @Singular List<Track> tracks) {
        this.persistentId = persistentId;
        this.name = name;
        this.tracks =
            tracks != null
                ? new ArrayList<>(tracks)
                : new ArrayList<>(0);
    }
    public String getPersistentId() { return persistentId; }
    public String getName() { return name; }
    public List<Track> getTracks() {
        return Collections.unmodifiableList(
            tracks != null ? tracks : new ArrayList<>(0));
    }
}

