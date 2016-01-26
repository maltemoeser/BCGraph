package de.maltemoeser.bcgraph.blockchain;

import java.util.List;
import java.util.Map;

/**
 * SimplifiedBlockChain is a convenience class that simplifies access to the list of block headers and heights.
 * It includes a list that maps block height to hashes, and a map that maps block hashes to heights.
 */
public class SimplifiedBlockChain {

    private final Map<String, Integer> blockHashToHeight;
    private final List<String> blockHashes;
    private final int numberOfBlocks;

    public SimplifiedBlockChain(Map<String, Integer> blockHashToHeight, List<String> blockHashes) {
        this.blockHashToHeight = blockHashToHeight;
        this.blockHashes = blockHashes;
        numberOfBlocks = this.blockHashes.size();
    }

    public int getHeightForHash(String blockHash) {
        return blockHashToHeight.get(blockHash);
    }

    public String getHashForHeight(int blockHeight) {
        return blockHashes.get(blockHeight);
    }

    public boolean isValidBlock(String hash) {
        return blockHashToHeight.containsKey(hash);
    }

    /**
     * Returns the number of blocks in the BlockChain
     * Note, that this does not correspond to the height of the chain tip, but is larger by 1 (due to block height 0)
     * @return the number of blocks in the blockchain
     */
    public int getNumberOfBlocks() {
        return numberOfBlocks;
    }
}
