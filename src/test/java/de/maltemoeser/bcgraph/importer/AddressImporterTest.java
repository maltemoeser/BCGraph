package de.maltemoeser.bcgraph.importer;

import de.maltemoeser.bcgraph.entities.BCAddress;
import de.maltemoeser.bcgraph.testing.Neo4jTest;
import de.maltemoeser.bcgraph.testing.TestUtils;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

import static org.junit.Assert.assertEquals;

//TODO: add some unparsable scripts and see what they return
public class AddressImporterTest extends Neo4jTest {

    AddressImporter addressImporter;

    @Before
    public void createObjects() {
        addressImporter = injector.getInstance(AddressImporter.class);
    }

    @Test
    public void singleAddressStringFromP2PKHScript() {
        Transaction tx = TestUtils.getP2PKHTransaction();
        Script script = tx.getOutput(0).getScriptPubKey();
        testSingleAddress(script, "1Q5Ae5FXzTjAthWKutqP3bQYYthEFv9Xmt");
    }

    @Test
    public void singleAddressStringFromCompressedP2PKScript() {
        ECKey key = ECKey.fromPrivate(BigInteger.valueOf(42));
        Script script = ScriptBuilder.createOutputScript(key);
        testSingleAddress(script, "1EMxdcJsfN5jwtZRVRvztDns1LgquGUTwi");
    }

    @Test
    public void singleAddressStringFromUncompressedP2PKScript() {
        ECKey key = ECKey.fromPrivate(BigInteger.valueOf(42), false);
        Script script = ScriptBuilder.createOutputScript(key);
        testSingleAddress(script, "1HGn3jxoSh8twi4mR3iaNmZr6pbHgjFJEg");
    }

    @Test
    public void multipleAddressStringsFromScript() {
        Transaction tx = TestUtils.getMultiSigOutputTransaction();
        Script script = tx.getOutput(0).getScriptPubKey();

        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            addressImporter.parseAddress(script);
            List<String> addresses = addressImporter.getAddresses();

            assertEquals(3, addresses.size());

            assertEquals("1PCu5JyieveBFZpE9FT6VWGaZPTfEsDzWe", addresses.get(0));
            assertEquals("1Lc5yK4PUrRcT26xVJ3PJDuhjCcq7zrNSK", addresses.get(1));
            assertEquals("13v86MqrxZ3LgwPN1yE9fxF2cj2pbvK2NA", addresses.get(2));
        }
    }

    @Test
    public void multipleBCAddressesFromScript() {
        Transaction tx = TestUtils.getMultiSigOutputTransaction();
        Script script = tx.getOutput(0).getScriptPubKey();

        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {

            List<BCAddress> addresses = addressImporter.parseAddress(script);

            assertEquals(3, addresses.size());

            assertEquals("1PCu5JyieveBFZpE9FT6VWGaZPTfEsDzWe", addresses.get(0).getHash());
            assertEquals("1Lc5yK4PUrRcT26xVJ3PJDuhjCcq7zrNSK", addresses.get(1).getHash());
            assertEquals("13v86MqrxZ3LgwPN1yE9fxF2cj2pbvK2NA", addresses.get(2).getHash());
        }
    }

    @Test
    public void P2SHfromScript() {
        Transaction tx = TestUtils.getP2SHOutputTransaction();
        Script script = tx.getOutput(1).getScriptPubKey();
        testSingleAddress(script, "39NYcE1djHQv9KW9qqf4VFH2MsEQRdQADB");
    }

    @Test
    public void OPReturnOutputFromScript() {
        Transaction tx = TestUtils.getOpReturnTransaction();
        Script script = tx.getOutput(0).getScriptPubKey();
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            addressImporter.parseAddress(script);
            List<String> addresses = addressImporter.getAddresses();
            assertEquals(0, addresses.size());
        }
    }

    public void testSingleAddress(Script script, String addressHash) {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            addressImporter.parseAddress(script);
            List<String> addresses = addressImporter.getAddresses();
            assertEquals(addressHash, addresses.get(0));
            assertEquals(1, addresses.size());
        }
    }
}
