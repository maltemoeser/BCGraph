package de.maltemoeser.bcgraph.database;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.maltemoeser.bcgraph.config.ApplicationConfig;
import de.maltemoeser.bcgraph.config.ProductionConfig;
import org.junit.After;
import org.junit.Before;

import static org.junit.Assert.assertNull;

public class ProductionDatabaseTest {

    Injector injector;
    private Database database;

    @Before
    public void setUp() {
        injector = Guice.createInjector(new AbstractModule() {

            @Override
            protected void configure() {
                bind(ApplicationConfig.class).to(ProductionConfig.class);
                bind(Database.class).to(ProductionDatabase.class);
            }
        });

        database = injector.getInstance(Database.class);
    }

    //@Test
    public void testLastInsertedBlockExists() {
        Integer lastInsertedBlockHeight = database.getLastInsertedBlockHeight();
        assertNull(lastInsertedBlockHeight);
    }

    @After
    public void destroyDatabase() {
        database.shutdown();
    }

}
