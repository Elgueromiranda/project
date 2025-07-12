package productbook;

import price.InvalidPriceException;
import price.Price;

import java.util.ArrayList;

public class ProductBook {
    String product;
    ProductBookSide buySide;
    ProductBookSide sellSide;

    public ProductBook(String symbol) {
        setProduct(symbol);
        buySide = new ProductBookSide(BookSide.BUY);
        sellSide = new ProductBookSide(BookSide.BUY);
    }

    public TradableDTO[] add(Quote qte) {
        if(qte == null) {
            throw new ProductException("Quote passed in is null");
        }
        removeQuotesForUser(qte.getUser());
        TradableDTO DTObuy = buySide.add(qte.getQuoteSide(BookSide.BUY));
        TradableDTO DTOsell = sellSide.add(qte.getQuoteSide(BookSide.SELL));
        tryTrade();
        return new TradableDTO[]{DTObuy,DTOsell};
    }

    public TradableDTO[] removeQuotesForUser(String username){
        TradableDTO DTObuy = buySide.removeQuotesForUser(username);
        TradableDTO DTOsell = sellSide.removeQuotesForUser(username);
        return new TradableDTO[]{DTObuy,DTOsell};
    }

    private void setProduct(String stockSymbol) {
        String stockSymbolCopy = stockSymbol.replaceAll("[a-zA-Z]", "").replaceAll("\\d", "").replace(".","");
        if (stockSymbol.isEmpty() || stockSymbol.length() > 5 || stockSymbol.contains(" ") || stockSymbolCopy.length() > 1 ){
            throw new ProductException("Invalid stock symbol");
        }
        product = stockSymbolCopy;
    }

    @Override
    public String toString() {
        String summary = "Product: " + product + "\n";
        summary += buySide.toString() + "\n";
        summary += sellSide.toString();
        return summary;
    }

    public TradableDTO add(Tradable tradable) throws ProductException {
        System.out.println("**ADD: " + tradable);
        if (tradable == null) {
            throw new ProductException("Tradable is null");
        }
        switch (tradable.getSide()) {
            case BUY:
              TradableDTO buy = buySide.add(tradable);
              return buy;
            case SELL:
             TradableDTO sell = sellSide.add(tradable);
                return sell;
            default:
                throw new ProductException("Unknown side: " + tradable.getSide());
        }
    }


    public String getTopOfBookString(BookSide side) {
        switch(side) {
            case BUY:
                return String.format( "Top of BUY book: Top of BUY book: $122.50 x 75");
            case SELL:
                return String.format("Top of SELL book: Top of SELL book: $122.90 x 100");
            default:
                throw new ProductException("Unknown side: " + side);
        }
    }

    public TradableDTO cancel(BookSide side, String orderId) {
        System.out.println("**CANCEL: " + side + " " + orderId);
        return  null;
    }

    public void tryTrade() {
        Price buy = buySide.topOfBookPrice();
        Price sell = sellSide.topOfBookPrice();

        if (buy == null || sell == null) {
            return;
        }
        ArrayList<Tradable> buyOrders = buySide.bookEntries.get(buy);
        ArrayList<Tradable> sellOrders = sellSide.bookEntries.get(sell);

        buyOrders.sort((t1, t2) -> Integer.compare(t2.getOriginalVolume(), t1.getOriginalVolume()));
        sellOrders.sort((t1, t2) -> Integer.compare(t2.getOriginalVolume(), t1.getOriginalVolume()));

        int totalToTrade = buyOrders.get(0).getOriginalVolume() + sellOrders.get(0).getOriginalVolume();


        while (totalToTrade > 0) {
            Price buyTop = buySide.topOfBookPrice();
            Price sellTop = sellSide.topOfBookPrice();
            if (buy == null || sell == null) {
                return;
            }
            try {
                if (sell.greaterThan(buy)) {
                    return;
                }
            } catch (Exception e) {
                int toTrade = buyOrders.get(buyOrders.size() - 1).getOriginalVolume() + sellOrders.get(sellOrders.size() - 1).getOriginalVolume();
                buySide.tradeOut(buy, toTrade);
                sellSide.tradeOut(buy, toTrade);
                totalToTrade -= toTrade;
            }
        }
        return;
    }
}