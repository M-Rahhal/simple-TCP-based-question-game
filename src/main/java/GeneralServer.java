import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SignatureException;

public class GeneralServer{

    private final static int PORT_NUMBER = 3002;
    private ServerSocket serverSocket;

    public GeneralServer() throws IOException {
        serverSocket = new ServerSocket(PORT_NUMBER);
    }

    public void startServer() throws IOException, SignatureException {
        while (true) {
            System.out.println("waiting for clients...");
            Socket socket = serverSocket.accept();
            System.out.println("client connected !");


            GeneralServerRequest request = new Gson().fromJson(new DataInputStream(socket.getInputStream()).readUTF(), GeneralServerRequest.class);
            // code to handle the requests for each type
            GeneralServerHandler handler = new GeneralServerHandler(socket, request);
            handler.start();
        }
    }

    public static void main(String[] args) throws IOException, SignatureException {
        GeneralServer server = new GeneralServer();
        server.startServer();
    }


}
