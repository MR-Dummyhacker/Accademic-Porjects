package Chatserver;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private String userId;
    private String userPort;
    private String userIpAddress;

    public Client(Socket socket, String username, String userID, String userPort, String userIpAddress) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
            this.userId = userID;
            this.userPort = userPort;
            this.userIpAddress = userIpAddress;
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }



    public void sendMessage() {
        try {
            setBufferedWriter(username);
            setBufferedWriter(userId);
            setBufferedWriter(userPort);
            setBufferedWriter(userIpAddress);

            Scanner scanner = new Scanner(System.in);

            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();

                if (messageToSend.startsWith("@")) {
                    setBufferedWriter(messageToSend + " <From :" + username);
                }

                else if (messageToSend.startsWith("+")) {
                    setBufferedWriter(messageToSend);
                }

                else if (messageToSend.startsWith("#")) {
                    setBufferedWriter(messageToSend);
                }

                else {
                    setBufferedWriter(username + ": " + messageToSend);
                }
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage() {
        new Thread(() -> {
            String msgFormGroupChat;

            while (socket.isConnected()) {
                try {
                    msgFormGroupChat = bufferedReader.readLine();
                    System.out.println(msgFormGroupChat);
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }).start();
    }

    public void setBufferedWriter(String contents) throws IOException {
        bufferedWriter.write(contents);
        bufferedWriter.newLine();
        bufferedWriter.flush();

    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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


    private static class User {
        private String id;
        private String name;

        public User(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public static boolean isValidIPv4Address(String ipAddress) {
        String[] octets = ipAddress.split("\\.");

        if (octets.length != 4) {
            return false;
        }

        for (String octet : octets) {
            try {
                int value = Integer.parseInt(octet);
                if (value < 0 || value > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) throws IOException {


        // These are the list of Clients user ID and their names.
        List<User> userList = new ArrayList<>();
        userList.add(new User("001137814", "Nur"));
        userList.add(new User("123456789", "Hadee"));
        userList.add(new User("987654321", "Fardin"));
        userList.add(new User("951753852","Rahman"));
        userList.add(new User("147258369", "Tonny"));

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your userId for the group chat: ");

        // This block of code checks if the user ID exist in the list.
        String userID;
        User selectedUserId = null;
        while (selectedUserId == null) {
            userID = scanner.nextLine();

            for (User user : userList) {
                if (user.getId().equals(userID)) {
                    selectedUserId = user;
                    break;
                }
            }

            if (selectedUserId == null) {
                System.err.println("Invalid user ID. Please try again.");
            }
        }

        System.out.println("Enter Ip address:");
        String ipAddress = scanner.nextLine();
        while (!isValidIPv4Address(ipAddress)) {
            System.err.println("Invalid IPv4 address. Try again");
            ipAddress = scanner.nextLine();
        }



        System.out.println("Enter port number");
        String port = scanner.next();
        while (!port.equals("1234")) {
            System.err.println("port not found. Please enter again");
            port = scanner.next();
        }

        Socket socket = new Socket("0.0.0.0",Integer.parseInt(port));
        Client client = new Client(socket, selectedUserId.getName(), selectedUserId.getId(), port, ipAddress);
        client.listenForMessage();
        client.sendMessage();
    }
}