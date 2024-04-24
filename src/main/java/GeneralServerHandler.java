import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import io.jsonwebtoken.Claims;
import lombok.SneakyThrows;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.SignatureException;
import java.util.LinkedHashMap;
import java.util.List;

public class GeneralServerHandler extends Thread{

    private Socket socket;
    private GeneralServerRequest request;

    public GeneralServerHandler(Socket socket, GeneralServerRequest request) {
        this.socket = socket;
        this.request = request;
    }

    public void handleResult() throws SignatureException, IOException, ClassNotFoundException {
        Claims claims = JWTManager.decodeJWT(request.token());
        User u = new Gson().fromJson((String)claims.get("User") , User.class);
        User user = DataManager.getInstance().getUser(u.getUsername());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF(user.toString());
        out.flush();

    }

    public void handleLeaderboard() throws SignatureException, IOException, ClassNotFoundException {
        Claims claims = JWTManager.decodeJWT(request.token());
        User u = new Gson().fromJson((String)claims.get("User") , User.class);

        StringBuilder builder = new StringBuilder("");
        List<User> list = DataManager.getInstance().getTopFiveUsers();

        for (User user: list)
            builder.append(user.toString()+"\n");

        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF(builder.toString());
        out.flush();
    }

    @SneakyThrows
    public void run(){
        switch (request.type()){
            case "RESULT":
                handleResult();
                break;
            case "LEADERBOARD":
                handleLeaderboard();
                break;
        }
    }
}
