package de.maltemoeser.bcgraph.blockchain;

import org.bitcoinj.core.BlockChain;

public interface BlockChainBuilder {

    void buildChain();

    BlockChain getChain();

    SimplifiedBlockChain getSimplifiedValidBlockChain();
}
