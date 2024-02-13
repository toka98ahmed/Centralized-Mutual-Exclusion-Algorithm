package Coordinator;

import java.io.*;
import java.net.*;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Random;

public class CoordinatorMain implements Runnable {

    Socket cSocket;
    static Queue<Thread> clientQ = new LinkedList<>();   //dh ele hn7ot feh l clients ele 3yza el resource
    static boolean resourceAvailable = true;

    static CoordinatorGUI coordinatorGUI;

    public CoordinatorMain(Socket cSocket) {
        this.cSocket = cSocket;
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        ServerSocket server = new ServerSocket(2000);
        coordinatorGUI = new CoordinatorGUI();
        coordinatorGUI.setVisible(true);
        System.out.println("Coordinator is now running...");
        coordinatorGUI.checkBox(resourceAvailable);
        Socket clientObj;
        while ((clientObj = server.accept()) != null) {
            coordinatorGUI.queueText(clientQ.toString());   //display names instead of socket
            Thread clientThread = new Thread(new CoordinatorMain(clientObj));
            clientQ.add(clientThread);
            coordinatorGUI.queueText(clientQ.size() + " client(s) waiting in queue.");
            if (resourceAvailable) {

                Thread currentThread = clientQ.remove();
                //Thread.sleep(3000);
               // coordinatorGUI.queueText(clientQ.size() + " client(s) waiting in queue.");
                currentThread.start();
            }
        }

    }

    public void initiateThread() {
        if (!clientQ.isEmpty()) {
            Thread currentThread = clientQ.remove();
            coordinatorGUI.queueText(clientQ.size() + " client(s) waiting in queue.");
            currentThread.start();
        }
    }

    @Override
    public void run() {
        try {

            resourceAvailable = false;
            coordinatorGUI.checkBox(resourceAvailable);
            DataInputStream input = new DataInputStream(cSocket.getInputStream());
            DataOutputStream output = new DataOutputStream(cSocket.getOutputStream());
            output.writeUTF("Request in progress...");
            Thread.sleep(3000);
            String name = input.readUTF();
            coordinatorGUI.current(name + " has access to resource");
            coordinatorGUI.queueText(clientQ.size() + " client(s) waiting in queue.");
            //act as client to resource server
            Socket coordinator_to_Resource = new Socket("localhost", 3000);
            DataOutputStream out = new DataOutputStream(coordinator_to_Resource.getOutputStream());
            DataInputStream in = new DataInputStream(coordinator_to_Resource.getInputStream());
            Random rand = new Random();
            int token = rand.nextInt();
            out.writeUTF("Coordinator");
            Thread.sleep(3000);
            out.writeInt(token);
            output.writeInt(token);
            //  Thread.sleep(5000);

            resourceAvailable = input.readBoolean();
            coordinatorGUI.checkBox(resourceAvailable);

            coordinatorGUI.current("Resource not in use");

            input.close();
            output.close();
            out.close();
            in.close();
            coordinator_to_Resource.close();
            cSocket.close();
            initiateThread();

        } catch (Exception e) {
        }
    }

}
