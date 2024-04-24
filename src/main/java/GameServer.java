import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SignatureException;

public class GameServer {
    private final static int PORT_NUMBER = 3001;
    private ServerSocket serverSocket;

    public GameServer() throws IOException {
        serverSocket = new ServerSocket(PORT_NUMBER);
    }

    public void startServer() throws IOException, InterruptedException, SignatureException, ClassNotFoundException {
        while (true){
            System.out.println("Server Started...");
            System.out.println("Waiting for clients...");
            Socket clientSocket1 = serverSocket.accept();
            System.out.println("Client connected!");
            String firstToken = new DataInputStream(clientSocket1.getInputStream()).readUTF();
            Socket clientSocket2 = serverSocket.accept();
            System.out.println("Client connected!");
            String secondToken = new DataInputStream(clientSocket2.getInputStream()).readUTF();


            DataOutputStream outputStream = new DataOutputStream(clientSocket1.getOutputStream());
            outputStream.writeUTF("Starting the Game...");
            outputStream.flush();

            outputStream = new DataOutputStream(clientSocket2.getOutputStream());
            outputStream.writeUTF("Starting the Game...");
            outputStream.flush();

            Game game = new Game(clientSocket1 ,clientSocket2 ,
                    new Gson().fromJson((String)JWTManager.decodeJWT(firstToken).get("User") , User.class) ,
                    new Gson().fromJson((String)JWTManager.decodeJWT(secondToken).get("User") , User.class));
            game.start();
        }
    }

    public static void main(String[] args) throws IOException, SignatureException, InterruptedException, ClassNotFoundException {
        GameServer gameServer = new GameServer();
        gameServer.startServer();

    }
}
