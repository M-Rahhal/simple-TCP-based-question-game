import com.google.gson.Gson;
import lombok.SneakyThrows;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Game extends Thread{
    private Player player1;
    private Player player2;
    private DataManager dataManager;
    private Set<Question> askedQuestions;

    public Game(Socket socket1, Socket socket2, User user1 ,User user2) throws IOException, ClassNotFoundException {
        player1 = new Player(user1 ,socket1);
        player2 = new Player(user2 , socket2);
        dataManager = DataManager.getInstance();
        askedQuestions = new HashSet<>();
    }

    @SneakyThrows
    public void run() {

        for (int i = 0; i < 5; i++) {
            Question q = dataManager.getRandomQuestion();
            while (askedQuestions.contains(q))
                q = dataManager.getRandomQuestion();

            QuestionDTO dto = new QuestionDTO(q.getQuestion(), q.getChoices(), q.getPoints(), 0);

            //create a thread for each user to send the question
            Runnable firstTask = new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    DataOutputStream dataOutputStream = new DataOutputStream(player1.getSocket().getOutputStream());
                    dataOutputStream.writeUTF(new Gson().toJson(dto));
                    dataOutputStream.flush();
                }
            };
            Runnable secondTask = new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    DataOutputStream dataOutputStream = new DataOutputStream(player2.getSocket().getOutputStream());
                    dataOutputStream.writeUTF(new Gson().toJson(dto));
                    dataOutputStream.flush();
                }
            };

            Thread thread1 = new Thread(firstTask);
            Thread thread2 = new Thread(secondTask);
            thread1.start();
            thread2.start();
            thread2.join();
            thread1.join();

            final LocalDateTime[] player1time = {LocalDateTime.now()};
            final LocalDateTime[] player2time = {LocalDateTime.now()};
            final QuestionDTO[] firstAnswer = {null};
            final QuestionDTO[] secondAnswer = {null};

            //define threads to read the answers
            firstTask = new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    DataInputStream in = new DataInputStream(player1.getSocket().getInputStream());
                    firstAnswer[0] = new Gson().fromJson(in.readUTF(), QuestionDTO.class);
                    player1time[0] = LocalDateTime.now();
                }
            };
            secondTask = new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    DataInputStream in = new DataInputStream(player2.getSocket().getInputStream());
                    secondAnswer[0] = new Gson().fromJson(in.readUTF(), QuestionDTO.class);
                    player2time[0] = LocalDateTime.now();
                }
            };

            thread1 = new Thread(firstTask);
            thread2 = new Thread(secondTask);
            thread1.start();
            thread2.start();
            thread1.join();
            thread2.join();

            //check the answers
            int correctAnswer = q.getCorrectAnswer();
            //if the second player answered first
            if (player1time[0].toString().compareTo(player2time[0].toString()) == 1) {
                int playerAnswer = secondAnswer[0].answer();
                // if the second answer is correct
                if (playerAnswer == correctAnswer) {
                    sendMessageToBothPlayers(
                            "Player 2 (" + player2.getUser().getUsername() + ") got the points!\n" +
                                    "Correct answer is: " + correctAnswer
                    );
                    player2.setPoints(player2.getPoints() + q.getPoints());
                }
                //if not we check for the first player's answer
                else if (firstAnswer[0].answer() == correctAnswer) {
                    sendMessageToBothPlayers(
                            "Player 1 (" + player1.getUser().getUsername() + ") got the points!\n" +
                                    "Correct answer is: " + correctAnswer
                    );
                    player1.setPoints(player1.getPoints() + q.getPoints());
                }
                // if No answer is correct
                else {
                    sendMessageToBothPlayers(
                            "No one got the points!\n" +
                                    "Correct answer is: " + correctAnswer
                    );
                }
            }
            //if the first player answered first
            else if (player1time[0].toString().compareTo(player2time[0].toString()) == -1) {
                int playerAnswer = secondAnswer[0].answer();
                // if the first answer is correct
                if (playerAnswer == correctAnswer) {
                    sendMessageToBothPlayers(
                            "Player 1 (" + player1.getUser().getUsername() + ") got the points!\n" +
                                    "Correct answer is: " + correctAnswer
                    );
                    player1.setPoints(player1.getPoints() + q.getPoints());
                }
                //if not we check for the second player's answer
                else if (secondAnswer[0].answer() == correctAnswer) {
                    sendMessageToBothPlayers(
                            "Player 2 (" + player2.getUser().getUsername() + ") got the points!\n" +
                                    "Correct answer is: " + correctAnswer
                    );
                    player2.setPoints(player2.getPoints() + q.getPoints());
                }
                // if No answer is correct
                else {
                    sendMessageToBothPlayers(
                            "You both answered correctly, No one got the points!\n" +
                                    "Correct answer is: " + correctAnswer
                    );
                }
            }
            //if both answered at the same time
            else {
                sendMessageToBothPlayers(
                        "You both answered incorrectly, No one got the points!\n" +
                                "Correct answer is: " + correctAnswer
                );
            }
        }

        StringBuilder builder = new StringBuilder("");
        builder.append("Player1 points ("+player1.getUser().getUsername()+"): "+player1.getPoints()+"\n");
        builder.append("Player2 points ("+player2.getUser().getUsername()+"): "+player2.getPoints()+"\n");
        if (player1.getPoints()> player2.getPoints())
            builder.append("Winner: Player 1");
        else if (player1.getPoints()< player2.getPoints())
            builder.append("Winner: Player 2");
        else builder.append("Draw");

        User user1 = player1.getUser();
        User user2 = player2.getUser();
        user1.setGamesPlayed(user1.getGamesPlayed()+1);
        user2.setGamesPlayed(user2.getGamesPlayed()+1);
        user1.incPoints(player1.getPoints());
        user2.incPoints(player2.getPoints());
        dataManager.addUser(user1);
        dataManager.addUser(user2);

        sendMessageToBothPlayers(builder.toString());
    }

    public void sendMessageToBothPlayers(String message) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(player1.getSocket().getOutputStream());
        dataOutputStream.writeUTF(message);
        dataOutputStream.flush();

        dataOutputStream = new DataOutputStream(player2.getSocket().getOutputStream());
        dataOutputStream.writeUTF(message);
        dataOutputStream.flush();
    }


}
