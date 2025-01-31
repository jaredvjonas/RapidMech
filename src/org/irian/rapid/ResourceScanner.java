package org.irian.rapid;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResourceScanner {

    private final List<File> dirList = new ArrayList<>();

    public ResourceScanner(List<File> dirList) {
        for (var f : dirList) {
            if (f.isDirectory()) {
                this.dirList.add(f);
            }
            else {
                System.out.printf("Ignoring folder... [%s] is not a directory\n", f);
            }
        }
    }

    public File scanDirectories(String filename) {
        for (var dir : dirList) {
            File file = scanDirectory(dir, filename);
            if (file != null) return file;
        }

        System.out.printf("File not found: %s\n", filename);
        return null;
    }

    private File scanDirectory(File dir, String filename) {
        for (var file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                var f = scanDirectory(file, filename);
                if (f != null) return f;
            }
            if (filename.equals(file.getName())) {
                return file;
            }
        }
        return null;
    }
}
