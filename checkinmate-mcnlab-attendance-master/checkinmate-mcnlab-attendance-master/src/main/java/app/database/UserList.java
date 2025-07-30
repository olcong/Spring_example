package app.database;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;

public class UserList {
    static HashMap<Long, UserData> users = new HashMap<>();

    public static void addUser(Long id, String name) {
        users.put(id, new UserData(id, name));
    }

    public static boolean isValidUser(Long id){
        return users.containsKey(id);
    }

    public static String getName(Long id){ return users.get(id).name; }

    public static Collection<UserData> getAllUsers() {
        return users.values();
    }
}

// Entity for UserList - Package Private
class UserData {
    Long id;
    String name;

    // Constructor
    public UserData(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Method

}
