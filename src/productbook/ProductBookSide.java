package productbook;

import price.InvalidPriceException;
import price.Price;

import java.awt.print.Book;
import java.util.*;
import java.util.stream.Collectors;

import static productbook.BookSide.BUY;
import static productbook.BookSide.SELL;

public class ProductBookSide {
    private BookSide side;
    final TreeMap<Price, ArrayList<Tradable>> bookEntries;

    public ProductBookSide(BookSide orderType) {
        side = orderType;
        if (side == BUY) {
            bookEntries = new TreeMap<>(Collections.reverseOrder());
        } else  {
            bookEntries = new TreeMap<>();
        }
    }

    @Override
    public String toString() {
        if (side == BUY) {
            String summary = "Side: " + side.name() + "\n";
            if (bookEntries.isEmpty()) {
                summary = summary + "\t\t<Empty>";
                return summary;
            }
            Set<Price> keys = bookEntries.keySet();
            for (Price price : keys) {
               summary = summary + "\tPrice: " + price.toString() + "\n";
               summary = summary + "\t\t" +  bookEntries.get(price).get(0).toString() + "\n";
            }
            return summary;
        } else if (side == SELL) {
            String summary = "Side: " + side.name() + "\n";
            if (bookEntries.isEmpty()) {
                summary = summary + "\t\t<Empty>";
                return summary;
            }
            Set<Price> keys = bookEntries.keySet();
            for (Price price : keys) {
                summary = summary + "\tPrice: " + price.toString() + "\n";
                summary = summary + "\t\t" +  bookEntries.get(price).get(0).toString() + "\n";
            }
            return summary;
        }

        return "";
    }

    public TradableDTO cancel(String tradableId) {
        Set<Price> prices = bookEntries.keySet();
        for (Price price : prices) {
            ArrayList<Tradable> tradables = bookEntries.get(price);
            for (Tradable t : tradables) {
                if (t.getId().equals(tradableId)) {
                    System.out.println("**CANCEL: " + t);
                    tradables.remove(t);
                    t.setCancelledVolume(t.getRemainingVolume());
                    t.setRemainingVolume(0);
                    if (tradables.isEmpty()) {
                        bookEntries.remove(price);
                    }
                    return new TradableDTO(t);
                }
            }
        }
        return null;
    }


    public int topOfBookVolume(){
        if (side == BookSide.BUY) {
            Price key = bookEntries.lastKey();
            ArrayList<Tradable> volume = bookEntries.get(key);
            int total = 0;
            for( Tradable tradable : volume ){
                total =+ tradable.getRemainingVolume();
            }
            return total;
        } else if (side == BookSide.SELL) {
            Price key = bookEntries.firstKey();
            ArrayList<Tradable> volume = bookEntries.get(key);
            int total = 0;
            for( Tradable tradable : volume ){
                total =+ tradable.getRemainingVolume();
            }
        }
      return 0;
    }

    public TradableDTO removeQuotesForUser(String username) {
        for (Price price : bookEntries.keySet()) {
            ArrayList<Tradable> tradables = bookEntries.get(price);
            for (Tradable t : tradables) {
                if (t.toString().contains("quote") && username.equals(t.getUser())) {
                   TradableDTO dto = cancel(t.getId());
                   return dto;
                }
            }
        }
        return null;
    }
    public TradableDTO add(Tradable tradable) throws ProductException {
        if(tradable == null) {
            throw new ProductException("Tradable Object is null");
        }
        System.out.println("**ADD: " + tradable);
        if(bookEntries.containsKey(tradable.getPrice())) {
            bookEntries.get(tradable.getPrice()).add(tradable);
        } else {
            ArrayList<Tradable> value = new ArrayList<>();
            value.add(tradable);
            bookEntries.put(tradable.getPrice(), value);
        }
        return new TradableDTO(tradable);
    }

    public Price topOfBookPrice() {
        if (bookEntries.isEmpty()) {
            return null;
        }
        return bookEntries.firstKey();
    }

    public void tradeOut(Price price, int vol) {
        Price top =  topOfBookPrice();
        if (top == null) {
            return;
        }
        try {
            if (top.greaterThan(price)) {
                ArrayList<Tradable> atPrice = bookEntries.get(top);
                int totalVolAtPrice = 0;
                for (Tradable tradable : atPrice) {
                    totalVolAtPrice =+ tradable.getRemainingVolume();
                }
                if (vol > totalVolAtPrice) {
                    for (Tradable t : atPrice) {
                      int rv = t.getRemainingVolume();
                      t.setFilledVolume(t.getOriginalVolume());
                      t.setRemainingVolume(0);
                        System.out.println(String.format("FULL FILL: (%s %s) %s %s order: %s at %s, Orig Vol: %s, Rem Vol: %s, Fill Vol: %s, CXL Vol: %s, ID: %s",
                                                t.getSide(), t.getOriginalVolume(), t.getUser(), t.getSide(), t.getProduct(), t.getPrice(), t.getOriginalVolume(), t.getRemainingVolume(), t.getFilledVolume(), t.getCancelledVolume(), t.getId()));
                    }
                } else {
                    int remainder = vol;
                    for (Tradable t : atPrice) {
                        int ratio = t.getRemainingVolume() / totalVolAtPrice;
                        int oTrade = vol * ratio;
                        vol = vol - oTrade;
                    }

                }
            }
        } catch (Exception e) {
            return;
        }
    }
}
