package com.github.amadarain.itunes;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.nio.file.Path;

import com.github.amadarain.itunes.parser.Parser;

public class ITunesLibrary {
    private final Map<Integer, Track> tracks;
    private final Map<Integer, Playlist> playlists;

    public ITunesLibrary(Map<Integer, Track> tracks, Map<Integer, Playlist> playlists) {
        this.tracks = Collections.unmodifiableMap(tracks);
        this.playlists = Collections.unmodifiableMap(playlists);
    }

    public static ITunesLibrary parse(Path path) {
        Parser parser = new Parser();
        return parser.parse(path);
    }

    public static ITunesLibraryBuilder builder() {
        return new ITunesLibraryBuilder();
    }
    public static class ITunesLibraryBuilder {
        private Map<Integer, Track> tracks;
        private Map<Integer, Playlist> playlists;

        public ITunesLibraryBuilder() {
            tracks = new HashMap<>();
            playlists = new HashMap<>();
        }

        public ITunesLibraryBuilder addTracks(Map<Integer, Track> tracks) {
            this.tracks.putAll(tracks);
            return this;
        }
        public ITunesLibraryBuilder addPlaylists(Map<Integer, Playlist> playlits) {
            this.playlists.putAll(playlists);
            return this;
        }
        public ITunesLibraryBuilder add(Track track) {
            tracks.put(track.getTrackId(), track);
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

    public Map<Integer, Track> getTracks() {
        return Collections.unmodifiableMap(tracks);
    }
    public Map<Integer, Playlist> getPlaylists() {
        return Collections.unmodifiableMap(playlists);
    }
}

