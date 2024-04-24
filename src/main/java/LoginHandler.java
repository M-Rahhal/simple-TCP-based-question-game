import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginHandler extends Thread{
    private AuthRequest request;
    private Socket client;
    public LoginHandler(AuthRequest req , Socket client){
        this.request = req;
        this.client = client;
    }
    @Override
    public void run(){
        try {
            DataManager dataManager = DataManager.getInstance();
            User user = dataManager.getUser(request.username());
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            if (user == null){
                out.writeUTF(new Gson().toJson(new AuthResponse("401" , "User Not Found!" , null)));
                out.flush();
                return;
            }
            if (!user.getPassword().equals(request.password())){
                out.writeUTF(new Gson().toJson(new AuthResponse("402" , "Incorrect Password!" , null)));
                out.flush();
                return;
            }

            HashMap<String , Object> claims = new HashMap<>();
            claims.put("User" , new Gson().toJson(user));
            String token = JWTManager.createJWT(claims ,999999999 );
            user.setToken(token);
            dataManager.addUser(user);

            out.writeUTF(new Gson().toJson(new AuthResponse("200" , "Logged in successfully!" , token)));
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
