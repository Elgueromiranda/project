package productbook;

import price.Price;

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
        if(qte == null) {
            throw new ProductException("Quote passed in is null");
        }
        removeQuotesForUser(qte.getUser());
        TradableDTO DTObuy = buySide.add(qte.getQuoteSide(BUY));
        TradableDTO DTOsell = sellSide.add(qte.getQuoteSide(SELL));
        tryTrade();
        return new TradableDTO[]{DTObuy,DTOsell};
    }

    public TradableDTO[] removeQuotesForUser(String username) throws ProductException {
        if(username == null) {
           throw new ProductException("Failed to cancel null quote\n");
        }
        TradableDTO DTObuy = buySide.removeQuotesForUser(username);
        TradableDTO DTOsell = sellSide.removeQuotesForUser(username);
        return new TradableDTO[]{DTObuy,DTOsell};
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
              TradableDTO buy = buySide.add(tradable);
              tryTrade();
              return buy;
            case SELL:
             TradableDTO sell = sellSide.add(tradable);
             tryTrade();
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
                int volume = buySide.bookEntries.get(price).get(0).getRemainingVolume();
                return String.format("Top of BUY book: %s x %s", price.toString(), volume);
            }
            return "Top of BUY book: Top of BUY book: $0.00 x 0";
        } else if (side == SELL) {
            Optional<Price> o = Optional.ofNullable(sellSide.topOfBookPrice());
            if (o.isPresent()) {
                Price price = o.get();
                int volume = sellSide.bookEntries.get(price).get(0).getRemainingVolume();
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
           return buySide.cancel(orderId);
        } else if (side == SELL) {
           return sellSide.cancel(orderId);
        }
        return null;
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

            if (buyTop == null || sellTop == null) {
                return;
            }
            try {
                if (sellTop.greaterThan(buyTop)) {
                    return;
                }
            } catch (Exception e) {

            }
            int toTrade = Math.min(buyVolume, sellVolume);
            buySide.tradeOut(buy, toTrade);
            sellSide.tradeOut(sell, toTrade);
            totalToTrade -= toTrade;
        }
        return;
    }
}