package ch.bitagent.bitcoin.lib.mining;

import ch.bitagent.bitcoin.lib.ecc.Hex;
import ch.bitagent.bitcoin.lib.ecc.Int;
import ch.bitagent.bitcoin.lib.helper.Bytes;
import ch.bitagent.bitcoin.lib.helper.Hash;
import ch.bitagent.bitcoin.lib.helper.Properties;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.logging.Logger;

/**
 * https://en.bitcoin.it/wiki/Stratum_mining_protocol
 * https://bitcointalk.org/index.php?topic=557866
 */
public class Stratum {

    private static final Logger log = Logger.getLogger(Stratum.class.getSimpleName());

    public static final Hex DIFFICULTY_1 = Hex.parse("00000000ffff0000000000000000000000000000000000000000000000000000");

    public static Socket socket() {
        var socket0 = Properties.getStratumRpcSockets().get(0).split(":");
        return Stratum.newSocket(socket0[0], Integer.parseInt(socket0[1]));
    }

    private static Socket newSocket(String host, int port) {
        try {
            InetAddress address = InetAddress.getByName(host);
            return new Socket(address, port);
        } catch (IOException e) {
            log.severe(e.getMessage());
            return null;
        }
    }

    public static PrintWriter socketWriter(Socket socket) {
        try {
            return new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            log.severe(e.getMessage());
            return null;
        }
    }

    public static BufferedReader socketReader(Socket socket) {
        try {
            return new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            log.severe(e.getMessage());
            return null;
        }
    }

    public static void socketClose(Socket socket, PrintWriter writer, BufferedReader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            log.severe(e.getMessage());
        }
        if (writer != null) {
            writer.close();
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            log.severe(e.getMessage());
        }
    }

    public static Int blockTarget(String nbits) {
        var trailingZeros = Integer.parseInt(nbits.substring(0, 2), 16);
        var blockTarget = Hex.parse(nbits.substring(2) + "00".repeat(trailingZeros));
        log.info(Bytes.byteArrayToHexString(blockTarget.toBytes(32)));
        return blockTarget;
    }

    public static Int difficultyTarget(int difficulty) {
        var difficultyTarget = DIFFICULTY_1.div(Int.parse(difficulty));
        log.info(String.format("%s/%s", difficulty, Bytes.byteArrayToHexString(difficultyTarget.toBytes(32))));
        return difficultyTarget;
    }

    public static Int difficultyTargetTest(int testDifficulty) {
        var testDifficultyTarget = DIFFICULTY_1.mul(Int.parse(testDifficulty));
        log.info(String.format("*%s/%s", testDifficulty, Bytes.byteArrayToHexString(testDifficultyTarget.toBytes(32))));
        return testDifficultyTarget;
    }

    public static byte[][] merkleBranchByteArray(JSONArray merkleBranch) {
        var merkleBranchByteArray = new byte[merkleBranch.length()][32];
        for (int i = 0; i < merkleBranchByteArray.length; i++) {
            merkleBranchByteArray[i] = Bytes.hexStringToByteArray((String) merkleBranch.get(i));
        }
        return merkleBranchByteArray;
    }

    public static String merkleRoot(MessageDigest digest, String coinb1, String extranonce1, String extranonce2, String coinb2, byte[][] merkleBranchByteArray) {
        var coinbase = coinb1 + extranonce1 + extranonce2 + coinb2;
        var merkleRootBytes = Hash.hash256(digest, Bytes.hexStringToByteArray(coinbase));
        for (byte[] branch : merkleBranchByteArray) {
            merkleRootBytes = Hash.hash256(digest, Bytes.add(merkleRootBytes, branch));
        }
        return Bytes.byteArrayToHexString(Bytes.changeOrder(merkleRootBytes));
    }

    public static Hex hash(String version, String prevhash, String merkleRoot, String nbits, String ntime, String nonce) {
        var blockheader = version + prevhash + merkleRoot + nbits + ntime + nonce + "000000800000000000000000000000000000000000000000000000000000000000000000000000000000000080020000";
        return Hex.parse(Hash.hash256(Bytes.hexStringToByteArray(blockheader)));
    }

    public static JSONArray miningSubscribe(PrintWriter writer, BufferedReader reader) {
        try {
            int id = 1;
            String miningSubscribe = String.format("{\"id\":%s,\"method\":\"mining.subscribe\",\"params\":[\"bitcoinjavalib\"]}", id);
            writer.println((miningSubscribe));
            var json = new JSONObject(reader.readLine());
            log.fine(json.toString(2));
            if (id == json.getInt("id") && json.isNull("error")) {
                return json.getJSONArray("result");
            } else {
                log.severe(String.format("mining subscribe with id %s and error %s!", id, json.getString("error")));
            }
        } catch (IOException e) {
            log.severe(e.getMessage());
        }
        return null;
    }

    public static boolean miningAuthorize(PrintWriter writer, BufferedReader reader, String username, String password) {
        if (username == null || password == null) {
            log.severe(String.format("invalid username %s password %s!", username, password));
            return false;
        }
        try {
            int id = 2;
            String miningSubscribe = String.format("{\"id\":%s,\"method\":\"mining.authorize\",\"params\":[\"%s\", \"%s\"]}", id, username, password);
            writer.println((miningSubscribe));
            var json = new JSONObject(reader.readLine());
            log.info(json.toString(2));
            if (id == json.getInt("id") && json.isNull("error")) {
                return json.getBoolean("result");
            } else {
                log.severe(String.format("mining authorize with id %s and error %s!", id, json.getString("error")));
            }
        } catch (IOException e) {
            log.severe(e.getMessage());
        }
        return false;
    }
}
