package de.maltemoeser.bcgraph.entities;

import com.google.inject.Inject;
import de.maltemoeser.bcgraph.constants.LabelType;
import de.maltemoeser.bcgraph.database.Database;
import org.neo4j.graphdb.GraphDatabaseService;

public class BCEntityService {

    Database database;
    GraphDatabaseService graphDatabaseService;

    @Inject
    public void setDatabase(Database database) {
        this.database = database;
        this.graphDatabaseService = database.getGraphDatabaseService();
    }

    protected BCBlock getNewBlock() {
        return new BCBlock(graphDatabaseService.createNode(LabelType.Block));
    }

    protected BCTransaction getNewTransaction() {
        return new BCTransaction(graphDatabaseService.createNode(LabelType.Transaction));
    }

    protected BCOutput getNewOutput() {
        return new BCOutput(graphDatabaseService.createNode());
    }

    protected BCAddress getNewAddress() {
        return new BCAddress(graphDatabaseService.createNode(LabelType.Address));
    }
}
