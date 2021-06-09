package com.github.am4dr.itunes;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@Builder
public class Track {
    private final String persistentId;
    private final int size;
    private final int totalTime;
    private final int discNumber;
    private final int discCount;
    private final int trackNumber;
    private final int bitRate;
    private final int sampleRate;
    private final int playCount;
    private final int rating;
    private final String name;
    private final String artist;
    private final String albumArtist;
    private final String composer;
    private final String album;
    private final String location;
    private final String genre;

    public String getPersistentId() { return persistentId; }
    public int getSize() { return size; }
    public int getTotalTime() { return totalTime; }
    public int getDiscNumber() { return discNumber; }
    public int getDiscCount() { return discCount; }
    public int getTrackNumber() { return trackNumber; }
    public int getBitRate() { return bitRate; }
    public int getSampleRate() { return sampleRate; }
	public int getPlayCount() { return playCount; }
    public int getRating() { return rating; }
    public String getName() { return name; }
    public String getArtist() { return artist; }
    public String getAlbumArtist() { return albumArtist; }
    public String getComposer() { return composer; }
    public String getAlbum() { return album; }
    public String getLocation() { return location; }
    public String getGenre() { return genre; }
}

