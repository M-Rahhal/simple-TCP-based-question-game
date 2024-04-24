import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Question implements java.io.Serializable{
    private String question;
    private String [] choices;
    private int correctAnswer;
    private int points;

    public Question(){}

    public Question(String question, String[] choices, int correctAnswer, int points) {
        this.question = question;
        this.choices = choices;
        this.correctAnswer = correctAnswer;
        this.points = points;
    }

    @Override
    public String toString() {
        String s= question + " ("+points+ " points)\n";
        for(int i=0; i<choices.length; i++)
            s += choices[i]+"\n";
        return s;
    }
}
