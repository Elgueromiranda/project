package productbook;

import price.Price;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.TreeMap;

public class ProductBookSide {
    private BookSide side;
    final TreeMap<Price, ArrayList<Tradable>> bookEntries;
    ProductBookSide(BookSide orderType) {
        side = orderType;
    }
    public TradableDTO add(TradableDTO tradable) throws ProductException {
        if(tradable == null) {
            throw new ProductException("Tradable Object is null");
        }
        if(!bookEntries.containsKey(side)) {
            ArrayList<Tradable> updatedTradables;
            bookEntries.put(tradable.getPrice(), );
        }

    }
}
