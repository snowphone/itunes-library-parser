package com.github.amadarain.itunes.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;

import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.github.amadarain.itunes.ITunesLibrary;
import com.github.amadarain.itunes.Track;
import com.github.amadarain.itunes.Playlist;


public class Parser {
    private Stack<State> states;
    private Map<Integer, Track> tracks;
    private Map<Integer, Playlist> playlists;

    public Parser() {
        initialize();
    }
    public void initialize() {
        states = new Stack<>();
        states.push(INIT());
        tracks = new HashMap<>();
        playlists = new HashMap<>();
    }

    public ITunesLibrary parse(Path path) {
        ITunesLibrary lib = null;
        try (Reader r = new FileReader(path.toFile())) {
            lib = parse(r);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return lib;
    }
    public ITunesLibrary parse(Reader reader) {
        try {
            XMLReader xmlr = XMLReaderFactory.createXMLReader();
            xmlr.setContentHandler(new ITunesLibXmlContentHandler());
            xmlr.parse(new InputSource(reader));
        }
        catch (org.xml.sax.SAXException | IOException e) {
            e.printStackTrace();
        }
        return moveToLibrary();
    }
    private ITunesLibrary moveToLibrary() {
        Map<Integer, Track> t = tracks;
        Map<Integer, Playlist> p = playlists;
        initialize();
        return new ITunesLibrary(t, p);
    }

    private abstract class State {
        protected void check(String expected, String actual) {
            if (!expected.equals(actual)) {
                String message = "the start tag and the end tag are not matched.\n"
                        + "start tag: `" + expected + "`, end tag: `" + actual + "`";
                throw new IllegalStateException(message);
            }
        }

        protected void push(State state) { states.push(state); }
        protected void pop() { states.pop(); }

        abstract void start(String name);
        abstract void end(String name);
        abstract void text(String str);

        protected String currentKey = null;
        protected void setCurrentKey(String key) { currentKey = key; }
        protected void storeKey() { consumeNextValue(this::setCurrentKey); }

        protected void consumeNextValue(Consumer<String> consumer) {
            push(new ConsumeValue(consumer));
        }
    }

    private State INIT() { return new Init(); }
    private State PLIST() { return new Plist(); }
    private State TRACKS() { return new Tracks(); }
    private State BUILD_TRACK() { return new BuildTrack(); }
    private State PLAYLISTS() { return new Playlists(); }
    private State BUILD_PLAYLIST() { return new BuildPlaylist(); }

    private class Init extends State {
        @Override
        void start(String name) {
            if (name.equals("plist")) {
                push(PLIST());
            }
        }
        @Override void end(String name) {}
        @Override void text(String str) {}
    }
    private class Plist extends State {
        @Override
        void start(String name) {
            System.out.println("Plist: start " + name);
            if (name.equals("key")) {
                consumeNextValue(it -> {
                    switch (it) {
                        case "Tracks": push(TRACKS()); break;
                        case "Playlists": push(PLAYLISTS()); break;
                        default:
                    }
                });
            }
        }
        @Override void end(String name) {}
        @Override void text(String str) {}
    }
    private class Tracks extends State {
        @Override
        void start(String name) {
            System.out.println("Tracks: start " + name);
            if (name.equals("key")) {
                push(BUILD_TRACK());
            }
        }
        @Override void end(String name) {
            if (name.equals("dict")) {
                pop();
            }
        }
        @Override void text(String str) {}
    }
    private class BuildTrack extends State {
        Track.TrackBuilder builder = Track.builder();
        @Override
        void start(String name) {
            switch (name) {
                case "key":
                    storeKey();
                    break;
                case "integer":
                case "date":
                case "string":
                    switch (currentKey) {
                        case "Track ID":
                            consumeNextValue(it -> builder.trackId(Integer.valueOf(it))); break;
                        case "Size":
                            consumeNextValue(it -> builder.size(Integer.valueOf(it))); break;
                        case "Total Time":
                            consumeNextValue(it -> builder.totalTime(Integer.valueOf(it))); break;
                        case "Disc Number":
                            consumeNextValue(it -> builder.discNumber(Integer.valueOf(it))); break;
                        case "Disc Count":
                            consumeNextValue(it -> builder.discCount(Integer.valueOf(it))); break;
                        case "Track Number":
                            consumeNextValue(it -> builder.trackNumber(Integer.valueOf(it))); break;
                        case "Bit Rate":
                            consumeNextValue(it -> builder.bitRate(Integer.valueOf(it))); break;
                        case "Sample Rate":
                            consumeNextValue(it -> builder.sampleRate(Integer.valueOf(it))); break;

                        case "Persistent ID": consumeNextValue(builder::persistentId); break;
                        case "Name": consumeNextValue(builder::name); break;
                        case "Artist": consumeNextValue(builder::artist); break;
                        case "Album Artist": consumeNextValue(builder::albumArtist); break;
                        case "Composer": consumeNextValue(builder::composer); break;
                        case "Album": consumeNextValue(builder::album); break;
                        case "Location": consumeNextValue(builder::location); break;

                        case "dict":
                        case "array":

                        default: setCurrentKey(null);
                    }
                    break;
                default:
            }
        }
        @Override void end(String name) {
            if (name.equals("dict")) {
                Track t = builder.build();
                tracks.put(t.getTrackId(), t);
                pop();
            }
        }
        @Override void text(String str) {}
    }
    private class Playlists extends State {
        @Override
        void start(String name) {
            System.out.println("Playlists: start " + name);
            if (name.equals("dict")) {
                push(BUILD_PLAYLIST());
            }
        }
        @Override void end(String name) {
            if (name.equals("array")) {
                pop();
            }
        }
        @Override void text(String str) {}
    }
    private class BuildPlaylist extends State {
        Playlist.PlaylistBuilder builder = Playlist.builder();
        @Override
        void start(String name) {
            System.out.println("BuildPlaylist: start " + name);
            switch (name) {
                case "key":
                    storeKey();
                    break;
                case "integer":
                case "date":
                case "string":
                    switch (currentKey) {
                        case "Playlist ID":
                            consumeNextValue(it -> builder.playlistId(Integer.valueOf(it))); break;
                        case "Playlist Persistent ID":
                            consumeNextValue(builder::persistentId); break;
                        case "Name": consumeNextValue(builder::name); break;

                        default:
                    }
                    break;
                case "array": push(new AddTrack(builder)); break;

                case "dict": break;

                default:
            }
        }
        @Override void end(String name) {
            if (name.equals("dict")) {
                Playlist p = builder.build();
                playlists.put(p.getPlaylistId(), p);
                pop();
            }
        }
        @Override void text(String str) {}
    }
    private class AddTrack extends State {
        Playlist.PlaylistBuilder builder;
        List<Track> t = new ArrayList<>();
        public AddTrack(Playlist.PlaylistBuilder builder) {
            this.builder = builder;
        }
        @Override
        void start(String name) {
            System.out.println("AddTrack: start " + name);
            if (name.equals("integer")) {
                consumeNextValue(it ->
                    t.add(tracks.get(Integer.valueOf(it)))
                );
            }
        }
        @Override void end(String name) {
            if (name.equals("array")) {
                builder.tracks(t);
                pop();
            }
        }
        @Override void text(String str) {}
    }
    private class ConsumeValue extends State {
        StringBuilder sb = new StringBuilder();
        Consumer<String> consumer;
        public ConsumeValue(Consumer<String> consumer) { this.consumer = consumer; }
        @Override
        void start(String name) {
            throw new IllegalStateException();
        }
        @Override void end(String name) {
            pop();
            String value = sb.toString();
            System.out.println("ConsumeValue: end " + name + ", value: " + value);
            consumer.accept(value);
        }
        @Override void text(String str) { sb.append(str); }
    }

    private class ITunesLibXmlContentHandler extends DefaultHandler {
        private final Parser parser = Parser.this;
        private State state() { return parser.states.peek(); }
        @Override
        public void startElement(String uri, String name, String qName, Attributes atts) {
            state().start(name);
        }
        @Override
        public void endElement(String uri, String name, String qName) {
            state().end(name);
        }
        @Override
        public void characters(char[] ch, int start, int length) {
            state().text(new String(ch, start, length));
        }
    }
}

