package de.maltemoeser.bcgraph.blockchain;

import com.google.inject.Inject;
import de.maltemoeser.bcgraph.config.ApplicationConfig;
import org.bitcoinj.core.Block;
import org.bitcoinj.utils.BlockFileLoader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Processes the block files of the Bitcoin Core client and serves them in correct order.
 */
public class CoreBlockLoader extends BlockLoader {

    private Iterator<Block> blockIterator;
    private Map<String, Block> tempBlockStore = new HashMap<>();
    private SimplifiedBlockChain simplifiedBlockChain;
    private final ApplicationConfig config;
    private int heightOfNextBlock = 0;

    @Inject
    public CoreBlockLoader(ApplicationConfig _properties) {
        this.config = _properties;
    }

    @Override
    public void initialize(SimplifiedBlockChain simplifiedBlockChain, int startHeight) {
        this.simplifiedBlockChain = simplifiedBlockChain;
        this.heightOfNextBlock = startHeight;
        int offset = AbstractBlockChainBuilder.getOffset(startHeight);
        blockIterator = new BlockFileLoader(
                config.getNetworkParameters(),
                config.getBlockFilesWithOffset(offset)
        ).iterator();
    }

    @Override
    public boolean hasNext() {
        return heightOfNextBlock < simplifiedBlockChain.getNumberOfBlocks();
    }

    @Override
    public Block next() throws NoSuchElementException {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }

        // Get the hash of the block that should come next
        String expectedHash = simplifiedBlockChain.getHashForHeight(heightOfNextBlock);

        // Get the block and increase our height counter
        Block next = _next(expectedHash);
        heightOfNextBlock++;
        return next;
    }

    protected Block _next(String expectedHash) {
        if (tempBlockStore.containsKey(expectedHash)) {
            // Remove the block from the cache
            return tempBlockStore.remove(expectedHash);
        }

        while (true) {
            Block next = getNextValidBlock();
            String nextHash = next.getHashAsString();
            if (next.getHashAsString().equals(expectedHash)) {
                return next;
            } else if (simplifiedBlockChain.getHeightForHash(nextHash) > heightOfNextBlock) {
                tempBlockStore.put(next.getHashAsString(), next);
            }
        }
    }

    protected Block getNextValidBlock() {
        Block next = blockIterator.next();
        // if block is an orphan, find the next valid block
        while (!simplifiedBlockChain.isValidBlock(next.getHashAsString())) {
            next = blockIterator.next();
        }
        return next;
    }

    @Override
    public Iterator<Block> iterator() {
        return this;
    }
}
