package com.ziorange.files.organizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author pziogas
 */
public class DiskAnalyzer {

    private final Set<String> imagePatterns;
    private final Set<String> videoPatterns;
    private final String scanPath;   

    public DiskAnalyzer(String scanPath,Set<String> imagePatterns, Set<String> videoPatterns) {
        this.imagePatterns = imagePatterns;
        this.videoPatterns = videoPatterns;
        this.scanPath = scanPath;        
    }

    public Set<Path> scan() throws IOException {
        Path startingDir = Paths.get(this.scanPath);
        startingDir.getFileName();
        Set<String> allPatterns = new HashSet<>();
        if (this.imagePatterns != null) {
            allPatterns.addAll(this.imagePatterns);
        }
        if (this.videoPatterns != null) {
            allPatterns.addAll(this.videoPatterns);
        }
        Finder finder = new Finder(allPatterns);
        Files.walkFileTree(startingDir, finder);
        Integer totalFound = finder.done();
        Set<Path> foundResults = finder.results();
        return foundResults;
    }

}
