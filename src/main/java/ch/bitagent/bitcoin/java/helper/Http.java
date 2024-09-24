package ch.bitagent.bitcoin.java.helper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.logging.Logger;

public class Http {

    private static final Logger log = Logger.getLogger(Http.class.getSimpleName());

    private Http() {}

    public static String get(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (200 == response.statusCode()) {
                log.fine(String.valueOf(response.statusCode()));
                return response.body();
            } else {
                log.severe(String.valueOf(response.statusCode()));
                log.severe(url);
                log.severe(response.body());
                throw new IllegalStateException(String.format("Http Status %s", response.statusCode()));
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static String postGetRawTransaction(String txId64) {
        try {
            String url = Properties.getBitcoinRpcUrl();
            String json = String.format("{\"jsonrpc\": \"1.0\", \"id\": \"bitcoinjavalib\", \"method\": \"getrawtransaction\", \"params\": [\"%s\"]}", txId64);
            String auth = Base64.getEncoder().encodeToString((Properties.getBitcoinRpcAuth()).getBytes());
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("content-type", "text/plain;")
                    .header("Authorization", "Basic " + auth)
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (200 == response.statusCode()) {
                log.fine(String.valueOf(response.statusCode()));
                var txArray = response.body().split("\"");
                return txArray[3];
            } else {
                log.severe(String.valueOf(response.statusCode()));
                log.severe(url);
                log.severe(json);
                log.severe(auth);
                log.severe(response.body());
                throw new IllegalStateException(String.format("Http Status %s", response.statusCode()));
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
