package com.github.amadarain.itunes;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.amadarain.itunes.parser.Parser;

public class ITunesLibrary {
    private final List<Track> tracks;
    private final Map<Integer, Playlist> playlists;

    public ITunesLibrary(List<Track> tracks, Map<Integer, Playlist> playlists) {
        this.tracks = Collections.unmodifiableList(tracks);
        this.playlists = Collections.unmodifiableMap(playlists);
    }

    public static ITunesLibrary parse(Path path) {
        Parser parser = new Parser();
        return parser.parse(path);
    }

    public List<Track> getTracks() {
        return Collections.unmodifiableList(tracks);
    }
    public Map<Integer, Playlist> getPlaylists() {
        return Collections.unmodifiableMap(playlists);
    }

    public static ITunesLibraryBuilder builder() {
        return new ITunesLibraryBuilder();
    }
    public static class ITunesLibraryBuilder {
        private List<Track> tracks;
        private Map<Integer, Playlist> playlists;

        public ITunesLibraryBuilder() {
            tracks = new ArrayList<>();
            playlists = new HashMap<>();
        }

        public ITunesLibraryBuilder addTracks(List<Track> tracks) {
            this.tracks.addAll(tracks);
            return this;
        }
        public ITunesLibraryBuilder addPlaylists(Map<Integer, Playlist> playlits) {
            this.playlists.putAll(playlists);
            return this;
        }
        public ITunesLibraryBuilder add(Track track) {
            tracks.add(track);
            return this;
        }
        public ITunesLibraryBuilder add(Playlist playlist) {
            playlists.put(playlist.getPlaylistId(), playlist);
            return this;
        }
        public ITunesLibrary build() {
            return new ITunesLibrary(tracks, playlists);
        }
    }
}

