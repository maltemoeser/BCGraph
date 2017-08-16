package de.maltemoeser.bcgraph.entities;

import de.maltemoeser.bcgraph.constants.LabelType;
import de.maltemoeser.bcgraph.constants.NodeProperty;
import org.bitcoinj.core.TransactionOutPoint;
import org.neo4j.graphdb.Node;
import org.neo4j.helpers.collection.Iterators;

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

    /**
     * Retrieve a transaction by its hash.
     * Note, that there can be multiple transactions with the same hash -- in this case they can only be spent according to BIP30.
     * There have been transactions with duplicate hashes in the past, but they have not been spent, therefore
     * it is currently possible to use this method during import in order to look up the outputs of previous transactions.
     * Note, that this method will fail when it tries to read one of those transactions with duplicate hashes.
     */
    public BCTransaction getTransactionByHash(String transactionHash) {
        Node result = Iterators.single(
                graphDatabaseService.findNodes(LabelType.Transaction, NodeProperty.TRANSACTION_HASH, transactionHash)
        );
        return new BCTransaction(result);
    }

    /**
     * Allows to identify a transaction based on its hash and the block's hash in which it was included in the blockchain.
     * This can be necessary because transaction IDs are not unique (see BIP30).
     */
    public BCTransaction getTransactionByHashAndBlockHash(String transactionHash, String blockHash) {
        List<Node> nodes = Iterators.asList(graphDatabaseService.findNodes(LabelType.Transaction, NodeProperty.TRANSACTION_HASH, transactionHash));
        if(nodes.size() == 0) { // we did not find any node with this transaction hash
            throw new NoSuchElementException("No transaction found with hash " + transactionHash);
        } else if(nodes.size() == 1) { // there is exactly one with this transaction hash
            return new BCTransaction(nodes.get(0));
        } else { // there is more than one node with this transaction hash
            for(Node n : nodes) {
                BCTransaction transaction = new BCTransaction(n);
                // the transaction can be uniquely identified by comparing the hash of the block it is in
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
