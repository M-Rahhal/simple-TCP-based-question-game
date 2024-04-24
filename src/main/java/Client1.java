import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client1 {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please choose:\n1-login\n2-signup");
        int choice = scanner.nextInt();
        String token="";
        switch (choice){
            case 1:
                token = login();
                break;
            case 2:
                token = signup();
                break;
            default:
                System.out.println("Wrong Entry");
                System.exit(0);
        }

        //now connect to the game server with the token and display its menu

        while (true){
            System.out.println("Enter a choice:");
            System.out.println("1 --> Start a new game");
            System.out.println("2 --> show my results");
            System.out.println("3 --> show leaderboard");
            System.out.println("4 --> Sign-out");

            choice = scanner.nextInt();

            switch (choice){
                case 1:
                    playGame(token);
                    break;
                case 2:
                    System.out.println(getResult(token));
                    break;
                case 3:
                    System.out.println(getLeaderboard(token));
                    break;
                case 4:
                    System.exit(0);
                default:
                    continue;

            }



        }



    }

    public static String getLeaderboard(String token) throws IOException {
        GeneralServerRequest request =new GeneralServerRequest(token ,"LEADERBOARD");
        Socket socket = new Socket("127.0.0.1" , 3002);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream());
        out.writeUTF(new Gson().toJson(request));
        out.flush();

        //get the result back
        return in.readUTF();
    }
    public static String getResult(String token) throws IOException {
        GeneralServerRequest request =new GeneralServerRequest(token ,"RESULT");
        Socket socket = new Socket("127.0.0.1" , 3002);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream());
        out.writeUTF(new Gson().toJson(request));
        out.flush();

        //get the result back
        return in.readUTF();
    }
    public static void playGame(String token) throws IOException {
        Socket socket = new Socket("127.0.0.1",3001);
        // send token
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream());
        out.writeUTF(token);
        out.flush();
        System.out.println(in.readUTF());
        // read the questions from the server
        for (int j =0 ; j< 5 ; j++) {
            QuestionDTO dto = new Gson().fromJson(in.readUTF(), QuestionDTO.class);
            System.out.println("Question:" + dto.question());
            System.out.println("points:" + dto.points());
            System.out.println("Choices: ");
            for (int i = 0; i < dto.choices().length; i++)
                System.out.println(i + "- " + dto.choices()[i]);
            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            dto = new QuestionDTO(dto.question(), dto.choices(), dto.points(), choice);
            out.writeUTF(new Gson().toJson(dto));

            // reading the response from the user
            String message = in.readUTF();
            System.out.println(message);
        }

        //reading the final result

        System.out.println(in.readUTF());
    }
    public static String login() throws IOException {

        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Please enter username:");
            String username = scanner.nextLine();
            System.out.println("Please enter password:");
            String password = scanner.nextLine();

            Socket socket = new Socket("127.0.0.1", 3000);

            AuthRequest request = new AuthRequest(username, password, "LOGIN");
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF(new Gson().toJson(request));
            dataOutputStream.flush();

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            AuthResponse response = new Gson().fromJson(dataInputStream.readUTF(), AuthResponse.class);
            if (response.state().equals("200")) {
                System.out.println(response.message());
                return response.token();
            } else{
                System.out.println("state: "+response.state());
                System.out.println("server message: "+response.message());
                System.out.println("You want to:\n" +
                        "1 --> retry\n" +
                        "any key --> exit");
                int choice = scanner.nextInt();
                if (choice !=1)
                     System.exit(0);
            }
        }
    }
    public static String signup() throws IOException {

        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Please enter username:");
            String username = scanner.nextLine();
            System.out.println("Please enter password:");
            String password = scanner.nextLine();

            Socket socket = new Socket("127.0.0.1", 3000);

            AuthRequest request = new AuthRequest(username, password, "SIGNUP");
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF(new Gson().toJson(request));
            dataOutputStream.flush();

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            AuthResponse response = new Gson().fromJson(dataInputStream.readUTF(), AuthResponse.class);
            if (response.state().equals("200")) {
                System.out.println(response.message());
                return response.token();
            } else{
                System.out.println("state: "+response.state());
                System.out.println("server message: "+response.message());
                System.out.println("You want to:\n" +
                        "1 --> retry\n" +
                        "any key --> exit");
                int choice = scanner.nextInt();
                if (choice !=1)
                    System.exit(0);
            }
        }
    }
}
