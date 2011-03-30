package ynot.util.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;

public class FileSystemManager {

    private File currentDirectory;
    private String fileSeparator;

    public FileSystemManager() {
        Properties p = System.getProperties();
        currentDirectory = new File(p.getProperty("user.home"));
        fileSeparator = p.getProperty("file.separator");
    }

    public void cd(String onePath) throws FileNotFoundException {
        String dirPath = onePath;
        if (".".equals(dirPath)) {
            dirPath = currentDirectory.getAbsolutePath();
        }
        if ("..".equals(dirPath)) {
            dirPath = currentDirectory.getParentFile().getAbsolutePath();
        }
        cd(new File(dirPath));
    }

    public void cd(final File oneFile) throws FileNotFoundException {
        File dirFile = oneFile;
        if (dirFile == null) {
            throw new FileNotFoundException("The path is null");
        }
        if (!dirFile.isAbsolute()) {
            dirFile = new File(currentDirectory.getAbsolutePath()
                    + fileSeparator + dirFile.getPath());
        }
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            throw new FileNotFoundException(
                    "Path doesn't exist or is not a directory :"
                            + dirFile.getPath());
        }
        currentDirectory = dirFile;
    }
    
    public void ls() {
        File[] listOfFiles = currentDirectory.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
          if (listOfFiles[i].isFile()) {
            System.out.println("(f) " + listOfFiles[i].getName());
          } else if (listOfFiles[i].isDirectory()) {
            System.out.println("(d) " + listOfFiles[i].getName());
          }
        }
    }
}
