package currentmarket;

import price.InvalidPriceException;
import productbook.ProductException;

import java.util.ArrayList;
import java.util.HashMap;

public class CurrentMarketPublisher {
    private volatile static CurrentMarketPublisher instance;

    private HashMap<String, ArrayList<CurrentMarketObserver>> filters = new HashMap<>();

    public static CurrentMarketPublisher getInstance()
    {
        if (instance == null)
        {
            synchronized (CurrentMarketPublisher.class)
            {
                if (instance == null) // Double-Check!
                    instance = new CurrentMarketPublisher();
            }
        }
        return instance;
    }

    private CurrentMarketPublisher() {

    }

    public void subscribeCurrentMarket(String symbol, CurrentMarketObserver cmo) throws CurrentMarketException {
        if (cmo == null) {
            throw new CurrentMarketException("CurrentMarketObserver is null");
        }
        validateSymbol(symbol);
        if (!filters.containsKey(symbol)) {
            filters.put(symbol, new ArrayList<CurrentMarketObserver>());
        }
        filters.get(symbol).add(cmo);
    }

    public void unSubscribeCurrentMarket(String symbol, CurrentMarketObserver cmo) throws CurrentMarketException {
        if (cmo == null) {
            throw new CurrentMarketException("failed to unsubscribe null current market observer");
        }
        validateSymbol(symbol);
        if (filters.containsKey(symbol)) {
            filters.get(symbol).remove(cmo);
        }
    }
    public void acceptCurrentMarket(String symbol, CurrentMarketSide buySide, CurrentMarketSide sellSide) throws CurrentMarketException {
        validateSymbol(symbol);
        if (filters.containsKey(symbol)) {
            ArrayList<CurrentMarketObserver> observers = filters.get(symbol);
            for (CurrentMarketObserver observer : observers) {
                observer.updateCurrentMarket(symbol, buySide, sellSide);
            }
            return;
        }
        System.out.println("there are no CurrentMarketObservers registered for that symbol");
    }


    private void validateSymbol(String symbol) throws CurrentMarketException {
        String symbolCopy = symbol.replaceAll("[a-zA-Z0-9.]", "");
        if (symbol.isEmpty() || symbol.length() > 5 || symbol.contains(" ") || symbolCopy.length() > 1 ){
            throw new CurrentMarketException("Invalid symbol for CurrentMarketPublisher");
        }
    }

}
