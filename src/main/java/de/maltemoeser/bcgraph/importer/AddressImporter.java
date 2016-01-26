package de.maltemoeser.bcgraph.importer;

import com.google.inject.Inject;
import de.maltemoeser.bcgraph.config.ApplicationConfig;
import de.maltemoeser.bcgraph.constants.OutputType;
import de.maltemoeser.bcgraph.entities.BCAddress;
import de.maltemoeser.bcgraph.entities.BCAddressService;
import org.bitcoinj.core.Address;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptChunk;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.bitcoinj.core.Utils.sha256hash160;

public class AddressImporter {

    ApplicationConfig applicationConfig;
    List<String> addresses = new ArrayList<>();
    BCAddressService addressService;
    OutputType outputType;
    Script script;

    public List<String> getAddresses() {
        return addresses;
    }

    public List<BCAddress> parseAddress(Script script) {
        return parseAddress(script, ScriptParser.getOutputTypeFromScript(script));
    }

    public List<BCAddress> parseAddress(Script script, OutputType outputType) {
        this.script = script;
        this.outputType = outputType;
        getAddressesFromScript();
        return addresses.stream().map(address -> addressService.getOrCreateAddress(address)).collect(Collectors.toList());
    }

    private void getAddressesFromScript() {
        getAddressesFromScript(this.script);
    }

    /**
     * Convert script into typical Bitcoin addresses
     * Is only done when script is well-formed, i.e. P2PK, P2PKH, P2SH or MULTISIG
     *
     * @param script scriptPubKey of a transaction output
     */
    protected void getAddressesFromScript(Script script) {
        switch (outputType) {
            case ADDRESS:
            case P2SH:
                addresses.add(script.getToAddress(applicationConfig.getNetworkParameters()).toString());
                break;
            case PUBKEY:
                addresses.add(script.getToAddress(applicationConfig.getNetworkParameters(), true).toString());
                break;
            case MULTISIG:
                addresses = getAddressFromMultiSigScript(script);
                break;
        }
    }

    /**
     * Returns all addresses that are referenced in a multiSig script
     */
    protected static List<String> getAddressFromMultiSigScript(Script script) {
        List<String> addresses = new LinkedList<>();
        List<ScriptChunk> chunks = script.getChunks();
        for (int i = 1; i < chunks.size() - 2; i++) {
            addresses.add(new Address(MainNetParams.get(), sha256hash160(chunks.get(i).data)).toString());
        }
        return addresses;
    }

    @Inject
    public void setApplicationConfig(ApplicationConfig config) {
        this.applicationConfig = config;
    }

    @Inject
    public void setAddressService(BCAddressService addressService) {
        this.addressService = addressService;
    }
}
