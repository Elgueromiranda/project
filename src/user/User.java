package user;

import productbook.TradableDTO;

import java.util.HashMap;
import java.util.Objects;

public class User {
    private String userId;
    HashMap<String, TradableDTO> tradables;

    public void updateTradable (TradableDTO o) {
        if (!Objects.isNull(o)) {
            if (!tradables.containsKey(o.tradableId())) {
                tradables.put(o.tradableId(), o);
            } else {
                tradables.replace(o.tradableId(), o);
            }
        }
    }

    @Override
    public String toString() {
       String orders = "User Id: " + userId + "\n";
        for (TradableDTO t : tradables.values()) {
            orders = orders + String.format("\tProduct: %s, Price: %s, OriginalVolume: %s, RemainingVolume: %s, CancelledVolume: %s, FilledVolume: %s, User: %s, Side: %s, Id: %s\n",
                    t.product(), t.price(), t.originalVolume(), t.remainingVolume(), t.cancelledVolume(), t.filledVolume(), t.user(),t.side(),t.tradableId());
        }
        return orders;
    }

    public User(String id) throws UserException {
        setUserId(id);
        tradables = new HashMap<>();
    }

    public void setUserId(String id) throws UserException {
        if(id == null || id.length() != 3 || !id.matches("[a-zA-Z]+")){
            throw new UserException("Invalid user ID");
        }
        userId = id;
    }
}
