package com.github.amadarain.itunes.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.github.amadarain.itunes.ITunesLibrary;
import com.github.amadarain.itunes.Track;
import com.github.amadarain.itunes.Playlist;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParserState extends DefaultHandler {
    private ITunesLibrary.ITunesLibraryBuilder libBuilder;
    private Stack<State> states;
    private Map<Integer, Track> tracks;

    public ParserState(ITunesLibrary.ITunesLibraryBuilder builder) {
        this.libBuilder = builder;
        this.states = new Stack<>();
        this.states.push(new Init());
        this.tracks = new HashMap<>();
    }

    @Override
    public void startElement(String uri, String name, String qName, Attributes atts) {
        states.peek().start(name);
    }
    @Override
    public void endElement(String uri, String name, String qName) {
        states.peek().end(name);
    }
    @Override
    public void characters(char[] ch, int start, int length) {
        states.peek().text(new String(ch, start, length));
    }

    private interface State {
        void start(String name);
        void end(String name);
        void text(String str);
    }
    private class StateBase implements State {
        public void start(String name) {}
        public void end(String name) {}
        public void text(String str) {}

        protected void push(State state) { states.push(state); }
        protected void pop() { states.pop(); }

        protected String currentKey = null;
        protected void setCurrentKey(String key) { currentKey = key; }
        protected void storeKey() { consumeText(this::setCurrentKey); }

        protected void consumeText(Consumer<String> consumer) {
            push(new ConsumeValue(consumer));
        }
    }

    private class Init extends StateBase {
        @Override
        public void start(String name) {
            if (name.equals("plist")) {
                push(new Plist());
            }
        }
    }
    private class Plist extends StateBase {
        @Override
        public void start(String name) {
            log.debug("Plist: start " + name);
            if (name.equals("key")) {
                consumeText(it -> {
                    switch (it) {
                        case "Tracks": push(new Tracks()); break;
                        case "Playlists": push(new Playlists()); break;
                        default:
                    }
                });
            }
        }
    }
    private class Tracks extends StateBase {
        @Override
        public void start(String name) {
            log.debug("Tracks: start " + name);
            if (name.equals("key")) {
                push(new BuildTrack());
            }
        }
        @Override public void end(String name) {
            if (name.equals("dict")) {
                libBuilder.addTracks(new ArrayList<>(tracks.values()));
                pop();
            }
        }
    }
    private class BuildTrack extends StateBase {
        Track.TrackBuilder builder = Track.builder();
        Integer trackId;
        @Override
        public void start(String name) {
            switch (name) {
                case "key":
                    storeKey();
                    break;
                case "integer":
                case "date":
                case "string":
                    switch (currentKey) {
                        case "Track ID":
                            consumeText(it -> { trackId = Integer.valueOf(it); }); break;
                        case "Size":
                            consumeText(it -> builder.size(Integer.valueOf(it))); break;
                        case "Total Time":
                            consumeText(it -> builder.totalTime(Integer.valueOf(it))); break;
                        case "Disc Number":
                            consumeText(it -> builder.discNumber(Integer.valueOf(it))); break;
                        case "Disc Count":
                            consumeText(it -> builder.discCount(Integer.valueOf(it))); break;
                        case "Track Number":
                            consumeText(it -> builder.trackNumber(Integer.valueOf(it))); break;
                        case "Bit Rate":
                            consumeText(it -> builder.bitRate(Integer.valueOf(it))); break;
                        case "Sample Rate":
                            consumeText(it -> builder.sampleRate(Integer.valueOf(it))); break;

                        case "Persistent ID": consumeText(builder::persistentId); break;
                        case "Name": consumeText(builder::name); break;
                        case "Artist": consumeText(builder::artist); break;
                        case "Album Artist": consumeText(builder::albumArtist); break;
                        case "Composer": consumeText(builder::composer); break;
                        case "Album": consumeText(builder::album); break;
                        case "Location": consumeText(builder::location); break;

                        case "dict":
                        case "array":

                        default: setCurrentKey(null);
                    }
                    break;
                default:
            }
        }
        @Override public void end(String name) {
            if (name.equals("dict")) {
                Track track = builder.build();
                tracks.put(trackId, track);
                pop();
            }
        }
    }
    private class Playlists extends StateBase {
        @Override
        public void start(String name) {
            log.debug("Playlists: start " + name);
            if (name.equals("dict")) {
                push(new BuildPlaylist());
            }
        }
        @Override public void end(String name) {
            if (name.equals("array")) {
                pop();
            }
        }
    }
    private class BuildPlaylist extends StateBase {
        Playlist.PlaylistBuilder builder = Playlist.builder();
        @Override
        public void start(String name) {
            log.debug("BuildPlaylist: start " + name);
            switch (name) {
                case "key":
                    storeKey();
                    break;
                case "integer":
                case "date":
                case "string":
                    switch (currentKey) {
                        case "Playlist Persistent ID":
                            consumeText(builder::persistentId); break;
                        case "Name": consumeText(builder::name); break;

                        default:
                    }
                    break;
                case "array": push(new AddTrack(builder)); break;

                case "dict": break;

                default:
            }
        }
        @Override public void end(String name) {
            if (name.equals("dict")) {
                Playlist p = builder.build();
                libBuilder.add(p);
                pop();
            }
        }
    }
    private class AddTrack extends StateBase {
        Playlist.PlaylistBuilder builder;
        List<Track> listTracks = new ArrayList<>();
        public AddTrack(Playlist.PlaylistBuilder builder) {
            this.builder = builder;
        }
        @Override
        public void start(String name) {
            log.debug("AddTrack: start " + name);
            if (name.equals("integer")) {
                consumeText(it -> listTracks.add(tracks.get(Integer.valueOf(it))));
            }
        }
        @Override public void end(String name) {
            if (name.equals("array")) {
                builder.tracks(listTracks);
                pop();
            }
        }
    }
    private class ConsumeValue extends StateBase {
        StringBuilder sb = new StringBuilder();
        Consumer<String> consumer;
        public ConsumeValue(Consumer<String> consumer) { this.consumer = consumer; }
        @Override
        public void start(String name) {
            throw new IllegalStateException();
        }
        @Override public void end(String name) {
            pop();
            String value = sb.toString();
            log.debug("ConsumeValue: end " + name + ", value: " + value);
            consumer.accept(value);
        }
        @Override public void text(String str) { sb.append(str); }
    }
}

