
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;


@Getter
@Setter
public class User implements Serializable , Comparable{
    private String username;
    private String password;
    private String token;
    private int gamesPlayed;
    private String signInDate;
    private int totalPoints;
    public User(String username , String password){
        this.username = username;
        this.password = password;
        this.gamesPlayed =0;
        this.signInDate = LocalDate.now().toString();
    }
    public void incPoints(int points){
        this.totalPoints+=points;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", gamesPlayed=" + gamesPlayed +
                ", signInDate='" + signInDate + '\'' +
                ", totalPoints=" + totalPoints +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        User user = (User) o;

        if (this.totalPoints> user.totalPoints)
            return 1;
        else if (this.totalPoints< user.totalPoints)
            return -1;
        else return 0;
    }
}

