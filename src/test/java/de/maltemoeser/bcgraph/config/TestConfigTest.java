package de.maltemoeser.bcgraph.config;

import de.maltemoeser.bcgraph.testing.TestInjector;
import org.bitcoinj.params.MainNetParams;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class TestConfigTest extends TestInjector {

    ApplicationConfig config;

    @Test
    public void testTestConfig() {
        config = injector.getInstance(ApplicationConfig.class);

        assertTrue("test.properties".equals(config.getPropertyFileName()));
        assertTrue("/".equals(config.getBlockFileDirectory()));
        assertTrue("/".equals(config.getDatabaseDirectory()));
        assertEquals(MainNetParams.get(), config.getNetworkParameters());
    }
}
