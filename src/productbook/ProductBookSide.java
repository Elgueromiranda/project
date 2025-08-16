package productbook;

import price.InvalidPriceException;
import price.Price;
import user.DataValidationException;
import user.UserManager;

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
                summary += "\t\t<Empty>";
                return summary;
            }
            Set<Price> keys = bookEntries.keySet();
            for (Price price : keys) {
                summary += "\t\t" + price + ":\n";
                for (Tradable tradable : bookEntries.get(price)) {
                    summary += "\t\t\t" + tradable + ":\n";
                }

            }
            return summary;
    }

    public TradableDTO cancel(String tradableId) throws DataValidationException {
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
                    TradableDTO cancelled = new TradableDTO(t);
                    UserManager.getInstance().updateTradable(cancelled.user(), cancelled);
                    return cancelled;
                }
            }
        }
        return null;
    }


    public int topOfBookVolume(){
        int total = 0;

        if (side == BookSide.BUY) {
            try {
                Price key = bookEntries.firstKey();
                ArrayList<Tradable> volume = bookEntries.get(key);

                for( Tradable tradable : volume ){
                    total += tradable.getRemainingVolume();
                }
            } catch (Exception e) {
                return total;
            }
            return total;
        } else if (side == BookSide.SELL) {
            try {
                Price key = bookEntries.firstKey();
                ArrayList<Tradable> volume = bookEntries.get(key);

                for( Tradable tradable : volume ){
                    total = total + tradable.getRemainingVolume();
                }
            } catch (Exception e) {
                return total;
            }
            return total;
        }
      return 0;
    }

    public TradableDTO removeQuotesForUser(String username) throws DataValidationException {
        for (Price price : bookEntries.keySet()) {
            ArrayList<Tradable> tradables = bookEntries.get(price);
            for (Tradable t : tradables) {
                if (t.toString().contains("quote") && username.equals(t.getUser())) {
                    TradableDTO dto = null;
                        dto = cancel(t.getId());
                        UserManager.getInstance().updateTradable(dto.user(), dto);
                   return dto;
                }
            }
        }
        return null;
    }
    public TradableDTO add(Tradable tradable) throws ProductException, DataValidationException {
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
        TradableDTO order = new TradableDTO(tradable);
        UserManager.getInstance().updateTradable(order.user(), order);
        return order;
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
                        int rv = totalVolAtPrice;
                        t.setFilledVolume(t.getFilledVolume() + rv);
                        t.setRemainingVolume(0);
                        iter.remove();
                        UserManager.getInstance().updateTradable(t.getUser(), new TradableDTO(t));
                        totalVolAtPrice -= t.getFilledVolume();
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
                    Iterator<Tradable> iter = atPrice.iterator();

                    while (iter.hasNext()) {
                        Tradable t = iter.next();
                        double ratio = (double) t.getRemainingVolume() / totalVolAtPrice;
                        ratio = ratio + .005;
                        ratio = Math.floor(ratio * 100) / 100;
                        int toTrade = (int) Math.ceil((double) vol * ratio);
                        t.setFilledVolume(t.getFilledVolume() + toTrade);
                        t.setRemainingVolume(t.getRemainingVolume() - toTrade);

                        System.out.println(String.format("\t\tPARTIAL FILL: (%s %s) %s %s order: %s at %s, Orig Vol: %s, Rem Vol: %s, Fill Vol: %s, CXL Vol: %s, ID: %s",
                                t.getSide(), t.getFilledVolume(), t.getUser(), t.getSide(), t.getProduct(), t.getPrice(), t.getOriginalVolume(), t.getRemainingVolume(), t.getFilledVolume(), t.getCancelledVolume(), t.getId()));
                        remainder -= toTrade;
                        UserManager.getInstance().updateTradable(t.getUser(), new TradableDTO(t));
                    }
                }
            }
        } catch (Exception e) {
            return;
        }
    }
}
