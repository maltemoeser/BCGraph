package de.maltemoeser.bcgraph.blockchain;

import com.google.inject.Inject;
import de.maltemoeser.bcgraph.config.ApplicationConfig;
import org.bitcoinj.core.*;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.LevelDBBlockStore;
import org.bitcoinj.utils.BlockFileLoader;

import java.io.File;

/**
 * Constructs a blockchain and stores it on disk using LevelDBBlockStore.
 */
public class DiskBlockChainBuilder extends AbstractBlockChainBuilder {

    @Inject
    public DiskBlockChainBuilder(ApplicationConfig _applicationConfig) {
        this.applicationConfig = _applicationConfig;
        initializeBlockChain();
        initializeBlockFileLoader();
    }

    /**
     * Initialize the Blockchain by opening a LevelDB block store
     */
    @Override
    protected void initializeBlockChain() {
        NetworkParameters params = applicationConfig.getNetworkParameters();
        try {
            store = new LevelDBBlockStore(applicationConfig.getContext(), new File(applicationConfig.getBlockStoreDirectory()));
            chain = new BlockChain(params, store);
        } catch (BlockStoreException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the BlockFileLoader that provides the blocks based on which the Blockchain will be constructed.
     * To speed up synchronization, the BlockFileLoader will only provide blocks above a certain height threshold.
     */
    @Override
    protected void initializeBlockFileLoader() {
        StoredBlock head = chain.getChainHead();
        int height = head.getHeight();
        int offset = getOffset(height);
        blockFileLoader = new BlockFileLoader(
                applicationConfig.getNetworkParameters(),
                applicationConfig.getBlockFilesWithOffset(offset)
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
                // Only add new blocks to the chain
                if (store.get(block.getHash()) == null) {
                    chain.add(block);
                }
            } catch (BlockStoreException | PrunedException e) {
                e.printStackTrace();
            }
        }
    }
}
