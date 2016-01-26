package de.maltemoeser.bcgraph.testing;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.maltemoeser.bcgraph.blockchain.BlockChainBuilder;
import de.maltemoeser.bcgraph.blockchain.BlockLoader;
import de.maltemoeser.bcgraph.blockchain.CoreBlockLoader;
import de.maltemoeser.bcgraph.blockchain.MemoryBlockChainBuilder;
import de.maltemoeser.bcgraph.config.ApplicationConfig;
import de.maltemoeser.bcgraph.config.TestConfig;
import de.maltemoeser.bcgraph.database.Database;
import de.maltemoeser.bcgraph.database.TestDatabase;
import org.junit.After;
import org.junit.Before;

public class TestInjector extends BitcoinTest {

    protected Injector injector;

    @Before
    public void setUpTestInjector() {
        injector = Guice.createInjector(new AbstractModule() {

            @Override
            protected void configure() {
                bind(ApplicationConfig.class).to(TestConfig.class);
                bind(Database.class).to(TestDatabase.class);
                bind(BlockLoader.class).to(CoreBlockLoader.class);
                bind(BlockChainBuilder.class).to(MemoryBlockChainBuilder.class);
            }
        });
    }

    @After
    public void tearDownTestInjector() {
        injector = null;
    }
}
