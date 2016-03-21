package de.maltemoeser.bcgraph.testing;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.Transaction;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class TestUtilsTest extends BitcoinTest {

    /**
     * Ensure that we correctly load the test block.
     */
    @Test
    public void testGetTestBlock() {
        Block block = TestUtils.getTestBlock();

        assert (block != null);

        assertThat(block, instanceOf(Block.class));

        assertEquals(block.getHashAsString(), TestUtils.TEST_BLOCK_HASH);
        assertEquals(block.getTransactions().size(), TestUtils.TEST_BLOCK_NO_TX);

        Transaction coinbase = block.getTransactions().get(0);
        assertTrue(coinbase.isCoinBase());
        assertEquals(coinbase.getHashAsString(), TestUtils.COINBASE_HASH);

        Transaction secondTransaction = block.getTransactions().get(1);
        assertFalse(secondTransaction.isCoinBase());
        assertEquals(secondTransaction.getHashAsString(), TestUtils.SECOND_TRANSACTION_HASH);
    }

    @Test
    public void testGetStealthTransaction() {
        Transaction transaction = TestUtils.getStealthTransaction();
        assertThat(transaction, instanceOf(Transaction.class));
        assertEquals(TestUtils.STEALTH_TRANSACTION, transaction.getHashAsString());
    }

    @Test
    public void testGetPaymentCodeTransaction() {
        Transaction transaction = TestUtils.getPaymentCodeTransaction();
        assertThat(transaction, instanceOf(Transaction.class));
        assertEquals(TestUtils.PAYMENT_CODE_TRANSACTION, transaction.getHashAsString());
    }
}
