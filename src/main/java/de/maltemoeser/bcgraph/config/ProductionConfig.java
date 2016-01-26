package de.maltemoeser.bcgraph.config;

import com.google.inject.Singleton;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Access to configuration values for production.
 * Always use this, except for testing.
 */
@Singleton
public class ProductionConfig extends ApplicationConfig {

    @Override
    protected String getPropertyFileName() {
        return "production.properties";
    }

    @Override
    public List<File> getBlockFiles() {
        return getBlockFilesWithOffset(0);
    }

    @Override
    public List<File> getBlockFilesWithOffset(int offset) {
        List<File> files = new LinkedList<>();
        for (int i = offset; true; i++) {
            File file = new File(getBlockFileDirectory() + String.format("blk%05d.dat", i));
            if (!file.exists())
                return files;
            files.add(file);
        }
    }
}
