package de.maltemoeser.bcgraph.entities;

import de.maltemoeser.bcgraph.constants.LabelType;
import de.maltemoeser.bcgraph.constants.NodeProperty;
import org.neo4j.graphdb.Node;

public class BCAddressService extends BCEntityService {

    public BCAddress getOrCreateAddress(String addressHash) {

        BCAddress myAddress;
        Node node = graphDatabaseService.findNode(LabelType.Address, NodeProperty.ADDRESS_HASH, addressHash);

        if (node == null) {
            myAddress = getNewAddress();
            myAddress.setHash(addressHash);
        } else {
            myAddress = new BCAddress(node);
        }

        return myAddress;
    }

}
