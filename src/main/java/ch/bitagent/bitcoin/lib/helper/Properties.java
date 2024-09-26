package ch.bitagent.bitcoin.lib.helper;

import java.io.FileInputStream;
import java.util.logging.Logger;

/**
 * <p>Properties class.</p>
 */
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

    /**
     * <p>getBitcoinP2pUrl.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public static String getBitcoinP2pUrl() {
        return getProperty("bitcoin.p2p.url");
    }

    /**
     * <p>getBitcoinP2pHost.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public static String getBitcoinP2pHost() {
        return getBitcoinP2pUrl().split(":")[0];
    }

    /**
     * <p>getBitcoinP2pPort.</p>
     *
     * @return a {@link java.lang.Integer} object
     */
    public static Integer getBitcoinP2pPort() {
        return Integer.parseInt(getBitcoinP2pUrl().split(":")[1]);
    }

    /**
     * <p>getBitcoinP2pTestnet.</p>
     *
     * @return a {@link java.lang.Boolean} object
     */
    public static Boolean getBitcoinP2pTestnet() {
        return Boolean.parseBoolean(getProperty("bitcoin.p2p.testnet"));
    }

    /**
     * <p>getBitcoinRpcUrl.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public static String getBitcoinRpcUrl() {
        return getProperty("bitcoin.rpc.url");
    }

    /**
     * <p>getBitcoinRpcTestnet.</p>
     *
     * @return a {@link java.lang.Boolean} object
     */
    public static Boolean getBitcoinRpcTestnet() {
        return Boolean.parseBoolean(getProperty("bitcoin.rpc.testnet"));
    }

    /**
     * <p>getBitcoinRpcAuth.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public static String getBitcoinRpcAuth() {
        String auth = getProperty("bitcoin.rpc.auth");
        if (auth != null && !auth.trim().isEmpty()) {
            return auth;
        } else {
            return null;
        }
    }

    /**
     * <p>getBlockstreamMainnetUrl.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public static String getBlockstreamMainnetUrl() {
        return getProperty("blockstream.mainnet.url");
    }

    /**
     * <p>getBlockstreamTestnetUrl.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public static String getBlockstreamTestnetUrl() {
        return getProperty("blockstream.testnet.url");
    }

    /**
     * <p>getTxFresh.</p>
     *
     * @return a {@link java.lang.Boolean} object
     */
    public static Boolean getTxFresh() {
        return Boolean.parseBoolean(getProperty("tx.fresh"));
    }

    /**
     * <p>getTxCachefile.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public static String getTxCachefile() {
        return getProperty("tx.cachefile");
    }
}
