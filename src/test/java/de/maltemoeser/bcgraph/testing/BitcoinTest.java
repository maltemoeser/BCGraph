package de.maltemoeser.bcgraph.testing;

import org.bitcoinj.core.Context;
import org.bitcoinj.params.MainNetParams;

public class BitcoinTest {
    public Context context = new Context(MainNetParams.get());
}
