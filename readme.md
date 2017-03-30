# iTunes Library Parser

requirements: JDK 8

## Build

    gradle build

## Test

    gradle test

## Install to local Maven repository

    gradle install

## Usage
Install to local Maven repository or use [jitpack.io](https://jitpack.io/#am4dr/itunes-library-parser)

### Gradle
```groovy
repositories {
    jcenter()
    maven { url "https://jitpack.io" }
}

dependencies {
    // see https://jitpack.io/#am4dr/itunes-library-parser
    compile 'com.github.am4dr:itunes-library-parser:[version]'
}
```

### Groovy script
```groovy
@Grapes([
    @GrabResolver(name='jitpack.io', root='https://jitpack.io'),
    @Grab('com.github.am4dr:itunes-library-parser:[version]'),
])
import com.github.am4dr.itunes.ITunesLibrary
import java.nio.file.Paths

def path = Paths.get(System.getProperty('user.home'), 'Music', 'iTunes', 'iTunes Music Library.xml')
def lib = ITunesLibrary.parse(path)

// print the names of all playlists
lib.playlists.each { println it.name }
```

## License
MIT

