package de.maltemoeser.bcgraph.testing;

import de.maltemoeser.bcgraph.constants.LabelType;
import de.maltemoeser.bcgraph.database.Database;
import de.maltemoeser.bcgraph.entities.BCAddress;
import de.maltemoeser.bcgraph.entities.BCBlock;
import de.maltemoeser.bcgraph.entities.BCOutput;
import de.maltemoeser.bcgraph.entities.BCTransaction;
import org.junit.After;
import org.junit.Before;
import org.neo4j.graphdb.GraphDatabaseService;

public class Neo4jTest extends TestInjector {

    protected Database testDatabase;
    public GraphDatabaseService graphDatabaseService;

    @Before
    public void setUpInjectorAndDatabase() {
        testDatabase = injector.getInstance(Database.class);
        testDatabase.initialize();
        graphDatabaseService = testDatabase.getGraphDatabaseService();
    }

    @After
    public void tearDown() {
        testDatabase.shutdown();
        injector = null;
    }

    protected BCBlock getNewBlock() {
        return new BCBlock(graphDatabaseService.createNode(LabelType.Block));
    }

    protected BCTransaction getNewTransaction() {
        return new BCTransaction(graphDatabaseService.createNode(LabelType.Transaction));
    }

    protected BCTransaction getNewTransactionAtHeight(int height) {
        BCTransaction transaction = getNewTransaction();
        BCBlock block = getNewBlock();
        block.setHeight(height);
        block.addTransaction(transaction);
        return transaction;
    }

    protected BCOutput getNewOutput() {
        return new BCOutput(graphDatabaseService.createNode());
    }

    protected BCOutput getNewOutput(int index, long value) {
        BCOutput output = getNewOutput();
        output.setIndex(index);
        output.setValue(value);
        return output;
    }

    protected BCOutput getNewOutput(int index, long value, String addressString) {
        BCOutput output = getNewOutput(index, value);
        BCAddress address = getNewAddress();
        address.setHash(addressString);
        output.connectToAddress(address);
        return output;
    }

    protected BCAddress getNewAddress() {
        return new BCAddress(graphDatabaseService.createNode(LabelType.Address));
    }
}
