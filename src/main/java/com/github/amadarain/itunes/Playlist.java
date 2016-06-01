package com.github.amadarain.itunes;

import java.util.Collections;
import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@Builder
public class Playlist {
    private final String persistentId;
    private final String name;
    private final List<Track> tracks;

    public String getPersistentId() { return persistentId; }
    public String getName() { return name; }
    public List<Track> getTracks() { return Collections.unmodifiableList(tracks); }
}

