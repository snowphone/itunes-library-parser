# iTunes Library Parser

## Usage
### Gradle
```groovy
repositories {
    jcenter()
    maven { url "https://jitpack.io" }
}

dependencies {
    // see about [version] https://jitpack.io/#amadarain/itunes-library-parser
    compile 'com.github.amadarain:itunes-library-parser:[version]'
}
```
### Groovy example
```groovy
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
```
## License
MIT

