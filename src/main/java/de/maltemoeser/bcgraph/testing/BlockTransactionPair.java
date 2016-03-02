package de.maltemoeser.bcgraph.testing;

import de.maltemoeser.bcgraph.entities.BCBlock;
import de.maltemoeser.bcgraph.entities.BCTransaction;


public class BlockTransactionPair {

    protected BCBlock block;
    protected BCTransaction transaction;

    public BlockTransactionPair(BCBlock block, BCTransaction transaction) {
        this.block = block;
        this.transaction = transaction;
    }

    public BCBlock getBlock() {
        return block;
    }

    public void setBlock(BCBlock block) {
        this.block = block;
    }

    public BCTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(BCTransaction transaction) {
        this.transaction = transaction;
    }
}
