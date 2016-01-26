package de.maltemoeser.bcgraph.entities;

import de.maltemoeser.bcgraph.constants.LabelType;
import de.maltemoeser.bcgraph.constants.NodeProperty;
import org.bitcoinj.core.TransactionOutPoint;
import org.neo4j.graphdb.Node;
import org.neo4j.helpers.collection.IteratorUtil;

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

    public BCTransaction getTransactionByTransactionOutPoint(TransactionOutPoint outPoint) {
        return getTransactionByHash(outPoint.getHash().toString());
    }
}
