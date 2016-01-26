package de.maltemoeser.bcgraph.blockchain;

import com.google.inject.Inject;
import de.maltemoeser.bcgraph.config.ApplicationConfig;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.PrunedException;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.MemoryFullPrunedBlockStore;
import org.bitcoinj.utils.BlockFileLoader;

/**
 * Constructs a blockchain and stores it in memory.
 * Mainly used for testing.
 */
public class MemoryBlockChainBuilder extends AbstractBlockChainBuilder {

    @Inject
    public MemoryBlockChainBuilder(ApplicationConfig _applicationConfig) {
        this.applicationConfig = _applicationConfig;
        initializeBlockChain();
        initializeBlockFileLoader();
    }

    @Override
    protected void initializeBlockChain() {
        try {
            store = new MemoryFullPrunedBlockStore(applicationConfig.getNetworkParameters(), 2000);
            chain = new BlockChain(applicationConfig.getNetworkParameters(), store);
        } catch (BlockStoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initializeBlockFileLoader() {
        blockFileLoader = new BlockFileLoader(
                applicationConfig.getNetworkParameters(),
                applicationConfig.getBlockFiles()
        );
    }

    @Override
    public void buildChain() {
        try {
            addBlocksToChain();
            skipTopmostBlocks(SKIP_BLOCKS);
            constructSetOfValidHashes();
            store.close();
        } catch (VerificationException | BlockStoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void addBlocksToChain() {
        for (Block block : blockFileLoader) {
            try {
                chain.add(block);
            } catch (PrunedException e) {
                e.printStackTrace();
            }
        }
    }
}
