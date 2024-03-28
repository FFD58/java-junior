package ru.fafurin.hw.lesson5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {

    private final List<ConnectionHandler> clients = new ArrayList<>();
    private ServerSocket serverSocket;
    public final static int PORT = 4444;
    private boolean done = false;
    private ExecutorService pool;

    @Override
    public void run() {
        System.out.println("Server started on port " + PORT);
        try {
            serverSocket = new ServerSocket(PORT);
            pool = Executors.newCachedThreadPool();
            while (!done) {
                Socket client = serverSocket.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                clients.add(handler);
                pool.execute(handler);
            }

        } catch (IOException e) {
            shutdown();
        }

    }

    public void broadcast(String message) {
        for (ConnectionHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public String getAllClientsNames() {
        StringBuilder names = new StringBuilder();
        for (ConnectionHandler client : clients) {
            names.append(client.nickname).append("\n");
        }
        return names.toString();
    }

    public void shutdown() {
        done = true;
        try {
            pool.shutdown();
            if (!serverSocket.isClosed()) {
                serverSocket.close();
                for (ConnectionHandler client : clients) {
                    client.shutdown();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    class ConnectionHandler implements Runnable {

        private final Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String nickname;

        public ConnectionHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.println("Please, enter a name: ");
                nickname = in.readLine();
                broadcast(nickname + " connected!");
                printInfo();
                System.out.println(nickname + " connected!");
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("/all")) {
                        broadcast("Connected clients: \n" + getAllClientsNames());
                    } else if (message.startsWith("/nickname ")) {
                        changeNickname(message);
                    } else if (message.startsWith("/exit")) {
                        exit();
                    } else if (message.startsWith("/to ")) {
                        sendToNicknameMessage(message);
                    } else {
                        broadcast(nickname + "(to all): " + message);
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }

        private void exit() {
            broadcast(nickname + " left the chat!");
            System.out.println(nickname + " left the chat!");
            shutdown();
        }

        private void changeNickname(String message) {
            String[] messageSplit = message.split(" ", 2);
            if (messageSplit.length == 2) {
                broadcast(nickname + " renamed themselves to " + messageSplit[1]);
                System.out.println(nickname + " renamed themselves to " + messageSplit[1]);
                nickname = messageSplit[1];
                out.println("Successfully changed name to " + nickname);
            } else {
                out.println("No nickname provided!");
            }
        }

        private void printInfo() {
            broadcast("""
                    Chat commands:\s
                    /all - see list all connected clients\s
                    /nickname - change the nickname
                    /exit - left the chat.""");
        }

        private void sendToNicknameMessage(String message) {
            String[] messageSplit = message.split(" ", 3);
            if (messageSplit.length == 3) {
                sendMessage(messageSplit[1], nickname + "(only for you): " + messageSplit[2]);
            } else {
                out.println("No nickname provided!");
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        public void sendMessage(String to, String message) {
            Optional<ConnectionHandler> client = getClientByName(to);
            client.ifPresent(connectionHandler -> connectionHandler.out.println(message));
        }

        public Optional<ConnectionHandler> getClientByName(String name) {
            ConnectionHandler result = null;
            for (ConnectionHandler client : clients) {
                if (client.nickname.equals(name)) result = client;
            }
            return Optional.ofNullable(result);
        }

        public void shutdown() {
            try {
                in.close();
                out.close();
                if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}

