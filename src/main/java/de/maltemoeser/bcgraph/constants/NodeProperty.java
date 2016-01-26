package de.maltemoeser.bcgraph.constants;

/**
 * Node property keys.
 */
public final class NodeProperty {

    // Blocks
    public static final String BLOCK_HASH = "hash";
    public static final String BLOCK_HEIGHT = "height";
    public static final String BLOCK_UNIXTIME = "unixtime";

    // Transactions
    public static final String TRANSACTION_HASH = "hash";
    public static final String TRANSACTION_VALUE = "value";
    public static final String TRANSACTION_FEE = "fee";
    public static final String TRANSACTION_LOCKTIME = "locktime";

    // Outputs
    public static final String OUTPUT_INDEX = "index";
    public static final String OUTPUT_VALUE = "value";
    public static final String OUTPUT_MULTISIG_REQUIRED = "m";
    public static final String OUTPUT_MULTISIG_TOTAL = "n";

    // Addresses
    public static final String ADDRESS_HASH = "hash";
}
