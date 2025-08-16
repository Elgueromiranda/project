package productbook;

import currentmarket.CurrentMarketTracker;
import price.Price;
import price.PriceFactory;
import user.DataValidationException;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Optional;

import static productbook.BookSide.BUY;
import static productbook.BookSide.SELL;

public class ProductBook {
     private String product;
    private ProductBookSide buySide;
    private ProductBookSide sellSide;

    public ProductBook(String symbol) throws ProductException {
        setProduct(symbol);
        buySide = new ProductBookSide(BUY);
        sellSide = new ProductBookSide(SELL);
    }

    public TradableDTO[] add(Quote qte) throws ProductException {
        if (qte == null) {
            throw new ProductException("Quote passed in is null");
        }
        removeQuotesForUser(qte.getUser());
        TradableDTO DTObuy = null;
        TradableDTO DTOsell = null;
        try {
            DTObuy = buySide.add(qte.getQuoteSide(BUY));
            DTOsell = sellSide.add(qte.getQuoteSide(SELL));

        } catch (DataValidationException e) {

        }
        tryTrade();

        return new TradableDTO[]{DTObuy, DTOsell};
    }

    public TradableDTO[] removeQuotesForUser(String username) throws ProductException {
        if(username == null) {
           throw new ProductException("Failed to cancel null quote\n");
        }

        TradableDTO DTObuy = null;
        TradableDTO DTOsell = null;

        try {
            DTObuy = buySide.removeQuotesForUser(username);
            System.out.println("------------------------------------------");
            DTOsell = sellSide.removeQuotesForUser(username);
        } catch (DataValidationException e) {
            System.out.println("failed to remove user quotes\n" + e.getMessage());
        }
        TradableDTO[] DTO = new TradableDTO[]{DTObuy, DTOsell};
        updateMarket();
        return DTO;
    }

    private void setProduct(String symbol) throws ProductException {
        String symbolCopy = symbol.replaceAll("[a-zA-Z0-9.]", "");
        if (symbol.isEmpty() || symbol.length() > 5 || symbol.contains(" ") || symbolCopy.length() > 1 ){
            throw new ProductException("Invalid stock symbol");
        }
        product = symbol;
    }

    @Override
    public String toString() {
        String summary = "--------------------------------------------\n";
         summary += "Product: " + product + "\n";
        summary += buySide.toString() + "\n";
        summary += sellSide.toString() + "\n";
        summary += "--------------------------------------------\n";
        return summary;
    }

    public TradableDTO add(Tradable tradable) throws ProductException {
        if (tradable == null) {
            throw new ProductException("Tradable is null");
        }
        switch (tradable.getSide()) {
            case BUY:
                TradableDTO buy = null;
                try {
                    buy = buySide.add(tradable);
                } catch (DataValidationException e) {
                    System.out.println("failed to add tradable to productbook\n" + e.getMessage());
                }
                tryTrade();
                updateMarket();
                return buy;
            case SELL:
                TradableDTO sell = null;
                try {
                    sell = sellSide.add(tradable);
                } catch (DataValidationException e) {
                    System.out.println("failed to add tradable to productbook\n" + e.getMessage());
                }
                tryTrade();
                updateMarket();
                return sell;
            default:
                throw new ProductException("Unknown side: " + tradable.getSide());
        }
    }


    public String getTopOfBookString(BookSide side) {
        if (side == BUY) {
            Optional<Price> o = Optional.ofNullable(buySide.topOfBookPrice());
            if (o.isPresent()) {
                Price price = o.get();
                int volume = 0;
                ArrayList<Tradable> prices = buySide.bookEntries.get(price);
                for (Tradable tradable : prices) {
                    volume += tradable.getRemainingVolume();
                }
                return String.format("Top of BUY book: %s x %s", price.toString(), volume);
            }
            return "Top of BUY book: Top of BUY book: $0.00 x 0";
        } else if (side == SELL) {
            Optional<Price> o = Optional.ofNullable(sellSide.topOfBookPrice());
            if (o.isPresent()) {
                Price price = o.get();
                int volume = 0;
                ArrayList<Tradable> prices = sellSide.bookEntries.get(price);
                for (Tradable tradable : prices) {
                    volume += tradable.getRemainingVolume();
                }
                return String.format("Top of SELL book: %s x %s", price.toString(), volume);
            }
            return "Top of SELL book: Top of SELL book: $0.00 x 0";
        }
       return "";
    }

    public TradableDTO cancel(BookSide side, String orderId) throws ProductException {
        if (orderId == null) {
           throw new ProductException(String.format("Failed to cancel %s order\n", side));
        }
        if (side == BUY) {
            TradableDTO buy = null;
            try {
                buy = buySide.cancel(orderId);
                updateMarket();
                return buy;
            } catch (DataValidationException e) {
                System.out.println("failed to cancel order BUYSIDE" + orderId + "\n" + e.getMessage());
            }
        } else if (side == SELL) {
            TradableDTO sell = null;
            try {
                sell = sellSide.cancel(orderId);
                updateMarket();
                return sell;
            } catch (DataValidationException e) {
                System.out.println("failed to cancel order SELLSIDE" + orderId + "\n" + e.getMessage());
            }
        }
        return null;
    }

    private void updateMarket() {
        Price buy = buySide.topOfBookPrice();
        int buyVolume = buySide.topOfBookVolume();
        Price sell = sellSide.topOfBookPrice();
        int sellVolume = sellSide.topOfBookVolume();

        CurrentMarketTracker.getInstance().updateMarket(product, buy, buyVolume, sell, sellVolume);
    }

    public void tryTrade() {
        Price buy = buySide.topOfBookPrice();
        Price sell = sellSide.topOfBookPrice();

        if (buy == null || sell == null) {
            return;
        }
        int buyVolume = buySide.topOfBookVolume();
        int sellVolume = sellSide.topOfBookVolume();

        int totalToTrade = Math.max(buyVolume, sellVolume);

        while (totalToTrade > 0) {
            Price buyTop = buySide.topOfBookPrice();
            Price sellTop = sellSide.topOfBookPrice();
            buyVolume = buySide.topOfBookVolume();
            sellVolume = sellSide.topOfBookVolume();
            if (buyTop == null || sellTop == null) {
                return;
            }
            try {
                if (sellTop.greaterThan(buyTop)) {
                    return;
                }
            } catch (Exception e) {
                System.out.println("Price mismatch cant trade" + e.getMessage());
            }
            int toTrade = Math.min(buyVolume, sellVolume);
            buy = buySide.topOfBookPrice();
            buySide.tradeOut(buy, toTrade);
            sellSide.tradeOut(sell, toTrade);
            totalToTrade -= toTrade;
        }
        return;
    }
}