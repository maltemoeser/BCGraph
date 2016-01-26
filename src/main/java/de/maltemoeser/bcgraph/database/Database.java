package de.maltemoeser.bcgraph.database;

import org.neo4j.graphdb.GraphDatabaseService;

public interface Database {

    GraphDatabaseService getGraphDatabaseService();

    void initialize();

    void shutdown();

    Integer getLastInsertedBlockHeight();
}
