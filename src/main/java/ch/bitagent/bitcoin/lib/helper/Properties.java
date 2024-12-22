package ch.bitagent.bitcoin.lib.helper;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>Properties class.</p>
 */
public class Properties {

    private Properties() {
    }

    private static final Logger log = Logger.getLogger(Properties.class.getSimpleName());

    private static final String FILENAME = "bitcoinjavalib.properties";
    public static final String WALLET_FILENAME = "wallet.properties";

    private static final java.util.Properties bitcoinjavalibProperties = new java.util.Properties();
    private static final java.util.Properties walletProperties = new java.util.Properties();

    private static String getPath(String filename) {
        return Thread.currentThread().getContextClassLoader().getResource("").getPath() + filename;
    }

    private static String getProperty(java.util.Properties properties, String filename, String property) {
        var path = getPath(filename);
        try (var fileInputStream = new FileInputStream(path)) {
            if (properties.isEmpty()) {
                properties.load(fileInputStream);
                for (Object keyObject : properties.keySet().stream().sorted().toArray()) {
                    String key = (String) keyObject;
                    log.fine(String.format("%s=%s", key, properties.getProperty(key)));
                }
            }
            return properties.getProperty(property);
        } catch (Exception e) {
            log.severe(e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }
    }

    private static List<String> getPropertyList(java.util.Properties properties, String path, String property) {
        List<String> list = new ArrayList<>();
        int i = 0;
        var propertyI = getProperty(properties, path, property + i++);
        while (propertyI != null && !propertyI.isEmpty()) {
            list.add(propertyI);
            propertyI = getProperty(properties, path, property + i++);
        }
        return list;
    }

    /**
     * <p>getBitcoinP2pUrl.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public static String getBitcoinP2pUrl() {
        return getProperty(bitcoinjavalibProperties, FILENAME, "bitcoin.p2p.url");
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
        return Boolean.parseBoolean(getProperty(bitcoinjavalibProperties, FILENAME, "bitcoin.p2p.testnet"));
    }

    /**
     * <p>getBitcoinRpcUrl.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public static String getBitcoinRpcUrl() {
        return getProperty(bitcoinjavalibProperties, FILENAME, "bitcoin.rpc.url");
    }

    /**
     * <p>getBitcoinRpcTestnet.</p>
     *
     * @return a {@link java.lang.Boolean} object
     */
    public static Boolean getBitcoinRpcTestnet() {
        return Boolean.parseBoolean(getProperty(bitcoinjavalibProperties, FILENAME, "bitcoin.rpc.testnet"));
    }

    /**
     * <p>getBitcoinRpcAuth.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public static String getBitcoinRpcAuth() {
        String auth = getProperty(bitcoinjavalibProperties, FILENAME, "bitcoin.rpc.auth");
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
        return getProperty(bitcoinjavalibProperties, FILENAME, "blockstream.mainnet.url");
    }

    /**
     * <p>getBlockstreamTestnetUrl.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public static String getBlockstreamTestnetUrl() {
        return getProperty(bitcoinjavalibProperties, FILENAME, "blockstream.testnet.url");
    }

    /**
     * <p>getTxFresh.</p>
     *
     * @return a {@link java.lang.Boolean} object
     */
    public static Boolean getTxFresh() {
        return Boolean.parseBoolean(getProperty(bitcoinjavalibProperties, FILENAME, "tx.fresh"));
    }

    /**
     * <p>getTxCachefile.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public static String getTxCachefile() {
        return getProperty(bitcoinjavalibProperties, FILENAME, "tx.cachefile");
    }

    public static List<String> getElectrumRpcSockets() {
        return getPropertyList(bitcoinjavalibProperties, FILENAME, "electrum.rpc.socket.");
    }

    public static List<String> getWallets(String filename) {
        return getPropertyList(walletProperties, filename, "wallet.");
    }

    public static String getWalletMnemonic(String filename, int index) {
        var wallet = getWallets(filename);
        if (wallet.size() > index) {
            var walletArray = wallet.get(index).split(":");
            return walletArray[0];
        }
        return null;
    }

    public static String getWalletPassphrase(String filename, int index) {
        var wallet = getWallets(filename);
        if (wallet.size() > index) {
            var walletArray = wallet.get(index).split(":");
            if (walletArray.length > 1) {
                return walletArray[1];
            }
        }
        return null;
    }
}
