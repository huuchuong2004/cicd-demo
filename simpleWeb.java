import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class simpleWeb {

    public static void main(String[] args) throws Exception {
        int port = 8080;

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

                server.createContext("/", exchange -> {
                        String html = """
                                        <!doctype html>
                                        <html>
                                            <head>
                                                <meta charset=\"utf-8\">
                                                <title>SimpleWeb CI/CD Test</title>
                                                <style>
                                                    body{font-family:Arial,Helvetica,sans-serif;margin:40px}
                                                    button{padding:8px 12px;margin:6px}
                                                    #out{white-space:pre-wrap;background:#f6f6f6;padding:12px;border:1px solid #ddd}
                                                </style>
                                            </head>
                                            <body>
                                                <h1>SimpleWeb — CI/CD Test UI</h1>
                                                <p>Use the buttons below to call the server endpoints.</p>
                                                <div>
                                                    <button onclick=\"callHealth()\">Check /health</button>
                                                    <button onclick=\"callMessage()\">Get /api/message</button>
                                                </div>
                                                <h3>Output</h3>
                                                <div id=\"out\">(no output yet)</div>

                                                <script>
                                                async function callHealth(){
                                                    const res = await fetch('/health');
                                                    const txt = await res.text();
                                                    document.getElementById('out').textContent = '/health → ' + txt;
                                                }
                                                async function callMessage(){
                                                    const res = await fetch('/api/message');
                                                    const json = await res.json();
                                                    document.getElementById('out').textContent = JSON.stringify(json, null, 2);
                                                }
                                                </script>
                                            </body>
                                        </html>
                                        """;
                        respondWithType(exchange, 200, html, "text/html; charset=utf-8");
                });

                server.createContext("/health", exchange ->
                                respond(exchange, 200, "OK\n"));

                server.createContext("/api/message", exchange -> {
                        String json = "{\"message\": \"Hello from CI/CD Pipeline (JSON)\"}";
                        respondWithType(exchange, 200, json, "application/json; charset=utf-8");
                });

        server.start();
        System.out.println("Server running on http://localhost:" + port);
    }

    private static void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void respondWithType(HttpExchange exchange, int status, String body, String contentType) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
