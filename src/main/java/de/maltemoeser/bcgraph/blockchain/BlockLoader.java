package de.maltemoeser.bcgraph.blockchain;

import org.bitcoinj.core.Block;

import java.util.Iterator;

/**
 * BlockLoader simplifies dealing with the Bitcoin Core block files.
 * It provides an iterator over all blocks that ensures that no stale blocks are included
 * and that blocks are in correct order.
 */
public abstract class BlockLoader implements Iterator<Block>, Iterable<Block> {

    public abstract void initialize(SimplifiedBlockChain simplifiedBlockChain, int startHeight);

}
