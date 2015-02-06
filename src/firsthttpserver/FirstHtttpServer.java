package firsthttpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * @author Lars Mortensen
 */
public class FirstHtttpServer {

    static int port = 8080;
    static String ip = "127.0.0.1";
    static String contentFolder = "public/";

    public static void main(String[] args) throws Exception {
        if (args.length == 2) {
            port = Integer.parseInt(args[1]);
            ip = args[0];
        }
        HttpServer server = HttpServer.create(new InetSocketAddress(ip, port), 0);
        server.createContext("/welcome", new RequestHandler());
        server.createContext("/files", new SimpleFileHandler());
        server.setExecutor(null); // Use the default executor
        server.start();
        System.out.println("Server started, listening on port: " + port);
    }

    static class RequestHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException {
            String response = "Welcome to my very first almost home made Web Server :-)";
            response += "<br>" + "URI:" + he.getRequestURI();
            Scanner scan = new Scanner(he.getRequestBody());
            while (scan.hasNext()) {
                response += scan.nextLine();}

                he.sendResponseHeaders(200, response.length());
                try (PrintWriter pw = new PrintWriter(he.getResponseBody())) {
                    pw.print(response); //What happens if we use a println instead of print --> Explain
                }
            }
        }

        static class SimpleFileHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange he) throws IOException {
                File file = new File(contentFolder + "index.html");
                byte[] bytesToSend = new byte[(int) file.length()];
                try {
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                    bis.read(bytesToSend, 0, bytesToSend.length);
                } catch (IOException ie) {
                    System.out.println(ie);
                }
                he.sendResponseHeaders(200, bytesToSend.length);
                try (OutputStream os = he.getResponseBody()) {
                    os.write(bytesToSend, 0, bytesToSend.length);
                }
            }
        }
    
}
