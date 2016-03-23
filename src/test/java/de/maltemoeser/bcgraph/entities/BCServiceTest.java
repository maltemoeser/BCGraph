package de.maltemoeser.bcgraph.entities;

import de.maltemoeser.bcgraph.testing.Neo4jTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BCServiceTest extends Neo4jTest {

    BCEntityService entityService;
    BCBlockService blockService;
    BCTransactionService transactionService;
    BCAddressService addressService;

    @Before
    public void setUpServices() {
        this.entityService = injector.getInstance(BCEntityService.class);
        this.blockService = injector.getInstance(BCBlockService.class);
        this.transactionService = injector.getInstance(BCTransactionService.class);
        this.addressService = injector.getInstance(BCAddressService.class);
    }

    @Test
    public void testBCEntityService() {
        try (org.neo4j.graphdb.Transaction tx = graphDatabaseService.beginTx()) {
            BCBlock block = entityService.getNewBlock();
            assertTrue(block.isBlock());

            BCTransaction transaction = entityService.getNewTransaction();
            assertTrue(transaction.isTransaction());

            BCAddress address = entityService.getNewAddress();
            assertTrue(address.isAddress());

            BCOutput output = entityService.getNewOutput();
            // this must fail because an output is determined by its relationship
            assertFalse(output.isOutput());
            transaction.addOutput(output);
            // now it should succeed
            assertTrue(output.isOutput());
        }
    }

    @Test
    public void testBCBlockService() {
        try (org.neo4j.graphdb.Transaction tx = graphDatabaseService.beginTx()) {
            final String BLOCKHASH = "myBlock";
            BCBlock block0 = blockService.createBlock(BLOCKHASH, 0);
            BCBlock block1 = blockService.createBlock(BLOCKHASH + "2");
            block1.connectToPreviousBlock(block0);
            block1.setHeight(1);

            assertEquals(block0, blockService.getBlockByHash(BLOCKHASH));
            assertEquals(block0, blockService.getBlockByHeight(0));
            assertEquals(block1, blockService.getBlockByHash(BLOCKHASH + "2"));
            assertEquals(block1, blockService.getBlockByHeight(1));

            block1.setLatestBlock(true);

            assertEquals(block1, blockService.getLastInsertedBlock());
        }
    }

    @Test
    public void testBCTransactionService() {
        try (org.neo4j.graphdb.Transaction tx = graphDatabaseService.beginTx()) {
            final String TXHASH = "myTransaction";
            BCTransaction transaction = transactionService.createTransaction(TXHASH);
            assertEquals(transaction, transactionService.getTransactionByHash(TXHASH));

            final String BLOCKHASH1 = "myBlock1";
            final String BLOCKHASH2 = "myBlock2";

            BCTransaction transaction2 = transactionService.createTransaction(TXHASH);
            BCBlock block1 = blockService.createBlock(BLOCKHASH1);
            BCBlock block2 = blockService.createBlock(BLOCKHASH2);

            block1.addTransaction(transaction);
            block2.addTransaction(transaction2);

            assertEquals(transaction2, transactionService.getTransactionByHashAndBlockHash(TXHASH, BLOCKHASH2));
            assertEquals(transaction, transactionService.getTransactionByHashAndBlockHash(TXHASH, BLOCKHASH1));
        }
    }

    @Test
    public void testBCAddressService() {
        try (org.neo4j.graphdb.Transaction tx = graphDatabaseService.beginTx()) {
            final String ADDRESSHASH = "myAddress";
            BCAddress address0 = addressService.getOrCreateAddress(ADDRESSHASH);
            BCAddress address1 = addressService.getOrCreateAddress(ADDRESSHASH + "2");
            BCAddress address2 = addressService.getOrCreateAddress(ADDRESSHASH);
            assertNotEquals(address0, address1);
            assertEquals(address0, address2);

            assertNotNull(addressService.getAddress(ADDRESSHASH));
            assertNull(addressService.getAddress(ADDRESSHASH + "-IS-NULL"));
        }
    }
}
