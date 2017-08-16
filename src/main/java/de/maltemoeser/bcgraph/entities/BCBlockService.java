package de.maltemoeser.bcgraph.entities;

import de.maltemoeser.bcgraph.constants.LabelType;
import de.maltemoeser.bcgraph.constants.NodeProperty;
import org.neo4j.graphdb.Node;
import org.neo4j.helpers.collection.Iterators;

public class BCBlockService extends BCEntityService {

    public BCBlock createEmptyBlock() {
        return getNewBlock();
    }

    public BCBlock createBlock(String blockHash) {
        BCBlock bcBlock = getNewBlock();
        bcBlock.setHash(blockHash);
        return bcBlock;
    }

    public BCBlock createBlock(String blockHash, int height) {
        BCBlock block = createBlock(blockHash);
        block.setHeight(height);
        return block;
    }

    public BCBlock getBlockByHash(String blockHash) {
        Node node = graphDatabaseService.findNode(LabelType.Block, NodeProperty.BLOCK_HASH, blockHash);
        return new BCBlock(node);
    }

    public BCBlock getBlockByHeight(int height) {
        Node node = graphDatabaseService.findNode(LabelType.Block, NodeProperty.BLOCK_HEIGHT, height);
        return new BCBlock(node);
    }

    public BCBlock getLastInsertedBlock() {
        Node lastInsertedBlock = Iterators.single(graphDatabaseService.findNodes(LabelType.LatestBlock));
        return new BCBlock(lastInsertedBlock);
    }
}
