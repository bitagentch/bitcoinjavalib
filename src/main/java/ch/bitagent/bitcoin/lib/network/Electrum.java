package ch.bitagent.bitcoin.lib.network;

import ch.bitagent.bitcoin.lib.helper.Helper;
import ch.bitagent.bitcoin.lib.helper.Properties;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * https://electrum-protocol.readthedocs.io/en/latest/
 */
public class Electrum {

    private static final Logger log = Logger.getLogger(Electrum.class.getSimpleName());

    private final List<String> sockets = new ArrayList<>();
    private String defaultSocket;

    public Electrum() {
        this.initSockets(false);
    }

    public Electrum(boolean testnet) {
        this.initSockets(testnet);
    }

    private void initSockets(boolean testnet) {
        sockets.clear();
        if (testnet) {
            for (String electrumRpcSocket : Properties.getElectrumTestnetRpcSockets()) {
                log.fine(electrumRpcSocket);
                sockets.add(electrumRpcSocket);
            }
        } else {
            for (String electrumRpcSocket : Properties.getElectrumMainnetRpcSockets()) {
                log.fine(electrumRpcSocket);
                sockets.add(electrumRpcSocket);
            }
        }
        if (sockets.isEmpty()) {
            var error = "Please configure a electrum rpc socket in bitcoinjavalib.properties";
            log.severe(error);
            throw new IllegalStateException(error);
        }
        for (String socket : sockets) {
            if (ping(socket)) {
                defaultSocket = socket;
                break;
            }
        }
        if (defaultSocket == null) {
            var error = "Please configure a working electrum rpc socket in bitcoinjavalib.properties";
            log.severe(error);
            throw new IllegalStateException(error);
        }
    }

    public List<String> getSockets() {
        return sockets;
    }

    public String callSocket(String socket, String jsonRequest) {
        var start = System.currentTimeMillis();
        try {
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
                log.severe(String.format("jsonResponse == null - %s %sms", socket, System.currentTimeMillis() - start));
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
            log.severe(String.format("%s - %s %sms", e, socket, System.currentTimeMillis() - start));
            e.printStackTrace();
            return null;
        }
    }

    public boolean ping(String socket) {
        var jsonResponse = callSocket(socket, getJsonRequest("server.ping", null));
        if (jsonResponse != null && !jsonResponse.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public List<JSONObject> features() {
        List<JSONObject> features = new ArrayList<>();
        for (String socket : sockets) {
            var jsonResponse = callSocket(socket, getJsonRequest("server.features", null));
            var json = new JSONObject(jsonResponse);
            features.add(json.getJSONObject("result"));
        }
        return features;
    }

    public List<JSONArray> versions() {
        List<JSONArray> versions = new ArrayList<>();
        for (String socket : sockets) {
            var jsonResponse = callSocket(socket, getJsonRequest("server.version", List.of("", "1.4")));
            var json = new JSONObject(jsonResponse);
            versions.add(json.getJSONArray("result"));
        }
        return versions;
    }

    public JSONArray peers() {
        var jsonResponse = callSocket(defaultSocket, getJsonRequest("server.peers.subscribe", null));
        var json = new JSONObject(jsonResponse);
        return json.getJSONArray("result");
    }

    public Integer height() {
        var jsonResponse = callSocket(defaultSocket, getJsonRequest("blockchain.headers.subscribe", null));
        var json = new JSONObject(jsonResponse);
        return json.getJSONObject("result").getInt("height");
    }

    public JSONArray getHistory(String scripthash) {
        var jsonResponse = callSocket(defaultSocket, getJsonRequest("blockchain.scripthash.get_history", List.of(scripthash)));
        if (jsonResponse == null) {
            return null;
        }
        var json = new JSONObject(jsonResponse);
        if (json.isNull("result")) {
            log.severe(json.toString());
            return null;
        }
        return json.getJSONArray("result");
    }

    public JSONObject getBalance(String scripthash) {
        var jsonResponse = callSocket(defaultSocket, getJsonRequest("blockchain.scripthash.get_balance", List.of(scripthash)));
        if (jsonResponse == null) {
            return null;
        }
        var json = new JSONObject(jsonResponse);
        if (json.isNull("result")) {
            log.severe(json.toString());
            return null;
        }
        return json.getJSONObject("result");
    }

    public JSONArray getMempool(String scripthash) {
        var jsonResponse = callSocket(defaultSocket, getJsonRequest("blockchain.scripthash.get_mempool", List.of(scripthash)));
        if (jsonResponse == null) {
            return null;
        }
        var json = new JSONObject(jsonResponse);
        if (json.isNull("result")) {
            log.severe(json.toString());
            return null;
        }
        return json.getJSONArray("result");
    }

    public JSONArray listUnspent(String scripthash) {
        var jsonResponse = callSocket(defaultSocket, getJsonRequest("blockchain.scripthash.listunspent", List.of(scripthash)));
        if (jsonResponse == null) {
            return null;
        }
        var json = new JSONObject(jsonResponse);
        if (json.isNull("result")) {
            log.severe(json.toString());
            return null;
        }
        return json.getJSONArray("result");
    }

    public Long estimateFee(int number) {
        var jsonResponse = callSocket(defaultSocket, getJsonRequest("blockchain.estimatefee", List.of(String.valueOf(number))));
        if (jsonResponse == null) {
            return null;
        }
        var json = new JSONObject(jsonResponse);
        if (json.isNull("result")) {
            log.severe(json.toString());
            return null;
        }
        var estimateFeeKB = json.getDouble("result");
        return Long.parseLong(Helper.btcToSat(estimateFeeKB / 1024)) + 1;
    }

    public String getTransaction(String txHash) {
        var jsonResponse = callSocket(defaultSocket, getJsonRequest("blockchain.transaction.get", List.of(txHash, false)));
        if (jsonResponse == null) {
            return null;
        }
        var json = new JSONObject(jsonResponse);
        if (json.isNull("result")) {
            log.severe(json.toString());
            return null;
        }
        return json.getString("result");
    }

    public JSONObject getTransactionVerbose(String txHash) {
        var jsonResponse = callSocket(defaultSocket, getJsonRequest("blockchain.transaction.get", List.of(txHash, true)));
        if (jsonResponse == null) {
            return null;
        }
        var json = new JSONObject(jsonResponse);
        if (json.isNull("result")) {
            log.severe(json.toString());
            return null;
        }
        return json.getJSONObject("result");
    }

    public String broadcastTransaction(String rawTx) {
        if (rawTx == null || rawTx.isEmpty()) {
            return null;
        }
        var jsonResponse = callSocket(defaultSocket, getJsonRequest("blockchain.transaction.broadcast", List.of(rawTx)));
        if (jsonResponse == null) {
            return null;
        }
        var json = new JSONObject(jsonResponse);
        if (json.isNull("result")) {
            log.severe(json.toString());
            return null;
        }
        return json.getString("result");
    }

    private String getJsonRequest(String method, List<Object> params) {
        var jsonRequest = new JSONObject();
        jsonRequest.put("jsonrpc", "2.0");
        jsonRequest.put("method", method);
        var paramsJson = new JSONArray();
        if (params != null) {
            for (Object param : params) {
                paramsJson.put(param);
            }
        }
        jsonRequest.put("params", paramsJson);
        jsonRequest.put("id", "bitcoinjavalib");
        return jsonRequest.toString();
    }
}
