package de.maltemoeser.bcgraph.importer;

import de.maltemoeser.bcgraph.entities.BCBlock;
import de.maltemoeser.bcgraph.testing.Neo4jTest;
import de.maltemoeser.bcgraph.testing.TestUtils;
import org.bitcoinj.core.Block;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BlockImporterTest extends Neo4jTest {

    BlockImporter blockImporter;
    Block bitcoinBlock;

    @Before
    public void createBlockImporter() {
        blockImporter = injector.getInstance(BlockImporter.class);
        bitcoinBlock = TestUtils.getTestBlock();
    }

    @Test
    public void testCreateNodeFromBlock() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCBlock block = blockImporter.createNodeFromBlock(bitcoinBlock);
            testBasicBlockProperties(block);
        }
    }

    @Test
    public void testImportBlock() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            // Setup previous block
            BCBlock prev = getNewBlock();
            prev.setHash("00000000000000000532cfe52810c20f46e446d05e1a3a2e31f65b34810a7c96");
            prev.setHeight(350009);
            prev.setLatestBlock(true);

            BCBlock block = blockImporter.importBlock(bitcoinBlock);

            testBasicBlockProperties(block);

            assertEquals(350010, block.getHeight());
            assertTrue(block.isLatestBlock());

            assertEquals(prev, block.getPreviousBlock());
            assertFalse(prev.isLatestBlock());
        }
    }

    public void testBasicBlockProperties(BCBlock block) {
        assertEquals("00000000000000000ec32843883a983fe86f8823b87480520e88261784eea941", block.getHash());
        assertEquals(1427759805L, block.getUnixTime());
    }
}
