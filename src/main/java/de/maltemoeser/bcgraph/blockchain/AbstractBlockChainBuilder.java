package de.maltemoeser.bcgraph.blockchain;

import de.maltemoeser.bcgraph.config.ApplicationConfig;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.utils.BlockFileLoader;

import java.util.*;

/**
 * Superclass for constructing a blockchain that provides common logic independet of the underlying store.
 */
public abstract class AbstractBlockChainBuilder implements BlockChainBuilder {

    protected ApplicationConfig applicationConfig;
    protected BlockStore store;
    protected BlockChain chain;
    protected BlockFileLoader blockFileLoader;

    protected StoredBlock chainHead;

    protected final int SKIP_BLOCKS = 36;

    protected Map<String, Integer> blockHashToHeight = new HashMap<>();
    protected List<String> blockHashesInCorrectOrder = new ArrayList<>();

    abstract void initializeBlockChain();

    abstract void initializeBlockFileLoader();

    abstract void addBlocksToChain();

    /**
     * Because we have no reorganization logic in place, we skip a number of blocks to be safe
     *
     * @param skip offset from the chain head
     * @throws BlockStoreException
     */
    protected void skipTopmostBlocks(int skip) throws BlockStoreException {
        chainHead = chain.getChainHead();
        for (int i = 0; i < skip; i++) {
            chainHead = chainHead.getPrev(store);
        }
    }

    /**
     * Extracts the hashes and heights of valid blocks.
     *
     * @throws BlockStoreException
     */
    protected void constructSetOfValidHashes() throws BlockStoreException {
        while (true) {
            String blockHash = chainHead.getHeader().getHashAsString();
            int height = chainHead.getHeight();
            blockHashToHeight.put(blockHash, height);
            blockHashesInCorrectOrder.add(blockHash);
            if (height == 0) { // we reached the end
                break;
            }
            chainHead = chainHead.getPrev(store);
        }
        Collections.reverse(blockHashesInCorrectOrder);
    }

    public SimplifiedBlockChain getSimplifiedValidBlockChain() {
        return new SimplifiedBlockChain(blockHashToHeight, blockHashesInCorrectOrder);
    }

    public BlockChain getChain() {
        return chain;
    }

    protected StoredBlock getChainHead() {
        return chainHead;
    }

    /**
     * A somewhat ugly solution to determine an offset for the list of block files.
     *
     * @param height, from which on we need to load the blocks from the block files
     * @return a rough offset for where in the list of block files to start
     */
    protected static int getOffset(int height) {
        if (height > 390000) return 390;
        if (height > 380000) return 350;
        if (height > 370000) return 310;
        if (height > 360000) return 270;
        if (height > 340000) return 180;
        if (height > 320000) return 140;
        if (height > 300000) return 120;
        if (height > 250000) return 50;
        if (height > 220000) return 20;
        return 0;
    }
}
