# iTunes Library Parser

## Usage
### Gradle
    allprojects {
        repositories {
            ...
            maven { url "https://jitpack.io" }
        }
    }

    dependencies {
        compile 'com.github.amadarain:itunes-library-parser:0.1.0'
    }

### Groovy example

    @Grapes([
        @GrabResolver(name='jitpack.io', root='https://jitpack.io'),
        @Grab('com.github.amadarain:itunes-library-parser:master'),
    ])
    import com.github.amadarain.itunes.ITunesLibrary
    import java.nio.file.Paths

    def path = Paths.get(System.getProperty('user.home'), 'Music', 'iTunes', 'iTunes Music Library.xml')
    def lib = ITunesLibrary.parse(path)

    // print the names of all playlists
    lib.playlists.each { println it.name }

## License
MIT

