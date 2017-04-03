import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirtyWebServer {

    private static final int PORT = 11111;

    private static final Pattern PATTERN = Pattern.compile("GET /([^?]*).* HTTP/1.1");
    private static final String KILL_COMMAND = "kill";
    private static final String RESTART_COMMAND = "restart";

    public static void main(String[] args) throws IOException, AWTException {
        if (args.length > 0 && Arrays.asList(KILL_COMMAND, RESTART_COMMAND).contains(args[0].toLowerCase())) {
            sendCommandToLocalhost(KILL_COMMAND); //in both cases the command sent to localhost is "kill"
            if (KILL_COMMAND.equalsIgnoreCase(args[0])) {
                System.exit(0);
                return;
            }
        }
        new DirtyWebServer().run();
    }

    private void run() throws IOException {
        checkIfRunning();
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Listening on port " + server.getLocalPort());
            do {
                final Socket socket = server.accept();
                Executors.newFixedThreadPool(8).execute(() -> {
                    try (Socket s = socket; Scanner scanner = new Scanner(s.getInputStream()).useDelimiter("\\r\\n")) {
                        try {
                            Matcher matcher = PATTERN.matcher(scanner.next());
                            if (matcher.find()) {
                                String received = matcher.group(1);
                                String protocolString = String.format("%.20s", received).toLowerCase();
                                switch (protocolString) {
                                    case "hello":
                                        System.out.println("hello received from: " + socket.getRemoteSocketAddress());
                                        s.getOutputStream().write("HTTP/1.1 200 OK\r\n".getBytes());
                                        return;
                                    case KILL_COMMAND:
                                        System.exit(0);
                                        break;
                                    case "favicon.ico":
                                        return;
                                }
                                System.out.println("Received: " + received);
                                s.getOutputStream().write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
                                s.getOutputStream().write(received.getBytes());
                            } else {
                                throw new ParseException("command not formed correctly", 0);
                            }
                        } catch (Exception e) {
                            s.getOutputStream().write("HTTP/1.1 400 Bad Request\r\n".getBytes());
                        }
                    } catch (IOException ignored) {
                    }
                });
            } while (!Thread.interrupted());
        }
    }

    private void checkIfRunning() {
        int responseCode = DirtyWebServer.sendCommandToLocalhost("hello");
        if (responseCode == 200) {
            JOptionPane.showMessageDialog(null, "An instance is already running.");
            System.exit(-1);
        }
    }

    private static int sendCommandToLocalhost(String command) {
        int responseCode = 0;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("http", Inet4Address.getLocalHost().getHostAddress(), PORT, "/" + command).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(500);
            responseCode = connection.getResponseCode();
            connection.disconnect();
        } catch (IOException ignored) {
        }
        return responseCode;
    }

}