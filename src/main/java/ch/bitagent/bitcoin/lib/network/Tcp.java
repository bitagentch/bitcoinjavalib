package ch.bitagent.bitcoin.lib.network;

import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

public class Tcp {

    private static final Logger log = Logger.getLogger(Tcp.class.getSimpleName());

    private Tcp() {
    }

    public static String socket(String host, int port, String json) {
        try (Socket socket = new Socket(host, port)) {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.println(json);
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            return reader.readLine();
        } catch (Exception e) {
            log.severe(e.toString());
            return null;
        }
    }

    public static String tlsSocket(String host, int port, String json) {
        return sslSocket(host, port, json, true);
    }

    public static String sslSocket(String host, int port, String json) {
        return sslSocket(host, port, json, false);
    }

    private static String sslSocket(String host, int port, String json, boolean tls) {
        try {
            SSLContext sslContext = SSLContext.getInstance(tls ? "TLS" : "SSL");
            sslContext.init(null, trustAllCertificates, new SecureRandom());
            SSLSocketFactory factory = sslContext.getSocketFactory();
            try (SSLSocket socket = (SSLSocket) factory.createSocket(host, port)) {
                if (tls) {
                    socket.setEnabledProtocols(new String[]{"TLSv1.2", "TLSv1.3"});
                }
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                writer.println(json);
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                return reader.readLine();
            }
        } catch (Exception e) {
            log.severe(e.toString());
            return null;
        }
    }

    private static final TrustManager[] trustAllCertificates = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    // NOP
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    // NOP
                }
            }
    };
}
