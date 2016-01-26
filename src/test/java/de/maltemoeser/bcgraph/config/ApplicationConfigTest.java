package de.maltemoeser.bcgraph.config;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ApplicationConfigTest {

    @Test
    public void testAddSlashToString() {
        String withoutSlash = "abcd";
        String withSlash = "bcde/";

        assertTrue("abcd/".equals(ApplicationConfig.appendSlashToString(withoutSlash)));
        assertTrue("bcde/".equals(ApplicationConfig.appendSlashToString(withSlash)));
    }
}
