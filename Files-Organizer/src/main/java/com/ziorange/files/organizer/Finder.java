package com.ziorange.files.organizer;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import static java.nio.file.FileVisitResult.CONTINUE;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author pziogas
 */
public class Finder extends SimpleFileVisitor<Path> {

    private static int finalTotal = 0;

    private int numMatches = 0;
    private List<PathMatcher> matcherList = new ArrayList();
    private Set<Path> foundPaths = new HashSet();

    Finder(Set<String> patterns) {
        for (String pattern : patterns) {
            matcherList.add(FileSystems.getDefault()
                    .getPathMatcher("glob:" + "*."+pattern));
        }
    }

    // Compares the glob pattern against
    // the file or directory name.
    void find(Path file) {
        Path name = file.getFileName();
        for (PathMatcher matcher : matcherList) {
            if (name != null && matcher.matches(name)) {
                numMatches++;             
                foundPaths.add(file);
            }
        }
    }

    // Prints the total number of
    // matches to standard out.
    int done() {
        System.out.println("Matched: "
                + numMatches);
        finalTotal = finalTotal + numMatches;
        return finalTotal;
    }

    Set<Path> results() {
        return foundPaths;
    }

    // Invoke the pattern matching
    // method on each file.
    @Override
    public FileVisitResult visitFile(Path file,
            BasicFileAttributes attrs) {
        find(file);
        return CONTINUE;
    }

    // Invoke the pattern matching
    // method on each directory.
    @Override
    public FileVisitResult preVisitDirectory(Path dir,
            BasicFileAttributes attrs) {
        find(dir);
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file,
            IOException exc) {        
        return CONTINUE;
    }

}
