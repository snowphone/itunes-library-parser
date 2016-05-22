package com.github.amadarain.itunes.parser;

import spock.lang.*

class ParserSpec extends Specification {
    Parser parser
    def setup() {
        parser = new Parser()
    }

    def "no tracks and no playlists"() {
        def xml = createReader(
"""<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple Computer//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>Major Version</key><integer>1</integer>
    <key>Minor Version</key><integer>1</integer>
    <key>Application Version</key><string>12.3.2.35</string>
    <key>Date</key><date>2016-02-10T16:05:37Z</date>
    <key>Features</key><integer>5</integer>
    <key>Show Content Ratings</key><true/>
    <key>Library Persistent ID</key><string>7FE190A91827057C</string>
    <key>Tracks</key><dict>
    </dict>
    <key>Playlists</key><array>
    </array>
</dict>
</plist>
""")

        expect:
        parser.parse(xml)

    }
    def "only one track"() {
        def xml = createReader(
"""<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple Computer//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>Major Version</key><integer>1</integer>
    <key>Minor Version</key><integer>1</integer>
    <key>Application Version</key><string>12.3.2.35</string>
    <key>Date</key><date>2016-02-10T16:05:37Z</date>
    <key>Features</key><integer>5</integer>
    <key>Show Content Ratings</key><true/>
    <key>Library Persistent ID</key><string>7FE190A91827057C</string>
    <key>Tracks</key><dict>
        <key>1234</key><dict>
            <key>Track ID</key><integer>1234</integer>
            <key>Size</key><integer>3506304</integer>
            <key>Total Time</key><integer>146050</integer>
            <key>Disc Number</key><integer>1</integer>
            <key>Disc Count</key><integer>2</integer>
            <key>Track Number</key><integer>1</integer>
            <key>Bit Rate</key><integer>192</integer>
            <key>Sample Rate</key><integer>44100</integer>
            <key>Persistent ID</key><string>B0220DB6D9DC9032</string>
            <key>Name</key><string>Name</string>
            <key>Artist</key><string>Artist</string>
            <key>Album Artist</key><string>Album Artist</string>
            <key>Composer</key><string>Composer</string>
            <key>Album</key><string>Album</string>
            <key>Location</key><string>path to file</string>
        </dict>
    </dict>
    <key>Playlists</key><array>
    </array>
</dict>
</plist>
""")
        def lib = parser.parse(xml)
        def track = lib.tracks[1234]
        expect:
        lib.tracks.size() == 1
        track
        track.trackId == 1234
        track.size == 3506304
        track.totalTime == 146050
        track.discNumber == 1
        track.discCount == 2
        track.trackNumber == 1
        track.bitRate == 192
        track.sampleRate == 44100
        track.persistentId == "B0220DB6D9DC9032"
        track.name == "Name"
        track.artist == "Artist"
        track.albumArtist == "Album Artist"
        track.composer == "Composer"
        track.album == "Album"
        track.location == "path to file"
    }
    def "playlist"() {
        def xml = createReader(
"""<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple Computer//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>Major Version</key><integer>1</integer>
    <key>Minor Version</key><integer>1</integer>
    <key>Application Version</key><string>12.3.2.35</string>
    <key>Date</key><date>2016-02-10T16:05:37Z</date>
    <key>Features</key><integer>5</integer>
    <key>Show Content Ratings</key><true/>
    <key>Library Persistent ID</key><string>7FE190A91827057C</string>
    <key>Tracks</key><dict>
        <key>1234</key><dict>
            <key>Track ID</key><integer>1234</integer>
            <key>Name</key><string>Track 1234</string>
        </dict>
        <key>1235</key><dict>
            <key>Track ID</key><integer>1235</integer>
            <key>Name</key><string>Track 1235</string>
        </dict>
        <key>1236</key><dict>
            <key>Track ID</key><integer>1236</integer>
            <key>Name</key><string>Track 1236</string>
        </dict>
    </dict>
    <key>Playlists</key><array>
        <dict>
            <key>Playlist ID</key><integer>10</integer>
            <key>Name</key><string>name</string>
            <key>Playlist Items</key><array>
                <dict>
                    <key>Track ID</key><integer>1234</integer>
                </dict>
                <dict>
                    <key>Track ID</key><integer>1236</integer>
                </dict>
            </array>
        </dict>
    </array>
</dict>
</plist>
""")
        def lib = parser.parse(xml)
        expect:
        lib
        lib.playlists[10]
        lib.playlists[10].tracks
        lib.playlists[10].tracks.size() == 2
        lib.playlists[10].tracks.find { it.name == 'Track 1234' }
    }
    Reader createReader(String str) { new StringReader(str) }
} 
