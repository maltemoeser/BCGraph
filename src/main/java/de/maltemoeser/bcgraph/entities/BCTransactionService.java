package de.maltemoeser.bcgraph.entities;

import de.maltemoeser.bcgraph.constants.LabelType;
import de.maltemoeser.bcgraph.constants.NodeProperty;
import org.bitcoinj.core.TransactionOutPoint;
import org.neo4j.graphdb.Node;
import org.neo4j.helpers.collection.IteratorUtil;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class BCTransactionService extends BCEntityService {

    public BCTransaction createTransaction(String transactionHash) {
        BCTransaction bcTransaction = getNewTransaction();
        bcTransaction.setHash(transactionHash);
        return bcTransaction;
    }

    public BCTransaction getTransactionByHash(String transactionHash) {
        Node result = IteratorUtil.single(
                graphDatabaseService.findNodes(LabelType.Transaction, NodeProperty.TRANSACTION_HASH, transactionHash)
        );
        return new BCTransaction(result);
    }

    /**
     * Allows to identify a transaction based on its hash and the block's hash in which it was included in the blockchain.
     * This can be necessary because transaction IDs are not unique (see BIP30).
     */
    public BCTransaction getTransactionByHashAndBlockHash(String transactionHash, String blockHash) {
        List<Node> nodes = IteratorUtil.asList(graphDatabaseService.findNodes(LabelType.Transaction, NodeProperty.TRANSACTION_HASH, transactionHash));
        if(nodes.size() == 0) {
            throw new NoSuchElementException("No transaction found with hash " + transactionHash);
        } else if(nodes.size() == 1) {
            return new BCTransaction(nodes.get(0));
        } else {
            for(Node n : nodes) {
                BCTransaction transaction = new BCTransaction(n);
                if(transaction.getBlock().getHash().equals(blockHash)) {
                    return transaction;
                }
            }
            throw new NoSuchElementException("No transaction found with hash " + transactionHash + " and block hash " + blockHash);
        }
    }

    public BCTransaction getTransactionByTransactionOutPoint(TransactionOutPoint outPoint) {
        return getTransactionByHash(outPoint.getHash().toString());
    }
}
