package de.maltemoeser.bcgraph.blockchain;

import com.google.inject.Inject;
import org.bitcoinj.core.Block;

/**
 * Provides access to valid blocks in correct order and the necessary blockchain information
 * in order to import all blocks into Neo4j.
 */
public class BlockProvider {

    private SimplifiedBlockChain simplifiedBlockChain;
    private BlockLoader blockLoader;
    private int startHeight = 0;

    @Inject
    public BlockProvider(BlockChainBuilder blockChainBuilder, BlockLoader blockLoader) {
        blockChainBuilder.buildChain();
        simplifiedBlockChain = blockChainBuilder.getSimplifiedValidBlockChain();
        this.blockLoader = blockLoader;
    }

    public void initialize(int startHeight) {
        this.startHeight = startHeight;
        initialize();
    }

    public void initialize() {
        blockLoader.initialize(simplifiedBlockChain, this.startHeight);
    }

    public int getHeightForHash(String hash) {
        return simplifiedBlockChain.getHeightForHash(hash);
    }

    public Iterable<Block> getBlocks() {
        return blockLoader;
    }
}
