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
            String summary = "Side: " + side.name() + "\n";
            if (bookEntries.isEmpty()) {
                summary = summary + "\t\t<Empty>";
                return summary;
            }
            Set<Price> keys = bookEntries.keySet();
            for (Price price : keys) {
                summary = summary + "\t\t" + price + ":\n";
                for (Tradable tradable : bookEntries.get(price)) {
                    summary = summary + "\t\t\t" + tradable + ":\n";
                }

            }
            return summary;
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
                total += tradable.getRemainingVolume();
            }
            return total;
        } else if (side == BookSide.SELL) {
            Price key = bookEntries.firstKey();
            ArrayList<Tradable> volume = bookEntries.get(key);
            int total = 0;
            for( Tradable tradable : volume ){
                total = total + tradable.getRemainingVolume();
            }
            return total;
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
        Price top = topOfBookPrice();
        if (price == null) {
            return;
        }
        try {
            if (top.greaterOrEqual(price)) {
                ArrayList<Tradable> atPrice = bookEntries.get(top);
                int totalVolAtPrice = 0;
                for (Tradable tradable : atPrice) {
                    totalVolAtPrice += tradable.getRemainingVolume();
                }
                if (vol >= totalVolAtPrice) {
                    Iterator<Tradable> iter = atPrice.iterator();
                    while (iter.hasNext()) {
                        Tradable t = iter.next();
                        int rv = t.getRemainingVolume();
                        t.setFilledVolume(t.getOriginalVolume());
                        t.setRemainingVolume(0);
                        iter.remove();
                        if (atPrice.isEmpty()) {
                            bookEntries.remove(top);
                            System.out.println(String.format("\t\tFULL FILL: (%s %s) %s %s order: %s at %s, Orig Vol: %s, Rem Vol: %s, Fill Vol: %s, CXL Vol: %s, ID: %s",
                                    t.getSide(), t.getFilledVolume(), t.getUser(), t.getSide(), t.getProduct(), t.getPrice(), t.getOriginalVolume(), t.getRemainingVolume(), t.getFilledVolume(), t.getCancelledVolume(), t.getId()));
                            return;
                        }
                        System.out.println(String.format("\t\tFULL FILL: (%s %s) %s %s order: %s at %s, Orig Vol: %s, Rem Vol: %s, Fill Vol: %s, CXL Vol: %s, ID: %s",
                                t.getSide(), t.getOriginalVolume(), t.getUser(), t.getSide(), t.getProduct(), t.getPrice(), t.getOriginalVolume(), t.getRemainingVolume(), t.getFilledVolume(), t.getCancelledVolume(), t.getId()));
                    }

                } else {
                    int remainder = vol;
                    for (Tradable t : atPrice) {
                        double ratio = (double) t.getRemainingVolume() / totalVolAtPrice;
                        int toTrade = (int) Math.ceil((double) vol * ratio);
                        toTrade = Math.min(toTrade, remainder);
                        t.setFilledVolume(t.getFilledVolume() + toTrade);
                        t.setRemainingVolume(t.getRemainingVolume() - toTrade);
                        System.out.println(String.format("\t\tPARTIAL FILL: (%s %s) %s %s order: %s at %s, Orig Vol: %s, Rem Vol: %s, Fill Vol: %s, CXL Vol: %s, ID: %s",
                                t.getSide(), t.getFilledVolume(), t.getUser(), t.getSide(), t.getProduct(), t.getPrice(), t.getOriginalVolume(), t.getRemainingVolume(), t.getFilledVolume(), t.getCancelledVolume(), t.getId()));
                        remainder -= toTrade;
                    }

                }
            }
        } catch (Exception e) {
            return;
        }
    }
}
