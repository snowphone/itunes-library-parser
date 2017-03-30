package com.github.am4dr.itunes.parser;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;

import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;

import com.github.am4dr.itunes.ITunesLibrary;


public class Parser {

    public ITunesLibrary parse(Path path) {
        ITunesLibrary lib = null;
        try (Reader r = new InputStreamReader(new FileInputStream(path.toFile()), "utf-8")) {
            lib = parse(r);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return lib;
    }
    public ITunesLibrary parse(Reader reader) {
        ITunesLibrary.ITunesLibraryBuilder builder = ITunesLibrary.builder();
        try {
            XMLReader xmlr = XMLReaderFactory.createXMLReader();
            xmlr.setContentHandler(new ParserState(builder));
            xmlr.parse(new InputSource(reader));
        }
        catch (org.xml.sax.SAXException | IOException e) {
            e.printStackTrace();
        }
        return builder.build();
    }
}

