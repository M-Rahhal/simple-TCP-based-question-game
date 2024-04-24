import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class AuthServer{
    private final static int PORT_NUMBER = 3000;
    private ServerSocket serverSocket;

    public AuthServer() throws IOException {
        serverSocket = new ServerSocket(PORT_NUMBER);
    }

    public void startServer() throws IOException, InterruptedException {
        while (true){
            System.out.println("Server Started...");
            System.out.println("Waiting for clients...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected!");
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());


            AuthRequest request= new Gson().fromJson(in.readUTF(), AuthRequest.class);

            switch (request.type()){
                case "LOGIN":
                    new LoginHandler(request , clientSocket).start();
                    break;
                case "SIGNUP":
                    new SignupHandler(request , clientSocket).start();
                    break;
                default:
                    out.writeUTF(new Gson().toJson(new AuthResponse("400" , "Something went wrong!" , null)));
                    out.flush();
                    break;
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        AuthServer server = new AuthServer();
        server.startServer();
    }
}
