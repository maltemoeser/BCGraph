package de.maltemoeser.bcgraph.config;

import com.google.inject.Singleton;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Access to configuration values for testing.
 */
@Singleton
public class TestConfig extends ApplicationConfig {

    @Override
    String getPropertyFileName() {
        return "test.properties";
    }

    @Override
    public List<File> getBlockFiles() {
        List<File> files = new LinkedList<>();
        URL resource = TestConfig.class.getClassLoader().getResource("blk00000.dat");
        File file = new File(resource.getFile());
        files.add(file);
        return files;
    }

    @Override
    public List<File> getBlockFilesWithOffset(int offset) {
        return getBlockFiles();
    }
}
