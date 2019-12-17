package com.ziorange.files.organizer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pziogas
 */
public class FilesOrganizer {

    private String exportPath;
    private Map<String, String> monthsMapping = new HashMap<>();
    private static final String YEAR = "Year_";
    private static final String MONTH = "Month_";

    public FilesOrganizer(String exportPath) {
        this.exportPath = exportPath;
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

        try {
            System.out.println("Copying " + filePath.toString() + " -->" + Paths.get(fileMigrationDirectoryPathIncludingRoot.concat(filePath.getFileName().toString())).toString());
            Files.copy(filePath, Paths.get(fileMigrationDirectoryPathIncludingRoot.concat(filePath.getFileName().toString())));
        } catch (IOException ex) {
            Logger.getLogger(FilesOrganizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getFileMigrationPath(final Path filePath) {
        try {
            FileTime creationTime = (FileTime) Files.getAttribute(filePath, "creationTime");
            String fileCreationTime = creationTime.toString();
            String[] splitedCreationDate = fileCreationTime.split("-");
            return YEAR.concat(splitedCreationDate[0])
                    .concat(File.separator)
                    .concat(MONTH)
                    .concat(monthsMapping.get(splitedCreationDate[1]))
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

}
