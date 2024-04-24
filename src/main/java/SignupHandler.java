import com.google.gson.Gson;

import java.awt.image.ImageProducer;
import java.io.*;
import java.net.Socket;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

public class SignupHandler extends Thread{

    private AuthRequest request;
    private Socket client;

    public SignupHandler(AuthRequest request , Socket client) {
        this.request = request;
        this.client = client;
    }

    @Override
    public void run(){
        try {
            DataManager manager = DataManager.getInstance();
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            if (manager.getUser(request.username()) != null){
                out.writeUTF(new Gson().toJson(new AuthResponse("400" , "Username already taken!" , null)));
                out.flush();
                return;
            }

            User user = new User(request.username() , request.password());
            HashMap<String , Object> claims = new HashMap<>();
            claims.put("User" , new Gson().toJson(user));
            String token = JWTManager.createJWT(claims , 999999999);
            user.setToken(token);
            manager.addUser(user);

            out.writeUTF(new Gson().toJson(new AuthResponse("200" , "User created successfully!" , token)));
            out.flush();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
