package com.github.amadarain.itunes;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.amadarain.itunes.parser.Parser;

public class ITunesLibrary {
    private final List<Track> tracks;
    private final List<Playlist> playlists;

    public ITunesLibrary(List<Track> tracks, List<Playlist> playlists) {
        this.tracks = Collections.unmodifiableList(
            tracks != null ? tracks : new ArrayList<>(0));
        this.playlists = Collections.unmodifiableList(
            playlists != null ? playlists : new ArrayList<>(0));
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

    public static ITunesLibraryBuilder builder() {
        return new ITunesLibraryBuilder();
    }
    public static class ITunesLibraryBuilder {
        private List<Track> tracks;
        private List<Playlist> playlists;

        public ITunesLibraryBuilder() {
            tracks = new ArrayList<>();
            playlists = new ArrayList<>();
        }

        public ITunesLibraryBuilder addTracks(List<Track> tracks) {
            this.tracks.addAll(tracks);
            return this;
        }
        public ITunesLibraryBuilder addPlaylists(List<Playlist> playlits) {
            this.playlists.addAll(playlists);
            return this;
        }
        public ITunesLibraryBuilder add(Track track) {
            tracks.add(track);
            return this;
        }
        public ITunesLibraryBuilder add(Playlist playlist) {
            playlists.add(playlist);
            return this;
        }
        public ITunesLibrary build() {
            return new ITunesLibrary(tracks, playlists);
        }
    }
}

