package de.maltemoeser.bcgraph.testing;

import com.google.inject.Inject;
import de.maltemoeser.bcgraph.database.Database;
import de.maltemoeser.bcgraph.entities.BCBlock;
import de.maltemoeser.bcgraph.entities.BCBlockService;
import de.maltemoeser.bcgraph.entities.BCTransaction;
import de.maltemoeser.bcgraph.entities.BCTransactionService;
import org.neo4j.graphdb.GraphDatabaseService;

public class TestGraphUtils {

    GraphDatabaseService graphDatabaseService;
    BCTransactionService transactionService;
    BCBlockService blockService;
    private int counter = 0;

    @Inject
    public TestGraphUtils(Database database, BCBlockService blockService, BCTransactionService transactionService) {
        this.graphDatabaseService = database.getGraphDatabaseService();
        this.transactionService = transactionService;
        this.blockService = blockService;
    }

    public BCTransaction createTransactionAtHeight(int height) {
        return transactionService.createTransaction("tx-" + counter++ + "-"  + height);
    }

    public BCBlock createBlockAtHeight(int height) {
        return blockService.createBlock("block-" + counter++ + "-" + height, height);
    }

    public BlockTransactionPair createTransactionAndBlockAtHeight(int height) {
        BCBlock block = createBlockAtHeight(height);
        BCTransaction transaction = createTransactionAtHeight(height);
        block.addTransaction(transaction);
        return new BlockTransactionPair(block, transaction);
    }
}
