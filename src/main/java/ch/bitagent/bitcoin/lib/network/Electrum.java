package ch.bitagent.bitcoin.lib.network;

import ch.bitagent.bitcoin.lib.helper.Helper;
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
        for (String socket : sockets) {
            var jsonResponse = callSocket(socket, getJsonRequest("server.ping", null));
            if (jsonResponse != null && !jsonResponse.isEmpty()) {
                pongs.add(new JSONObject(jsonResponse));
            }
        }
        return pongs;
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
        var jsonResponse = callSocket(null, getJsonRequest("server.peers.subscribe", null));
        var json = new JSONObject(jsonResponse);
        return json.getJSONArray("result");
    }

    public Integer height() {
        var jsonResponse = callSocket(null, getJsonRequest("blockchain.headers.subscribe", null));
        var json = new JSONObject(jsonResponse);
        return json.getJSONObject("result").getInt("height");
    }

    public JSONArray getHistory(String scripthash) {
        var jsonResponse = callSocket(null, getJsonRequest("blockchain.scripthash.get_history", List.of(scripthash)));
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
        var jsonResponse = callSocket(null, getJsonRequest("blockchain.scripthash.get_balance", List.of(scripthash)));
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
        var jsonResponse = callSocket(null, getJsonRequest("blockchain.scripthash.get_mempool", List.of(scripthash)));
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
        var jsonResponse = callSocket(null, getJsonRequest("blockchain.scripthash.listunspent", List.of(scripthash)));
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
        var jsonResponse = callSocket(null, getJsonRequest("blockchain.estimatefee", List.of(String.valueOf(number))));
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
        var jsonResponse = callSocket(null, getJsonRequest("blockchain.transaction.get", List.of(txHash, false)));
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
        var jsonResponse = callSocket(null, getJsonRequest("blockchain.transaction.get", List.of(txHash, true)));
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
        var jsonResponse = callSocket(null, getJsonRequest("blockchain.transaction.broadcast", List.of(rawTx)));
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
