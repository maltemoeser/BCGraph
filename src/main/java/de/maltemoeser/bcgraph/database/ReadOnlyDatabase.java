package de.maltemoeser.bcgraph.database;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.maltemoeser.bcgraph.config.ApplicationConfig;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;

import java.io.File;

/**
 * Similar to the ProductionDatabase, but does not allow to modify the underlying data.
 * Should be used during all analyses to prevent accidentally modifying the database.
 */
@Singleton
public class ReadOnlyDatabase extends Neo4jDatabase implements Database {

    @Inject
    public ReadOnlyDatabase(ApplicationConfig properties) {
        super.properties = properties;
        initialize();
        createIndexesAndUniqueConstraints();
        waitUntilIndexesAreOnline();
    }

    public void initialize() {
        initializeInDirectory(properties.getDatabaseDirectory());
    }

    private void initializeInDirectory(String directory) {
        LOGGER.info("Initializing Database.");
        graphDatabase = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder(new File(directory))
                .setConfig(GraphDatabaseSettings.keep_logical_logs, "false")
                .setConfig(GraphDatabaseSettings.read_only, "true")
                .newGraphDatabase();
        registerShutdownHook(graphDatabase);
    }
}
