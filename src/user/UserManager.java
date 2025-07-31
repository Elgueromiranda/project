package user;

import productbook.TradableDTO;

import java.util.Objects;
import java.util.TreeMap;

public final class UserManager {
    TreeMap<String, User> users;

    private static UserManager instance = new UserManager();

         public static UserManager getInstance() {
         return instance;
         }

         private UserManager(){
             users = new TreeMap<>();
         }
    public void init(String[] usersIn) throws DataValidationException {
        if (usersIn == null) {
            throw new DataValidationException("Users list is null");
        }
        for (String user : usersIn) {
            try {
                users.put(user, new User(user));
            } catch (UserException e) {
                throw new DataValidationException("User is null" + user);
            }
        }

    }
    public void updateTradable(String userId, TradableDTO tradable) throws DataValidationException {
        if (userId == null || tradable == null || !users.containsKey(userId)) {
            throw new DataValidationException("Invalid user id");
        }
        User user = users.get(userId);
        user.updateTradable(tradable);
    }

    @Override
    public String toString() {
        String trades = "";
       for (User user : users.values()) {
            trades += user.toString() + "\n";
       }
       return trades;
    }
}
