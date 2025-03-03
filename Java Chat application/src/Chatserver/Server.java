package Chatserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
public class Server {
    private final ServerSocket serverSocket;
    public Server(ServerSocket serverSocket) {

        this.serverSocket = serverSocket;
    }
    public void startServer() {
        try {

            while (!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected");
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }

        } catch (IOException e) {
            closeServerSocket();
        }
    }
    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        InetAddress ipAddress = InetAddress.getByName("0.0.0.0");
        ServerSocket serverSocket = new ServerSocket(1234, 50, ipAddress);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}