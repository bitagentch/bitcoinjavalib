package ch.bitagent.bitcoin.lib.tx;

import ch.bitagent.bitcoin.lib.ecc.Hex;
import ch.bitagent.bitcoin.lib.helper.Bytes;
import ch.bitagent.bitcoin.lib.helper.Hash;
import ch.bitagent.bitcoin.lib.helper.Helper;
import ch.bitagent.bitcoin.lib.helper.Properties;
import ch.bitagent.bitcoin.lib.network.Electrum;
import ch.bitagent.bitcoin.lib.network.Http;

import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;

/**
 * <p>TxFetcher class.</p>
 */
public class TxFetcher {

    private static final Logger log = Logger.getLogger(TxFetcher.class.getSimpleName());

    private TxFetcher() {
    }

    /**
     * <p>fetch a tx</p>
     *
     * @param txId    .
     * @param testnet .
     * @param cache   .
     * @return a tx
     */
    public static Tx fetch(String txId, Boolean testnet, Map<String, String> cache) {
        String txId64 = Helper.zfill(64, txId);
        String txRaw = null;
        if (cache == null || !cache.containsKey(txId64)) {
            long start = System.currentTimeMillis();
            try {
                if (Properties.getBitcoinRpcAuth() != null && Properties.getBitcoinRpcTestnet().equals(testnet)) {
                    txRaw = Http.postGetRawTransaction(txId64);
                } else if (Boolean.TRUE.equals(testnet)) {
                    txRaw = Http.get(getUrlBlockstream(testnet, txId64));
                } else {
                    txRaw = new Electrum().getTransaction(txId64);
                }
                // make sure the tx we got matches to the hash we requested
                Tx tx;
                byte[] rawBytes = Bytes.hexStringToByteArray(txRaw);
                if (rawBytes[4] == 0) {
                    // no inputs
                    rawBytes = Bytes.add(Arrays.copyOfRange(rawBytes, 0, 4), Arrays.copyOfRange(rawBytes, 6, rawBytes.length));
                    tx = Tx.parse(rawBytes, testnet);
                    var locktime = Hex.parse(Bytes.changeOrder(Arrays.copyOfRange(rawBytes, rawBytes.length - 4, rawBytes.length)));
                    tx.setLocktime(locktime);
                } else {
                    tx = Tx.parse(rawBytes, testnet);
                }
                if (!tx.id().equals(txId64)) {
                    throw new IllegalStateException(String.format("not the same id: %s vs %s", tx.id(), txId));
                }
                if (cache != null) {
                    cache.put(txId64, txRaw);
                    log.fine(String.format("tx %s to cache.", txId64));
                }
            } catch (Exception e) {
                log.severe(e.getMessage());
                throw new IllegalStateException(e.getMessage());
            }
            log.fine(String.format("time %sms", System.currentTimeMillis() - start));
        } else {
            log.fine(String.format("tx %s from cache.", txId64));
            txRaw = cache.get(txId64);
        }
        var txBytes = Bytes.hexStringToByteArray(txRaw);
        Tx tx = Tx.parse(txBytes, testnet);
        // make sure the tx we got matches to the hash we requested
        String computed;
        if (Boolean.TRUE.equals(tx.getSegwit())) {
            computed = tx.id();
        } else {
            computed = Hex.parse(Bytes.changeOrder(Hash.hash256(txBytes))).toString();
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
}
