package com.ziorange.files.organizer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author pziogas
 */
public class FilesOrganizer {

    private String exportPath;
    private final Set<String> imagePatterns;
    private final Set<String> videoPatterns;
    private final Map<String, String> monthsMapping = new HashMap<>();
    private final Integer migrateImageSizeRule;
    private static final String YEAR = "Year_";
    private static final String MONTH = "Month_";
    private static final String NO_EXTENSION = "NoExtensionFiles";
    private static final String IMAGES = "Images";
    private static final String VIDEOS = "Videos";

    public FilesOrganizer(String exportPath, Set<String> imagePatterns, Set<String> videoPatterns, Integer migrateImageSizeRule) {
        this.exportPath = exportPath;
        this.imagePatterns = imagePatterns;
        this.videoPatterns = videoPatterns;
        this.migrateImageSizeRule = migrateImageSizeRule;
        monthsMapping.put("01", "January");
        monthsMapping.put("02", "February");
        monthsMapping.put("03", "March");
        monthsMapping.put("04", "April");
        monthsMapping.put("05", "May");
        monthsMapping.put("06", "June");
        monthsMapping.put("07", "July");
        monthsMapping.put("08", "August");
        monthsMapping.put("09", "September");
        monthsMapping.put("10", "October");
        monthsMapping.put("11", "November");
        monthsMapping.put("12", "December");
    }

    public void migrateFile(final Path filePath) {
        String fileMigrationDirectoryPath = getFileMigrationPath(filePath);
        if (fileMigrationDirectoryPath == null || fileMigrationDirectoryPath.isEmpty()) {
            Logger.getLogger(FilesOrganizer.class.getName()).log(Level.SEVERE, "Couldnt migrate {0}", filePath.toString());
            return;
        }
        String fileMigrationDirectoryPathIncludingRoot = exportPath.concat(File.separator).concat(fileMigrationDirectoryPath);
        Path fileMigrationDirectoryPathIncludingRootPath = Paths.get(fileMigrationDirectoryPathIncludingRoot);
        createExportDirectoryIfNotExist(fileMigrationDirectoryPathIncludingRootPath);

        if (migrateImageSizeRule == null) {
            copyFiles(filePath, Paths.get(fileMigrationDirectoryPathIncludingRoot.concat(filePath.getFileName().toString())));
            return;
        }
        if (isImageRuleApplied(filePath)) {
            moveFiles(filePath, Paths.get(fileMigrationDirectoryPathIncludingRoot.concat(filePath.getFileName().toString())));
            return;
        }
    }

    private void copyFiles(final Path filePath, final Path destinationPath) {
        try {
            System.out.println("Copying " + filePath.toString() + " -->" + destinationPath.toString());
            Files.copy(filePath, destinationPath);
        } catch (IOException ex) {
            Logger.getLogger(FilesOrganizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     private void moveFiles(final Path filePath, final Path destinationPath) {
        try {
            System.out.println("Moving " + filePath.toString() + " -->" + destinationPath.toString());
            Files.move(filePath, destinationPath);
        } catch (IOException ex) {
            Logger.getLogger(FilesOrganizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getFileMigrationPath(final Path filePath) {
        try {
            //FileTime creationTime = (FileTime) Files.getAttribute(filePath, "creationTime");
            FileTime creationTime = Files.getLastModifiedTime(filePath);
            String fileCreationTime = creationTime.toString();
            String[] splitedCreationDate = fileCreationTime.split("-");
            return YEAR.concat(splitedCreationDate[0])
                    .concat(File.separator)
                    .concat(MONTH)
                    .concat(monthsMapping.get(splitedCreationDate[1]))
                    .concat(File.separator)
                    .concat(getFileType(filePath))
                    .concat(File.separator);
        } catch (IOException ex) {
            Logger.getLogger(FilesOrganizer.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private void createExportDirectoryIfNotExist(Path exportPathLocation) {
        if (!Files.exists(exportPathLocation)) {
            try {
                Files.createDirectories(exportPathLocation);
            } catch (IOException ioe) {
                Logger.getLogger(FilesOrganizer.class.getName()).log(Level.SEVERE, null, ioe);
            }
        }
    }

    private String getFileType(final Path filePath) {
        String fileName = filePath.getFileName().toString();
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return NO_EXTENSION; // empty extension
        }
        String fileExtension = fileName.substring(lastIndexOf).replace(".", "");

        if (imagePatterns.contains(fileExtension)) {
            return IMAGES;
        }
        if (videoPatterns.contains(fileExtension)) {
            return VIDEOS;
        }
        return fileExtension.toLowerCase();
    }

    private boolean isImageRuleApplied(final Path filePath) {
        try {
            BufferedImage bimg = ImageIO.read(new File(filePath.toString()));
            int width = bimg.getWidth();
            if (width < migrateImageSizeRule) {
                return true;
            }
        } catch (IOException ex) {
            Logger.getLogger(FilesOrganizer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return false;
    }

}
