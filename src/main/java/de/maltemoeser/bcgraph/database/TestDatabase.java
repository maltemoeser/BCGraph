package de.maltemoeser.bcgraph.database;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.maltemoeser.bcgraph.config.ApplicationConfig;
import org.neo4j.test.TestGraphDatabaseFactory;

/**
 * Data written to the TestDatabase is not persisted to disk.
 * Useful for testing.
 */
@Singleton
public class TestDatabase extends Neo4jDatabase implements Database {

    @Inject
    public TestDatabase(ApplicationConfig properties) {
        super.properties = properties;
        initialize();
        createIndexesAndUniqueConstraints();
        waitUntilIndexesAreOnline();
    }

    @Override
    public void initialize() {
        LOGGER.debug("Initializing Test Database.");
        graphDatabase = new TestGraphDatabaseFactory().newImpermanentDatabase();
        super.registerShutdownHook(graphDatabase);
    }
}
