package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * A minimal HTTP proxy running on virtual threads that sends everything to the mock server, whatever host
 * the client asked for, so nothing ever reaches the real internet. HTTPS arrives as a CONNECT request and is
 * tunneled to the mock's HTTPS side; plain HTTP arrives as a request for a full URL and is relayed, as is,
 * to the mock's plain-HTTP side.
 * <p>
 * The threading is kept deliberately simple: open connections are not tracked, and stopping the proxy leaves
 * their threads to finish on their own. A production-grade proxy would own its threads, for example through
 * an executor, and stop them all in {@link #stop()}.
 */
final class HttpProxy {

    private final ServerSocket serverSocket;
    private final InetSocketAddress httpsTarget;
    private final InetSocketAddress httpTarget;
    private final SystemPropertyOverrides jvmProxySettings;

    HttpProxy(InetSocketAddress httpsTarget, InetSocketAddress httpTarget) throws IOException {
        this.httpsTarget = httpsTarget;
        this.httpTarget = httpTarget;
        this.serverSocket = new ServerSocket(0, 50, InetAddress.getLoopbackAddress());
        String host = serverSocket.getInetAddress().getHostAddress();
        String port = String.valueOf(serverSocket.getLocalPort());
        this.jvmProxySettings = new SystemPropertyOverrides()
                .add("http.proxyHost", host)
                .add("http.proxyPort", port)
                .add("https.proxyHost", host)
                .add("https.proxyPort", port)
                .add("http.nonProxyHosts", "localhost|127.*|[::1]");
        Thread.ofVirtual().name("http-proxy-acceptor").start(this::acceptLoop);
    }

    void installAsJvmProxy() {
        jvmProxySettings.override();
    }

    void uninstallAsJvmProxy() {
        jvmProxySettings.rollback();
    }

    private void acceptLoop() {
        while (!serverSocket.isClosed()) {
            try {
                Socket client = serverSocket.accept();
                Thread.ofVirtual().name("http-proxy-connection").start(() -> handle(client));
            } catch (IOException e) {
                // The server socket was closed
            }
        }
    }

    private void handle(Socket client) {
        try (client) {
            InputStream clientIn = client.getInputStream();
            OutputStream clientOut = client.getOutputStream();

            // Read the request line and headers only; anything after them (a body, TLS) is relayed untouched
            String requestLine = readLine(clientIn);
            StringBuilder head = new StringBuilder(requestLine).append("\r\n");
            String header;
            while (!(header = readLine(clientIn)).isEmpty()) {
                head.append(header).append("\r\n");
            }
            head.append("\r\n");

            if (requestLine.startsWith("CONNECT ")) {
                try (Socket upstream = new Socket(httpsTarget.getAddress(), httpsTarget.getPort())) {
                    clientOut.write("HTTP/1.1 200 Connection Established\r\n\r\n".getBytes(StandardCharsets.US_ASCII));
                    clientOut.flush();
                    relay(client, upstream);
                }
            } else {
                // e.g. "POST http://example.com/service HTTP/1.1": the mock's HttpServer understands it as is
                try (Socket upstream = new Socket(httpTarget.getAddress(), httpTarget.getPort())) {
                    upstream.getOutputStream().write(head.toString().getBytes(StandardCharsets.ISO_8859_1));
                    relay(client, upstream);
                }
            }
        } catch (IOException | InterruptedException e) {
            // The connection was dropped; nothing to clean up beyond closing the sockets
        }
    }

    private static void relay(Socket client, Socket upstream) throws IOException, InterruptedException {
        InputStream upstreamIn = upstream.getInputStream();
        OutputStream clientOut = client.getOutputStream();
        Thread upstreamToClient = Thread.ofVirtual().start(() -> {
            pipe(upstreamIn, clientOut);
            shutdownOutput(client);
        });
        pipe(client.getInputStream(), upstream.getOutputStream());
        shutdownOutput(upstream);
        upstreamToClient.join();
    }

    private static String readLine(InputStream in) throws IOException {
        ByteArrayOutputStream line = new ByteArrayOutputStream();
        int b;
        while ((b = in.read()) != -1 && b != '\n') {
            if (b != '\r') {
                line.write(b);
            }
        }
        return line.toString(StandardCharsets.ISO_8859_1);
    }

    private static void pipe(InputStream in, OutputStream out) {
        try {
            in.transferTo(out);
            out.flush();
        } catch (IOException e) {
            // One side closed the connection
        }
    }

    private static void shutdownOutput(Socket socket) {
        try {
            socket.shutdownOutput();
        } catch (IOException e) {
            // Already closed
        }
    }

    void stop() throws IOException {
        serverSocket.close();
    }

}
