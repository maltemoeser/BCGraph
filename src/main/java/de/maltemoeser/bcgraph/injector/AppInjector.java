package de.maltemoeser.bcgraph.injector;

import com.google.inject.AbstractModule;
import de.maltemoeser.bcgraph.blockchain.BlockChainBuilder;
import de.maltemoeser.bcgraph.blockchain.BlockLoader;
import de.maltemoeser.bcgraph.blockchain.CoreBlockLoader;
import de.maltemoeser.bcgraph.blockchain.DiskBlockChainBuilder;
import de.maltemoeser.bcgraph.config.ApplicationConfig;
import de.maltemoeser.bcgraph.config.ProductionConfig;
import de.maltemoeser.bcgraph.database.Database;
import de.maltemoeser.bcgraph.database.ProductionDatabase;

public class AppInjector extends AbstractModule {

    @Override
    protected void configure() {
        bind(Database.class).to(ProductionDatabase.class);
        bind(ApplicationConfig.class).to(ProductionConfig.class);
        bind(BlockLoader.class).to(CoreBlockLoader.class);
        bind(BlockChainBuilder.class).to(DiskBlockChainBuilder.class);
    }
}
