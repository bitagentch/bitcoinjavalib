package ch.bitagent.bitcoin.lib.mining;

import ch.bitagent.bitcoin.lib.ecc.Hex;
import ch.bitagent.bitcoin.lib.helper.Bytes;
import ch.bitagent.bitcoin.lib.helper.Hash;
import ch.bitagent.bitcoin.lib.helper.Helper;
import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StratumTest {

    private static final Logger log = Logger.getLogger(StratumTest.class.getSimpleName());

    @Test
    void miningSubscribe() {
        var socket = Stratum.socket();
        var writer = Stratum.socketWriter(socket);
        var reader = Stratum.socketReader(socket);
        var result = Stratum.miningSubscribe(writer, reader);
        Stratum.socketClose(socket, writer, reader);

        log.fine(result.toString(2));
        var difficulty = result.getJSONArray(0).getJSONArray(0);
        assertEquals("mining.set_difficulty", difficulty.getString(0));
        assertEquals(1, difficulty.getInt(1));
        var notify = result.getJSONArray(0).getJSONArray(1);
        assertEquals("mining.notify", notify.getString(0));
        assertEquals(1, notify.getInt(1));
        var extranonce1 = result.getString(1);
        assertEquals("", extranonce1);
        var extranonce2Size = result.getInt(2);
        assertEquals(6, extranonce2Size);
    }

    @Disabled("manual")
    @Test
    void miningAuthorize() {
        var socket = Stratum.socket();
        var writer = Stratum.socketWriter(socket);
        var reader = Stratum.socketReader(socket);
        var result = Stratum.miningAuthorize(writer, reader, null, null);
        Stratum.socketClose(socket, writer, reader);
        assertTrue(result);
    }

    @Test
    void testDifficultyTarget() throws IOException {
        var socket = Stratum.socket();
        var writer = Stratum.socketWriter(socket);
        var reader = Stratum.socketReader(socket);

        var subscribe = Stratum.miningSubscribe(writer, reader);
        var difficulty = subscribe.getJSONArray(0).getJSONArray(0).getInt(1);
        var target = Stratum.difficultyTarget(difficulty);
        var extranonce1 = subscribe.getString(1);
        var extranonce2Size = subscribe.getInt(2);

        while (true) {
            var serverMessage = new JSONObject(reader.readLine());
            log.info(serverMessage.toString());
            if ("mining.set_difficulty".equals(serverMessage.getString("method"))) {
                difficulty = serverMessage.getJSONArray("params").getInt(0);
                target = Stratum.difficultyTargetTest(difficulty);
            } else if ("mining.notify".equals(serverMessage.getString("method"))) {
                JSONObject job = serverMessage;
                var jobParams = job.getJSONArray("params");
                var jobId = Hex.parse(jobParams.getString(0));
                var prevhash = jobParams.getString(1);
                var coinb1 = jobParams.getString(2);
                var coinb2 = jobParams.getString(3);
                var merkleBranch = jobParams.getJSONArray(4);
                var merkleBranchByteArray = Stratum.merkleBranchByteArray(merkleBranch);
                var version = jobParams.getString(5);
                var nbits = jobParams.getString(6);
                Stratum.blockTarget(nbits);
                var ntime = jobParams.getString(7);
                var cleanJobs = jobParams.getBoolean(8);
                log.info(String.format("cleanJobs %s", cleanJobs));

                var nonce = Helper.zfill(8, jobId.toHex().toString());
                log.info(String.format("nonce %s", nonce));

                long start = System.currentTimeMillis();
                var digest = Hash.getDigestSha256();
                var extranonce2 = Bytes.byteArrayToHexString(Bytes.randomBytes(extranonce2Size));
                var merkleRoot = Stratum.merkleRoot(digest, coinb1, extranonce1, extranonce2, coinb2, merkleBranchByteArray);
                var hash = Stratum.hash(version, prevhash, merkleRoot, nbits, ntime, nonce);
                int hashes = 1;
                while (hash.ge(target)) {
                    extranonce2 = Bytes.byteArrayToHexString(Bytes.randomBytes(extranonce2Size));
                    merkleRoot = Stratum.merkleRoot(digest, coinb1, extranonce1, extranonce2, coinb2, merkleBranchByteArray);
                    hash = Stratum.hash(version, prevhash, merkleRoot, nbits, ntime, nonce);
                    hashes++;
                }
                long delta = System.currentTimeMillis() - start;

                log.info(String.format("SUCCESS - extranonce2 %s", extranonce2));
                log.info(String.format("SUCCESS - %s hashes / %s ms", hashes, delta));
                log.info(String.format("SUCCESS - %s hashes per second", hashes / (delta / 1000 + 1)));
                break;
            } else {
                throw new IllegalStateException();
            }
        }

        Stratum.socketClose(socket, writer, reader);
    }
}