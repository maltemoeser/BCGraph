package de.maltemoeser.bcgraph.importer;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import de.maltemoeser.bcgraph.blockchain.BlockProvider;
import de.maltemoeser.bcgraph.database.Database;
import de.maltemoeser.bcgraph.entities.BCBlock;
import de.maltemoeser.bcgraph.entities.BCTransaction;
import de.maltemoeser.bcgraph.injector.AppInjector;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.Transaction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockChainImporter {

    private int lastInsertedBlockHeight;

    private final Provider<BlockImporter> blockImporterProvider;
    private final Provider<TransactionImporter> transactionImporterProvider;

    BlockProvider blockProvider;
    protected static final Logger LOGGER = LoggerFactory.getLogger(BlockChainImporter.class);
    Database database;
    GraphDatabaseService graphDatabaseService;

    @Inject
    public BlockChainImporter(Database database,
                              BlockProvider blockProvider,
                              Provider<BlockImporter> blockImporterProvider,
                              Provider<TransactionImporter> transactionImporterProvider) {
        this.database = database;
        this.graphDatabaseService = database.getGraphDatabaseService();
        this.blockProvider = blockProvider;
        this.blockImporterProvider = blockImporterProvider;
        this.transactionImporterProvider = transactionImporterProvider;
    }

    public void importBlockChain() {
        loadLastInsertedBlockHeight();
        blockProvider.initialize(lastInsertedBlockHeight + 1);
        parseBlocks();
    }

    /**
     * Retrieves the height of the last inserted block.
     * If there is none, sets the lastInsertedBlockHeight to -1
     */
    protected void loadLastInsertedBlockHeight() {
        Integer lastInserted = database.getLastInsertedBlockHeight();
        if (lastInserted == null) {
            lastInsertedBlockHeight = -1;
        } else {
            lastInsertedBlockHeight = lastInserted;
        }
    }

    protected int getLastInsertedBlockHeight() {
        return lastInsertedBlockHeight;
    }

    /**
     * Iterate over all blocks in our block files.
     */
    protected void parseBlocks() {
        for (Block block : blockProvider.getBlocks()) {
            parseIfNotAlreadyInserted(block);
        }
    }

    /**
     * Ensures that the block has not already been inserted by comparing its height with the height of the last inserted block.
     * Note, that we do not consider reorganisations.
     * Our assumption is that chains will not be reorganized by more than the security offset set in BlockChainBuilder.
     *
     * @param block
     */
    private void parseIfNotAlreadyInserted(Block block) {
        String blockHash = block.getHashAsString();
        if (blockProvider.getHeightForHash(blockHash) > lastInsertedBlockHeight) {
            parseBlock(block);
        }
    }


    private void parseBlock(Block bitcoinBlock) {
        try (org.neo4j.graphdb.Transaction databaseTransaction = graphDatabaseService.beginTx()) {
            BlockImporter blockImporter = blockImporterProvider.get();
            BCBlock bcBlock = blockImporter.importBlock(bitcoinBlock);

            importTransactions(bitcoinBlock, bcBlock);

            databaseTransaction.success();
            LOGGER.info("Added block at height " + bcBlock.getHeight());
        }
    }

    /**
     * Iterates over all transactions in a block to import them.
     * @param bitcoinBlock is the original block object
     * @param bcBlock is the node in the database representing the block
     */
    private void importTransactions(Block bitcoinBlock, BCBlock bcBlock) {
        for (Transaction tx : bitcoinBlock.getTransactions()) {
            importTransaction(tx, bcBlock);
        }
    }

    /**
     * Imports a single transaction into the graph database.
     * @param bitcoinTransaction is the original bitcoin transaction
     * @param bcBlock is the block node that will be connected to the transaction
     */
    private void importTransaction(Transaction bitcoinTransaction, BCBlock bcBlock) {
        TransactionImporter transactionImporter = transactionImporterProvider.get();
        BCTransaction bcTransaction = transactionImporter.importTransaction(bitcoinTransaction);
        bcBlock.addTransaction(bcTransaction);
    }

    /**
     * Run the blockchain importer.
     */
    public static void main(String args[]) {
        Injector injector = Guice.createInjector(new AppInjector());
        BlockChainImporter parser = injector.getInstance(BlockChainImporter.class);
        parser.importBlockChain();
    }
}
