package com.github.am4dr.itunes

import java.nio.file.Paths

import spock.lang.*

class RealityTest extends Specification {
    @Ignore
    def "parse real file"() {
        def path = Paths.get(System.getProperty('user.home'), 'Music', 'iTunes', 'iTunes Music Library.xml')
        def lib = ITunesLibrary.parse(path)
        expect:
        lib
        lib.tracks
        lib.playlists
    }
}

