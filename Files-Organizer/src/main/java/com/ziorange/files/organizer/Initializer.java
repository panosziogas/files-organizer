package com.ziorange.files.organizer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pziogas
 */
public class Initializer {

    private static final String ERROR_MESSAGE_READING_PROPERTIES = "Provide a valid location of the .properties file";
    private static final String READ_PATH_KEY = "read_path";
    private static final String EXPORT_PATH_KEY = "export_path";
    private static final String IMAGE_FILES_EXTENSIONS_KEY = "image_files_extensions";
    private static final String VIDEO_FILES_EXTENSIONS_KEY = "video_files_extension";
    private static String importPath;
    private static String exportPath;
    private static Set<String> imageExtensions;
    private static Set<String> videoExtensions;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, ERROR_MESSAGE_READING_PROPERTIES);
            System.exit(-1);
        }
        String propertiesLocation = args[0];
        if (propertiesLocation == null || propertiesLocation.isEmpty()) {
            System.err.println(ERROR_MESSAGE_READING_PROPERTIES);
            Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, ERROR_MESSAGE_READING_PROPERTIES);
            System.exit(-1);
        }
        //Read properties file
        try (InputStream input = new FileInputStream(propertiesLocation)) {
            Properties prop = new Properties();
            prop.load(input);
            if (!isConfigurationValid(prop)) {
                System.exit(-1);
            }
            initiateProcedure(prop);
        } catch (IOException ex) {
            Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }

    private static void initiateProcedure(Properties prop) {
        String imageExtensionString = prop.getProperty(IMAGE_FILES_EXTENSIONS_KEY);
        if (imageExtensionString != null && !imageExtensionString.isEmpty()) {
            System.out.println("Looking for files with image extensions " + imageExtensionString + " at  " + importPath);
            imageExtensions = new HashSet(Arrays.asList(imageExtensionString.split(",")));
        }
        String videoExtensionString = prop.getProperty(VIDEO_FILES_EXTENSIONS_KEY);
        if (videoExtensionString != null && !videoExtensionString.isEmpty()) {
            System.out.println("Looking for files with video extensions " + videoExtensionString + " at  " + importPath);
            videoExtensions = new HashSet(Arrays.asList(videoExtensionString.split(",")));
        }
        DiskAnalyzer analyzer = new DiskAnalyzer(importPath,imageExtensions, videoExtensions);
        try {
            Set<Path> foundResults = analyzer.scan();
            FilesOrganizer filerOrganizer = new FilesOrganizer(exportPath);
            for (Path pathFound : foundResults) {               
                filerOrganizer.migrateFile(pathFound);
            }
        } catch (IOException ex) {
            Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static boolean isConfigurationValid(Properties prop) {
        importPath = prop.getProperty(READ_PATH_KEY);
        exportPath = prop.getProperty(EXPORT_PATH_KEY);

        if (importPath == null || importPath.isEmpty()) {
            System.err.println("Import location was not defined in property file");
            return false;
        }
        if (exportPath == null || exportPath.isEmpty()) {
            System.err.println("Export location was not defined in property file");
            return false;
        }
        return true;
    }

}
