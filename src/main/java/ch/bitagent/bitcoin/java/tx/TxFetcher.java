package ch.bitagent.bitcoin.java.tx;

import ch.bitagent.bitcoin.java.ecc.Hex;
import ch.bitagent.bitcoin.java.helper.Bytes;
import ch.bitagent.bitcoin.java.helper.Helper;
import ch.bitagent.bitcoin.java.helper.Http;
import ch.bitagent.bitcoin.java.helper.Properties;

import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * <p>TxFetcher class.</p>
 */
public class TxFetcher {

    private static final Logger log = Logger.getLogger(TxFetcher.class.getSimpleName());

    private static Map<String, String> cache = new ConcurrentHashMap<>();

    private static final String CACHE_FILE = Properties.getTxCachefile();

    private TxFetcher() {}

    /**
     * <p>fetch.</p>
     *
     * @param txId a {@link java.lang.String} object
     * @param testnet a {@link java.lang.Boolean} object
     * @param fresh a boolean
     * @return a {@link ch.bitagent.bitcoin.java.tx.Tx} object
     */
    public static Tx fetch(String txId, Boolean testnet, boolean fresh) {
        String txId64 = Helper.zfill64(txId);
        var doFresh = fresh || Properties.getTxFresh();
        loadCache(doFresh);
        if (doFresh || !cache.containsKey(txId64)) {
            long start = System.currentTimeMillis();
            try {
                String txRaw;
                if (Properties.getBitcoinRpcAuth() != null && Properties.getBitcoinRpcTestnet().equals(testnet)) {
                    txRaw = Http.postGetRawTransaction(txId64);
                } else {
                    txRaw = Http.get(getUrlBlockstream(testnet, txId64));
                }
                // make sure the tx we got matches to the hash we requested
                Tx tx;
                byte[] rawBytes = Bytes.hexStringToByteArray(txRaw);
                if (rawBytes[4] == 0) {
                    // no inputs
                    rawBytes = Bytes.add(Arrays.copyOfRange(rawBytes, 0, 4), Arrays.copyOfRange(rawBytes, 6, rawBytes.length));
                    tx = Tx.parse(new ByteArrayInputStream(rawBytes), testnet);
                    var locktime = Hex.parse(Bytes.changeOrder(Arrays.copyOfRange(rawBytes, rawBytes.length - 4, rawBytes.length)));
                    tx.setLocktime(locktime);
                } else {
                    tx = Tx.parse(new ByteArrayInputStream(rawBytes), testnet);
                }
                if (!tx.id().equals(txId64)) {
                    throw new IllegalStateException(String.format("not the same id: %s vs %s", tx.id(), txId));
                }
                cache.put(txId64, txRaw);
                dumpCache(doFresh);
            } catch (Exception e) {
                log.severe(e.getMessage());
                throw new IllegalStateException(e.getMessage());
            }
            log.fine(String.format("time %sms", System.currentTimeMillis() - start));
        }
        var txBytes = Bytes.hexStringToByteArray(cache.get(txId64));
        var txStream = new ByteArrayInputStream(txBytes);
        Tx tx = Tx.parse(txStream, testnet);
        // make sure the tx we got matches to the hash we requested
        String computed;
        if (Boolean.TRUE.equals(tx.getSegwit())) {
            computed = tx.id();
        } else {
            computed = Hex.parse(Bytes.changeOrder(Helper.hash256(txBytes))).toString();
        }
        if (!computed.equals(txId)) {
            throw new IllegalStateException(String.format("server lied: %s vs %s", computed, txId));
        }
        log.fine(String.format("tx %s", tx));
        return tx;
    }

    private static String getUrlBlockstream(Boolean testnet, String txId64) {
        String baseUrl;
        if (Boolean.TRUE.equals(testnet)) {
            baseUrl = Properties.getBlockstreamTestnetUrl();
        } else {
            baseUrl = Properties.getBlockstreamMainnetUrl();
        }
        return String.format("%s/tx/%s/hex", baseUrl, txId64);
    }

    private static void loadCache(Boolean fresh) {
        if (Boolean.TRUE.equals(fresh)) {
            TxFetcher.cache = new ConcurrentHashMap<>();
            return;
        }
        try {
            File fileToReadObject = new File(TxFetcher.CACHE_FILE);
            FileInputStream fileIn = new FileInputStream(fileToReadObject);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            TxFetcher.cache = (Map<String, String>) in.readObject();
            in.close();
            fileIn.close();
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
    }

    private static void dumpCache(Boolean fresh) {
        if (Boolean.TRUE.equals(fresh)) {
            return;
        }
        try {
            File fileToSaveObject = new File(TxFetcher.CACHE_FILE);
            FileOutputStream fileOut = new FileOutputStream(fileToSaveObject);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(TxFetcher.cache);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            log.severe(e.getMessage());
        }
    }
}
