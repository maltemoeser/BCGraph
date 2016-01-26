package de.maltemoeser.bcgraph.importer;

import com.google.inject.Inject;
import de.maltemoeser.bcgraph.database.Database;
import de.maltemoeser.bcgraph.entities.BCBlock;
import de.maltemoeser.bcgraph.entities.BCBlockService;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.Sha256Hash;
import org.neo4j.graphdb.GraphDatabaseService;

public class BlockImporter {

    private BCBlockService blockService;
    private BCBlock bcBlock;
    GraphDatabaseService graphDatabaseService;

    @Inject
    public BlockImporter(Database database) {
        this.graphDatabaseService = database.getGraphDatabaseService();
    }

    @Inject
    public void setBlockService(BCBlockService blockService) {
        this.blockService = blockService;
    }

    /**
     * Parses a bitcoinj Block object and stores it in the database.
     */
    public BCBlock importBlock(Block _block) {
        bcBlock = createNodeFromBlock(_block);
        if(_block.getPrevBlockHash().equals(Sha256Hash.ZERO_HASH)) { // first block ever created
            bcBlock.setHeight(0);
        } else {
            BCBlock previousBlock = blockService.getBlockByHash(_block.getPrevBlockHash().toString());
            bcBlock.setHeight(previousBlock.getHeight() + 1);
            bcBlock.connectToPreviousBlock(previousBlock);
            previousBlock.setLatestBlock(false);
        }
        bcBlock.setLatestBlock(true);
        return bcBlock;
    }

    protected BCBlock createNodeFromBlock(Block block) {
        BCBlock bcBlock = blockService.createEmptyBlock();
        bcBlock.setHash(block.getHashAsString());
        bcBlock.setUnixTime(block.getTime().getTime() / 1000);
        return bcBlock;
    }

    public BCBlock getBCBlock() {
        return bcBlock;
    }
}
