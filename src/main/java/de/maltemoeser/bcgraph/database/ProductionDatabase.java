package de.maltemoeser.bcgraph.database;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.maltemoeser.bcgraph.config.ApplicationConfig;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;

import java.io.File;

/**
 * Full access to the production database.
 * Used to import the data into Neo4j.
 */
@Singleton
public class ProductionDatabase extends Neo4jDatabase implements Database {

    @Inject
    public ProductionDatabase(ApplicationConfig properties) {
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
                .newGraphDatabase();
        registerShutdownHook(graphDatabase);
    }
}
