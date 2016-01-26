package de.maltemoeser.bcgraph.blockchain;

import de.maltemoeser.bcgraph.testing.TestInjector;
import de.maltemoeser.bcgraph.config.ApplicationConfig;
import org.bitcoinj.store.BlockStoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class DiskBlockChainTest extends TestInjector {

    @Test
    public void testDiskBlockChain() {

        DiskBlockChainBuilder validBlockChain = injector.getInstance(DiskBlockChainBuilder.class);
        assertEquals(0, validBlockChain.getChain().getBestChainHeight());

        validBlockChain.addBlocksToChain();

        assertEquals(525, validBlockChain.getChain().getBestChainHeight());

        try {
            validBlockChain.skipTopmostBlocks(10);
        } catch (BlockStoreException e) {
            e.printStackTrace();
        }

        assertEquals(515, validBlockChain.getChainHead().getHeight());

        try {
            validBlockChain.constructSetOfValidHashes();
        } catch (BlockStoreException e) {
            e.printStackTrace();
        }

        SimplifiedBlockChain simplifiedBlockChain = validBlockChain.getSimplifiedValidBlockChain();

        assertEquals(0, simplifiedBlockChain.getHeightForHash("000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f"));
        assertEquals("000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f", simplifiedBlockChain.getHashForHeight(0));

        assertEquals(2, simplifiedBlockChain.getHeightForHash("000000006a625f06636b8bb6ac7b960a8d03705d1ace08b1a19da3fdcc99ddbd"));
        assertEquals("000000006a625f06636b8bb6ac7b960a8d03705d1ace08b1a19da3fdcc99ddbd", simplifiedBlockChain.getHashForHeight(2));

        assertEquals(10, simplifiedBlockChain.getHeightForHash("000000002c05cc2e78923c34df87fd108b22221ac6076c18f3ade378a4d915e9"));
        assertEquals("000000002c05cc2e78923c34df87fd108b22221ac6076c18f3ade378a4d915e9", simplifiedBlockChain.getHashForHeight(10));
    }

    @Before
    @After
    public void destroyDirectory() {
        ApplicationConfig config = injector.getInstance(ApplicationConfig.class);
        try {
            org.apache.commons.io.FileUtils.deleteDirectory(new File(config.getBlockStoreDirectory()));
        } catch (Exception e) {
            //
        }
    }
}
