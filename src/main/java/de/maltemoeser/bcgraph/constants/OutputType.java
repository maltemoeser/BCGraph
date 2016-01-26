package de.maltemoeser.bcgraph.constants;

/**
 * Output types characterize a ScriptPubKey.
 */
public enum OutputType {
    ADDRESS,    // Pay to PubKey Hash
    PUBKEY,     // Pay to PubKey
    P2SH,       // Pay to Script Hash
    OP_RETURN,  // Data / Unspendable
    MULTISIG,   // m-of-n MultiSig
    UNKNOWN     // Non-standard transaction
}
