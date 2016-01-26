package de.maltemoeser.bcgraph.config;

import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public abstract class ApplicationConfig {

    private final Properties properties;
    private final Context context;

    public ApplicationConfig() {
        properties = new Properties();
        loadPropertiesFromFile();
        context = new Context(getNetworkParametersFromProperties());
    }

    abstract String getPropertyFileName();

    public abstract List<File> getBlockFiles();

    public abstract List<File> getBlockFilesWithOffset(int offset);

    private void loadPropertiesFromFile() {
        try {
            FileInputStream f = new FileInputStream("./" + getPropertyFileName());
            properties.load(f);
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDatabaseDirectory() {
        String dbd = properties.getProperty("DATABASE_DIRECTORY");
        return appendSlashToString(dbd);
    }

    public String getBlockFileDirectory() {
        String bfd = properties.getProperty("BLOCK_FILE_DIRECTORY");
        return appendSlashToString(bfd);
    }

    public String getBlockStoreDirectory() {
        return properties.getProperty("BLOCK_STORE_DIRECTORY");
    }

    protected static String appendSlashToString(String str) {
        if (!(str.endsWith("/"))) {
            return str + "/";
        }
        return str;
    }

    private NetworkParameters getNetworkParametersFromProperties() {
        String networkParam = properties.getProperty("NETWORK");
        switch (networkParam) {
            case "MAIN":
                return MainNetParams.get();
            case "TEST":
                return TestNet3Params.get();
            case "REG":
                return RegTestParams.get();
            default:
                return MainNetParams.get();
        }
    }

    public Context getContext() {
        return context;
    }

    public NetworkParameters getNetworkParameters() {
        return context.getParams();
    }
}
