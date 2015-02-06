package firsthttpserver;

import com.sun.net.httpserver.Headers;
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
import java.util.List;
import java.util.Map.Entry;

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
//        server.createContext("/files", new SimpleFileHandler());
        server.createContext("/headers", new HeadersHandler());
        server.createContext("/pages/", new SimpleFileHandler());
        server.setExecutor(null); // Use the default executor
        server.start();
        System.out.println("Server started, listening on port: " + port);
    }

    static class RequestHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException {
            String response = "";
            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html>\n");
            sb.append("<html>\n");
            sb.append("<head>\n");
            sb.append("<title>My fancy Web Site</title>\n");
            sb.append("<meta charset='UTF-8'>\n");
            sb.append("</head>\n");
            sb.append("<body>\n");
            sb.append("<h2>Welcome to my very first home made Web Server :-)</h2>\n");
            sb.append("</body>\n");
            sb.append("</html>\n");
            response = sb.toString();
            Headers h = he.getResponseHeaders();
            h.add("Content-Type", "text/html");
            he.sendResponseHeaders(200, response.length());
            try (PrintWriter pw = new PrintWriter(he.getResponseBody())) {
                pw.print(response); //What happens if we use a println instead of print --> Explain
            }
        }

    }

    static class HeadersHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException {

            Headers h1 = he.getRequestHeaders();
            Headers h2 = he.getResponseHeaders();

            String response1 = "";
            StringBuilder sb2 = new StringBuilder();
            sb2.append("<!DOCTYPE html>\n");
            sb2.append("<html>\n");
            sb2.append("<head>\n");
            sb2.append("<title>Headers</title>\n");
            sb2.append("<meta charset='UTF-8'>\n");
            sb2.append("</head>\n");

            sb2.append("<body>\n");
            sb2.append("<h2>Schema</h2>\n");
            sb2.append("<table border=\"1\">");
            sb2.append("<tr>");
            sb2.append("<td>Header</td>");
            sb2.append("<td>Value</td>");
            sb2.append("</tr>");

            System.out.println("Size: " + h1.entrySet().size());

            for (Entry<String, List<String>> entry : h1.entrySet()) {
                String key = entry.getKey();
                List<String> value = entry.getValue();

                System.out.println("in looop");

                sb2.append("<tr>");
                sb2.append("<td>");
                sb2.append(key);
                sb2.append("</td>");
                sb2.append("<td>");
                sb2.append(value);
                sb2.append("</td>");
                sb2.append("</tr>");
            }

            System.out.println("After loop");

            sb2.append("</table>");
            sb2.append("</body>\n");
            sb2.append("</html>\n");
            response1 = sb2.toString();
            h2.add("Content-Type", "text/html");
            he.sendResponseHeaders(200, response1.length());
            try (PrintWriter pw = new PrintWriter(he.getResponseBody())) {
                pw.print(response1); //What happens if we use a println instead of print --> Explain
            }

        }
    }

    static class SimpleFileHandler implements HttpHandler {

        

        @Override
        public void handle(HttpExchange he) throws IOException {
            
            String contentFolder = "public/";
            File file = new File(contentFolder + "index.html");
            byte[] bytesToSend = new byte[(int) file.length()];
            try {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                bis.read(bytesToSend, 0, bytesToSend.length);
            } catch (IOException ie) {
                ie.printStackTrace();
            }
            he.sendResponseHeaders(200, bytesToSend.length);
            try (OutputStream os = he.getResponseBody()) {
                os.write(bytesToSend, 0, bytesToSend.length);
            }
        }

    }

}

/*static class SimpleFileHandler implements HttpHandler {

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
 }*/
