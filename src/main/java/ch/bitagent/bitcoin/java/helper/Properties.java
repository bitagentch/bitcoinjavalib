package ch.bitagent.bitcoin.java.helper;

import java.io.FileInputStream;
import java.util.logging.Logger;

public class Properties {

    private Properties() {}

    private static final Logger log = Logger.getLogger(Properties.class.getSimpleName());

    private static final String PATH = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "bitcoinjavalib.properties";

    private static final java.util.Properties bitcoinjavalibProperties = new java.util.Properties();

    private static String getProperty(String property) {
        try (var fileInputStream = new FileInputStream(PATH)) {
            if (bitcoinjavalibProperties.isEmpty()) {
                bitcoinjavalibProperties.load(fileInputStream);
                for (Object keyObject : bitcoinjavalibProperties.keySet().stream().sorted().toArray()) {
                    String key = (String) keyObject;
                    log.info(String.format("%s=%s", key, bitcoinjavalibProperties.getProperty(key)));
                }
            }
            return bitcoinjavalibProperties.getProperty(property);
        } catch (Exception e){
            log.severe(e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }
    }

    public static String getBitcoinP2pUrl() {
        return getProperty("bitcoin.p2p.url");
    }

    public static String getBitcoinP2pHost() {
        return getBitcoinP2pUrl().split(":")[0];
    }

    public static Integer getBitcoinP2pPort() {
        return Integer.parseInt(getBitcoinP2pUrl().split(":")[1]);
    }

    public static Boolean getBitcoinP2pTestnet() {
        return Boolean.parseBoolean(getProperty("bitcoin.p2p.testnet"));
    }

    public static String getBitcoinRpcUrl() {
        return getProperty("bitcoin.rpc.url");
    }

    public static Boolean getBitcoinRpcTestnet() {
        return Boolean.parseBoolean(getProperty("bitcoin.rpc.testnet"));
    }

    public static String getBitcoinRpcAuth() {
        String auth = getProperty("bitcoin.rpc.auth");
        if (auth != null && !auth.trim().isEmpty()) {
            return auth;
        } else {
            return null;
        }
    }

    public static String getBlockstreamMainnetUrl() {
        return getProperty("blockstream.mainnet.url");
    }

    public static String getBlockstreamTestnetUrl() {
        return getProperty("blockstream.testnet.url");
    }

    public static Boolean getTxFresh() {
        return Boolean.parseBoolean(getProperty("tx.fresh"));
    }

    public static String getTxCachefile() {
        return getProperty("tx.cachefile");
    }
}
