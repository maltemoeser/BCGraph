package de.maltemoeser.bcgraph.database;

import de.maltemoeser.bcgraph.config.TestConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNull;

public class TestDatabaseTest {

    private Database testDatabase;

    @Before
    public void setUpDatabase() {
        testDatabase = new TestDatabase(new TestConfig());
        testDatabase.initialize();
    }

    @Test
    public void testLastInsertedBlockExists() {
        Integer lastInsertedBlockHeight = testDatabase.getLastInsertedBlockHeight();
        assertNull(lastInsertedBlockHeight);
    }

    @After
    public void destroyDatabase() {
        testDatabase.shutdown();
    }
}
