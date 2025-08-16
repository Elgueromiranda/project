package currentmarket;

import price.Price;
import price.PriceFactory;

public class CurrentMarketTracker {
    private volatile static CurrentMarketTracker instance;

    public static CurrentMarketTracker getInstance()
    {
        if (instance == null)
        {
            synchronized (CurrentMarketTracker.class)
            {
                if (instance == null) // Double-Check!
                    instance = new CurrentMarketTracker();
            }
        }
        return instance;
    }

    private CurrentMarketTracker() {

    }
    public void updateMarket(String symbol, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume) {
        Price marketWidth = null;
        if (buyPrice == null  || sellPrice == null) {
            marketWidth = PriceFactory.makePrice(0);
            if (buyPrice == null) {
                buyPrice = PriceFactory.makePrice(0);
            }
            if(sellPrice == null) {
                sellPrice = PriceFactory.makePrice(0);
            }
            CurrentMarketSide buySide = null;
            CurrentMarketSide sellSide = null;
            try {
                buySide = new CurrentMarketSide(buyPrice, buyVolume);
                sellSide = new CurrentMarketSide(sellPrice, sellVolume);
            } catch (CurrentMarketException e) {
                System.out.println("failed to update market:" + e.getMessage());
            }
            String currentMarket = "*********** Current Market ***********\n" +
                    "* %s %sx%s - %sx%s [%s]\n" +
                    "**************************************";
            System.out.println(String.format(currentMarket, symbol, buyPrice, buyVolume, sellPrice, sellVolume, marketWidth));
            try {
                CurrentMarketPublisher.getInstance().acceptCurrentMarket(symbol, buySide, sellSide);
            } catch (CurrentMarketException e) {
                System.out.println("CurrentMarketTracker: Failed to update" + e.getMessage());
            }
        }  else {
            int priceDiff = sellPrice.getCents() - buyPrice.getCents();
            marketWidth = PriceFactory.makePrice(priceDiff);
            CurrentMarketSide buySide = null;
            CurrentMarketSide sellSide = null;
            try {
                buySide = new CurrentMarketSide(buyPrice, buyVolume);
                sellSide = new CurrentMarketSide(sellPrice, sellVolume);
            } catch (CurrentMarketException e) {
                System.out.println("failed to update market CurrentMarketTracker:" + e.getMessage());
            }
            String currentMarket = "*********** Current Market ***********\n" +
                    "* %s %sx%s - %sx%s [%s]\n" +
                    "**************************************";
            System.out.println(String.format(currentMarket, symbol, buyPrice, buyVolume, sellPrice, sellVolume, marketWidth));
            try {
                CurrentMarketPublisher.getInstance().acceptCurrentMarket(symbol, buySide, sellSide);
            } catch (CurrentMarketException e) {
                System.out.println("CurrentMarketTracker: Failed to update" + e.getMessage());
            }
        }
    }


}
