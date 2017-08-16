package de.maltemoeser.bcgraph.database;

import de.maltemoeser.bcgraph.config.ApplicationConfig;
import de.maltemoeser.bcgraph.constants.LabelType;
import de.maltemoeser.bcgraph.entities.BCBlock;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.helpers.collection.Iterators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

/**
 * Provides functionality needed for both production and test databases.
 */
public abstract class Neo4jDatabase {

    protected GraphDatabaseService graphDatabase;

    protected static final Logger LOGGER = LoggerFactory.getLogger(Neo4jDatabase.class);

    protected ApplicationConfig properties;

    public GraphDatabaseService getGraphDatabaseService() {
        return graphDatabase;
    }

    /**
     * Defines constraints for the graph property model.
     * Creates indexes for blocks and transactions.
     */
    protected void createIndexesAndUniqueConstraints() {
        try (Transaction tx = graphDatabase.beginTx()) {
            Schema schema = graphDatabase.schema();

            if (!schema.getIndexes().iterator().hasNext()) {
                LOGGER.debug("Creating Indexes and Unique Constraints.");

                schema.constraintFor(LabelType.Block)
                        .assertPropertyIsUnique("hash")
                        .create();

                schema.constraintFor(LabelType.Block)
                        .assertPropertyIsUnique("height")
                        .create();

                // No unique constraints for transactions (see BIP30)
                schema.indexFor(LabelType.Transaction)
                        .on("hash")
                        .create();

                schema.constraintFor(LabelType.Address)
                        .assertPropertyIsUnique("hash")
                        .create();

                schema.indexFor(LabelType.Coinbase)
                        .on("hash")
                        .create();

                schema.indexFor(LabelType.LatestBlock)
                        .on("height")
                        .create();
            }
            tx.success();
        }
    }

    protected void waitUntilIndexesAreOnline() {
        LOGGER.debug("Waiting for Indizes to Come Online.");
        try (Transaction ignored = graphDatabase.beginTx()) {
            graphDatabase.schema().awaitIndexesOnline(120, TimeUnit.SECONDS);
            LOGGER.debug("Indizes are online");
        }
    }

    public void shutdown() {
        LOGGER.info("Shutting down database...");
        graphDatabase.shutdown();
    }

    protected void registerShutdownHook(final GraphDatabaseService graphDatabase) {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDatabase.shutdown();
            }
        });
    }

    @Nullable
    public Integer getLastInsertedBlockHeight() {
        Integer lastInsertedBlockHeight = null;
        try (Transaction ignored = graphDatabase.beginTx()) {
            Node block = Iterators.singleOrNull(graphDatabase.findNodes(LabelType.LatestBlock));
            if (block != null) {
                lastInsertedBlockHeight = new BCBlock(block).getHeight();
            }
        }
        return lastInsertedBlockHeight;
    }
}
