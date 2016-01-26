package de.maltemoeser.bcgraph.constants;

import org.neo4j.graphdb.Label;

/**
 * Labels used in the property graph model.
 */
public enum LabelType implements Label {
    Block,
    Transaction,
    Address,
    Coinbase,       // -> Transaction
    LatestBlock,    // -> Block
    OP_Return,      // -> Output
    MultiSig,       // -> Output
    P2SH            // -> Output
}
