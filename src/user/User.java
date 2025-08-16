package user;

import currentmarket.CurrentMarketObserver;
import currentmarket.CurrentMarketSide;
import productbook.TradableDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class User implements CurrentMarketObserver {
    private String userId;
    private HashMap<String, TradableDTO> tradables;
    private HashMap<String, CurrentMarketSide[]> currentMarkets = new HashMap<>();

    public void updateTradable(TradableDTO o) {
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

    private void setUserId(String id) throws UserException {
        if(id == null || id.length() != 3 || !id.matches("[a-zA-Z]+")){
            throw new UserException("Invalid user ID");
        }
        userId = id;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public void updateCurrentMarket(String symbol, CurrentMarketSide buySide, CurrentMarketSide sellSide) {
        CurrentMarketSide[] market = {buySide, sellSide};
        currentMarkets.put(symbol, market);
    }

    public String getCurrentMarkets() {
        String market = "";

        for (String key : currentMarkets.keySet()) {
            market += String.format("%s %s - %s\n", key, currentMarkets.get(key)[0], currentMarkets.get(key)[1]);
        }
        return market;
    }
}
