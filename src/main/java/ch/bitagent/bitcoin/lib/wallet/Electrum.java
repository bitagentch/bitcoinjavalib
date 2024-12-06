package ch.bitagent.bitcoin.lib.wallet;

import ch.bitagent.bitcoin.lib.helper.Properties;
import ch.bitagent.bitcoin.lib.helper.Tcp;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * https://electrumx-spesmilo.readthedocs.io/en/stable/protocol-methods.html
 */
public class Electrum {

    private static final Logger log = Logger.getLogger(Electrum.class.getSimpleName());

    private final List<String> sockets = new ArrayList<>();

    public List<String> getSockets() {
        return sockets;
    }

    public String defaultSocket() {
        var electrumRpcSockets = Properties.getElectrumRpcSockets();
        if (electrumRpcSockets.isEmpty()) {
            var error = "Please configure a default electrum rpc socket in bitcoinjavalib.properties";
            log.severe(error);
            throw new IllegalStateException(error);
        }
        String electrumRpcSocket = electrumRpcSockets.get(0);
        log.fine(electrumRpcSocket);
        sockets.clear();
        sockets.add(electrumRpcSocket);
        return electrumRpcSocket;
    }

    public void allSockets() {
        sockets.clear();
        for (String electrumRpcSocket : Properties.getElectrumRpcSockets()) {
            log.fine(electrumRpcSocket);
            sockets.add(electrumRpcSocket);
        }
    }

    public String callSocket(String socket, String jsonRequest) {
        var start = System.currentTimeMillis();
        try {
            if (socket == null) {
                socket = defaultSocket();
            }
            log.fine(String.format(">> %s", jsonRequest));
            var socketData = socket.split(":");
            if (socketData.length != 3) {
                throw new IllegalArgumentException("Invalid socket");
            }
            var host = socketData[0];
            var port = Integer.parseInt(socketData[1]);
            var protocol = socketData[2].toLowerCase();

            String jsonResponse;
            if ("tls".equals(protocol)) {
                jsonResponse = Tcp.tlsSocket(host, port, jsonRequest);
            } else if ("ssl".equals(protocol)) {
                jsonResponse = Tcp.sslSocket(host, port, jsonRequest);
            } else {
                jsonResponse = Tcp.socket(host, port, jsonRequest);
            }
            if (jsonResponse == null) {
                log.severe(String.format("jsonResponse == null\n%s %sms", socket, System.currentTimeMillis() - start));
                return null;
            }
            var responseLog = jsonResponse.toString();
            if (responseLog.length() > 80) {
                responseLog = responseLog.substring(0, 80) + " ...";
            }
            log.fine(String.format("<< %s", responseLog));
            log.info(String.format("%s %sms", socket, System.currentTimeMillis() - start));
            return jsonResponse;
        } catch (Exception e) {
            log.severe(String.format("%s\n%s %sms", e, socket, System.currentTimeMillis() - start));
            e.printStackTrace();
            return null;
        }
    }

    public List<JSONObject> ping() {
        List<JSONObject> pongs = new ArrayList<>();
        var jsonRequest = "{\"jsonrpc\": \"2.0\", \"method\": \"server.ping\", \"params\": [], \"id\": \"bitcoinjavalib\"}";
        for (String socket : sockets) {
            var jsonResponse = callSocket(socket, jsonRequest);
            if (jsonResponse != null && !jsonResponse.isEmpty()) {
                pongs.add(new JSONObject(jsonResponse));
            }
        }
        return pongs;
    }

    public List<JSONObject> features() {
        List<JSONObject> features = new ArrayList<>();
        var jsonRequest = "{\"jsonrpc\": \"2.0\", \"method\": \"server.features\", \"params\": [], \"id\": \"bitcoinjavalib\"}";
        for (String socket : sockets) {
            var jsonResponse = callSocket(socket, jsonRequest);
            var json = new JSONObject(jsonResponse);
            features.add(json.getJSONObject("result"));
        }
        return features;
    }

    public List<JSONArray> versions() {
        List<JSONArray> versions = new ArrayList<>();
        var jsonRequest = "{\"jsonrpc\": \"2.0\", \"method\": \"server.version\", \"params\": [\"\", \"1.4\"], \"id\": \"bitcoinjavalib\"}";
        for (String socket : sockets) {
            var jsonResponse = callSocket(socket, jsonRequest);
            var json = new JSONObject(jsonResponse);
            versions.add(json.getJSONArray("result"));
        }
        return versions;
    }

    public JSONArray peers() {
        var jsonRequest = "{\"jsonrpc\": \"2.0\", \"method\": \"server.peers.subscribe\", \"params\": [], \"id\": \"bitcoinjavalib\"}";
        var jsonResponse = callSocket(null, jsonRequest);
        var json = new JSONObject(jsonResponse);
        return json.getJSONArray("result");
    }

    public JSONArray getHistory(String scripthash) {
        var jsonRequest = String.format("{\"jsonrpc\": \"2.0\", \"method\": \"blockchain.scripthash.get_history\", \"params\": [\"%s\"], \"id\": \"bitcoinjavalib\"}", scripthash);
        var jsonResponse = callSocket(null, jsonRequest);
        if (jsonResponse == null) {
            return null;
        }
        var json = new JSONObject(jsonResponse);
        return json.getJSONArray("result");
    }

    public JSONObject getBalance(String scripthash) {
        var jsonRequest = String.format("{\"jsonrpc\": \"2.0\", \"method\": \"blockchain.scripthash.get_balance\", \"params\": [\"%s\"], \"id\": \"bitcoinjavalib\"}", scripthash);
        var jsonResponse = callSocket(null, jsonRequest);
        if (jsonResponse == null) {
            return null;
        }
        var json = new JSONObject(jsonResponse);
        return json.getJSONObject("result");
    }

    public Long getBalanceTotal(String scripthash) {
        var balance = this.getBalance(scripthash);
        if (balance == null) {
            return null;
        }
        var unconfirmed = balance.getLong("unconfirmed");
        var confirmed = balance.getLong("confirmed");
        return unconfirmed + confirmed;
    }

    public JSONArray getMempool(String scripthash) {
        var jsonRequest = String.format("{\"jsonrpc\": \"2.0\", \"method\": \"blockchain.scripthash.get_mempool\", \"params\": [\"%s\"], \"id\": \"bitcoinjavalib\"}", scripthash);
        var jsonResponse = callSocket(null, jsonRequest);
        if (jsonResponse == null) {
            return null;
        }
        var json = new JSONObject(jsonResponse);
        return json.getJSONArray("result");
    }

    public JSONArray listUnspent(String scripthash) {
        var jsonRequest = String.format("{\"jsonrpc\": \"2.0\", \"method\": \"blockchain.scripthash.listunspent\", \"params\": [\"%s\"], \"id\": \"bitcoinjavalib\"}", scripthash);
        var jsonResponse = callSocket(null, jsonRequest);
        if (jsonResponse == null) {
            return null;
        }
        var json = new JSONObject(jsonResponse);
        return json.getJSONArray("result");
    }
}
