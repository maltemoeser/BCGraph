package de.maltemoeser.bcgraph.importer;

import de.maltemoeser.bcgraph.entities.*;
import de.maltemoeser.bcgraph.testing.Neo4jTest;
import org.bitcoinj.core.Coin;
import org.junit.Test;

import static org.junit.Assert.*;

public class BlockChainImporterTest extends Neo4jTest {

    BCTransactionService transactionService;
    BCBlockService blockService;

    @Test
    public void testImportBlockChain() {
        BlockChainImporter blockChainImporter = injector.getInstance(BlockChainImporter.class);
        transactionService = injector.getInstance(BCTransactionService.class);
        blockService = injector.getInstance(BCBlockService.class);

        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            blockChainImporter.importBlockChain();

            BCBlock genesis = blockService.getBlockByHeight(0);
            assertEquals("000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f", genesis.getHash());
            assertEquals(0, genesis.getHeight());
            assertEquals(1, genesis.getNumberOfTransactions());

            BCBlock one = blockService.getBlockByHeight(1);
            assertEquals("00000000839a8e6886ab5951d76f411475428afc90947ee320161bbf18eb6048", one.getHash());
            assertEquals(genesis, one.getPreviousBlock());
            assertEquals(1, one.getNumberOfTransactions());

            BCBlock twoHundred = blockService.getBlockByHash("000000008f1a7008320c16b8402b7f11e82951f44ca2663caf6860ab2eeef320");
            assertEquals(200, twoHundred.getHeight());
            assertEquals(1, twoHundred.getNumberOfTransactions());

            BCTransaction threeHundredCoinbase = transactionService.getTransactionByHash("bc15f9dcbe637c187bb94247057b14637316613630126fc396c22e08b89006ea");
            assertTrue(threeHundredCoinbase.isCoinbase());
            assertEquals(300, threeHundredCoinbase.getBlock().getHeight());
            assertEquals(Coin.valueOf(50, 0).getValue(), threeHundredCoinbase.getValue());

            BCOutput output = threeHundredCoinbase.getOutputByIndex(0);
            assertEquals(Coin.valueOf(50, 0).getValue(), output.getValue());
            assertEquals(1, output.getNumberOfAddresses());
            assertTrue("1Pi8agZKamjLJxfeGRUpGWGQimb8N21Hig".equals(output.getSingleAddress().getHash()));

            assertNotNull(testDatabase.getLastInsertedBlockHeight());
            assertEquals(489, (int) testDatabase.getLastInsertedBlockHeight());

            blockChainImporter.loadLastInsertedBlockHeight();
            assertEquals(489, blockChainImporter.getLastInsertedBlockHeight());
        }
    }
}
