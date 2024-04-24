
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class DataManager {
    private  ConcurrentHashMap<String, User> users;
    private  ArrayList<Question> questions;

    private static DataManager dataManager;

    private DataManager() throws IOException, ClassNotFoundException {
        readQuestions();
        readUsers();
    }
    public static synchronized DataManager getInstance() throws IOException, ClassNotFoundException {
        if (DataManager.dataManager == null)
            DataManager.dataManager = new DataManager();
        return DataManager.dataManager;
    }

    private void readUsers() throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(new File("users.out")));
        users = (ConcurrentHashMap<String, User>) objectInputStream.readObject();

    }

    private void readQuestions() throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("questions.out"));
        questions = (ArrayList<Question>)objectInputStream.readObject();
    }

    public List<User> getTopFiveUsers(){
        List<User> list = new ArrayList<>();
        for (String s: users.keySet())
            list.add(users.get(s));
        Collections.sort(list);
        if (list.size()<=5)
            return list;
        else
            return list.subList(list.size()-6 , list.size()-1);
    }

    private void saveUsers() throws IOException {
        synchronized (users) {

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(new File("users.out")));
            objectOutputStream.writeObject(users);
        }
    }

    public void addUser(User user) throws IOException {
        users.put(user.getUsername() , user);
        saveUsers();
    }
    public User getUser(String username){

        User user = users.get(username);
        return users.get(username);
    }

    public Question getRandomQuestion(){
        int randomNumber = ThreadLocalRandom.current().nextInt(0, questions.size());
        return questions.get(randomNumber);
    }


}
