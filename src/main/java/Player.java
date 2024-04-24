import lombok.Getter;
import lombok.Setter;

import java.net.Socket;


@Getter
@Setter
public class Player {
    private int points;
    private User user;
    private Socket socket;
    public Player(User user , Socket socket){
        this.socket = socket;
        this.user =user;
        this.points = 0;
    }

}
