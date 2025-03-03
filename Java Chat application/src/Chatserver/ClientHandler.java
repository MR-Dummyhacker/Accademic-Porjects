package Chatserver;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class ClientHandler implements Runnable{

    public  static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private String clientUserId;
    private String ClientUserPort;
    private String ClientIpAddress;
    private String coordinator;
    Date date = new Date();

    public ClientHandler(Socket socket) {
        try {
            this.socket =socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            this.clientUserId = bufferedReader.readLine();
            this.ClientUserPort = bufferedReader.readLine();
            this.ClientIpAddress = bufferedReader.readLine();
            clientHandlers.add(this);
            this.coordinator = clientHandlers.get(0).clientUsername;


            int listSize = clientHandlers.size();
            if (listSize == 1) {
                bufferedWriter.write("""
                        Welcome to the group. You are the first member in the server
                        You became the group Coordinator
                        1. To send group message, just type your message and enter
                        2. To send private message type '@receivers name' your message enter
                        3. To see the list of members and their details enter '+'
                        4. To see the options again enter '#'""");
            } else {
                broadcastMessage("SERVER:  " + clientUsername + " has entered the chat");
                bufferedWriter.write("Welcome to the group"+"\n"+coordinator + " is the group coordinator"+"\n"+
                                        "1. To send group message, just type your message and enter"+"\n" +
                                        "2. To send private message type '@receivers name' your message enter"+"\n" +
                                        "3. To see the list of members and their details enter '+'"+"\n" +
                                        "4. To see the options again enter '#'");
            }
            bufferedWriter.newLine();
            bufferedWriter.flush();

        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        String messageFromClient;

       // long lastActiveTime = System.currentTimeMillis();

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();

                if (messageFromClient.startsWith("@")) {
                    int spaceIndex = messageFromClient.indexOf(' ');
                    if (spaceIndex != -1) {
                        String recipient = messageFromClient.substring(1, spaceIndex);
                        String message = (messageFromClient.substring(spaceIndex + 1));
                        for (ClientHandler clientHandler : clientHandlers) {
                            if (clientHandler.clientUsername.equals(recipient) & !clientHandler.clientUsername.equals(clientUsername)) {
                                clientBufferedwriter(clientHandler, message + " <" + date + ">");
                            }
                        }
                    }
                }

                else if (messageFromClient.startsWith("+")) {

                        for (ClientHandler clientHandler : clientHandlers) {

                            if (clientHandler.clientUsername.equals(clientUsername)) {
                                clientBufferedwriter(clientHandler, "Member list");

                                for (ClientHandler clientHandler1 : clientHandlers) {
                                    if (!clientHandler1.clientUsername.equals(clientUsername) & clientHandler1.equals(clientHandlers.get(0))) {
                                        clientBufferedwriter(clientHandler, clientHandler1.clientUsername + "(Coordinator)" + ", Id: " + clientHandler1.clientUserId +
                                                ", Port: " + clientHandler1.ClientUserPort + "\n" + "Ip address: " + clientHandler1.ClientIpAddress);
                                    }

                                    if (!clientHandler1.clientUsername.equals(clientUsername) & !clientHandler1.equals(clientHandlers.get(0))) {
                                        clientBufferedwriter(clientHandler, clientHandler1.clientUsername +"(member)" + ", Id: " + clientHandler1.clientUserId +
                                                                ", Port: " + clientHandler1.ClientUserPort + "\n" + "Ip address: " + clientHandler1.ClientIpAddress);
                                    }
                                }
                                clientBufferedwriter(clientHandler,"End of list");

                            }
                        }
                    }

                else if (messageFromClient.startsWith("#")) {

                    for (ClientHandler clientHandler : clientHandlers) {

                        if (clientHandler.clientUsername.equals(clientUsername)) {
                            clientBufferedwriter(clientHandler,
                                    """
                                            1. To send group message, just type your message and enter
                                            2. To send private message type '@receivers name' your message enter
                                            3. To see the list of members and their details enter '+'
                                            4. To see the options again enter '#'""");
                        }
                    }
                }

                else {
                    broadcastMessage(messageFromClient);
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void clientBufferedwriter(ClientHandler clientHandler, String contents) throws IOException{
        clientHandler.bufferedWriter.write(contents);
        clientHandler.bufferedWriter.newLine();
        clientHandler.bufferedWriter.flush();
    }

    public void broadcastMessage(String messageToSend) {
        try {

        for (ClientHandler clientHandler : clientHandlers) {

                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bufferedWriter.write(messageToSend +" <"+ date+">");
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            }
            } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }


    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER:  " + clientUsername + " has left the chat!" );

        String newCoordinator = clientHandlers.get(0).clientUsername;
        try {
                    for (ClientHandler clientHandler : clientHandlers) {
                        if (clientHandler.equals(clientHandlers.get(0)) & !clientHandlers.get(0).clientUsername.equals(coordinator)) {
                            clientBufferedwriter(clientHandler, "You are the new group coordinator");
                        } else {

                            if (!clientHandler.clientUsername.equals(clientHandlers.get(0).clientUsername) & !coordinator.equals(newCoordinator)) {
                                clientBufferedwriter(clientHandler,clientHandlers.get(0).clientUsername + " is the new group coordinator");
                            } break;
                        }
                    }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }

    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
