package ResourceServer;

import java.net.*;
import java.io.*;

public class ResourceServer implements Runnable {

    Socket cSocket;
    static int token;

    static ResourceServerGUI resourceServerGUI;

    public ResourceServer(Socket cSocket) {
        this.cSocket = cSocket;
    }

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(3000);
        resourceServerGUI = new ResourceServerGUI();
        resourceServerGUI.setVisible(true);
        while (true) {
            Socket clientSocket = server.accept();
            Thread clientThread = new Thread(new ResourceServer(clientSocket));
            clientThread.start();
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("Client is connected");
            DataInputStream input = new DataInputStream(cSocket.getInputStream());
            DataOutputStream output = new DataOutputStream(cSocket.getOutputStream());

            String messageSource = input.readUTF();

            if ("Coordinator".equals(messageSource)) {
                token = input.readInt();
                System.out.println(token);
            } else {
                int tokenClient = input.readInt();
                if (tokenClient == token) {
                    resourceServerGUI.setTextArea(input.readUTF());
                    Thread.sleep(3000);
                    System.out.println("Client connected successfully");
                    output.writeUTF("Update received successfully!");
                }
            }
            output.close();
            input.close();
            cSocket.close();
        } catch (Exception e) {
        }
    }

}
