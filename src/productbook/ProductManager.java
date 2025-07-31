package productbook;

import user.DataValidationException;
import user.UserManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static productbook.BookSide.BUY;
import static productbook.BookSide.SELL;

public final class ProductManager {
    private static ProductManager instance = new ProductManager();
    HashMap<String, ProductBook> productBooks;

    private ProductManager() {
        productBooks = new HashMap<>();
    }

    public static ProductManager getInstance() {
        return instance;
    }

    public TradableDTO[] addQuote(Quote q) throws DataValidationException, ProductException {
        if (q == null) {
            throw new DataValidationException("Quote cannot be null");
        }
        ProductBook book = productBooks.get(q.getSymbol());
        TradableDTO[] sides = book.removeQuotesForUser(q.getUser());
        TradableDTO buyside = addTradable(q.getQuoteSide(BUY));
        TradableDTO sellside = addTradable(q.getQuoteSide(SELL));
        return new TradableDTO[] {buyside, sellside};
    }

    public TradableDTO addTradable(Tradable o) throws DataValidationException, ProductException {
        if (o == null) {
            throw new DataValidationException("Tradable cannot be null. cannot add null.");
        }
        ProductBook book = productBooks.get(o.getProduct());
        TradableDTO tradable = book.add(o);
        UserManager manager = UserManager.getInstance();
        manager.updateTradable(o.getProduct(), new TradableDTO(o));
        return tradable;
    }

    public TradableDTO cancel(TradableDTO o) throws ProductException, DataValidationException {
        ProductBook book = productBooks.get(o.product());
        TradableDTO canceled = book.cancel(o.side(), o.tradableId());
        if (canceled == null) {
            System.out.println("Failed to cancel order");
        }
        return canceled;
    }

    public TradableDTO[] cancelQuote(String symbol, String user) throws ProductException, DataValidationException {
        ProductBook book = productBooks.get(symbol);
        TradableDTO[] canceled = book.removeQuotesForUser(user);
        return  canceled;
    }

    @Override
    public String toString() {
        String books = "";
        for (ProductBook productBook : productBooks.values()) {
            books += productBook.toString() + "\n";
        }
        return books;
    }

    public void addProduct(String symbol) throws DataValidationException, ProductException {
        if (symbol == null) {
            throw new DataValidationException("Invalid product symbol");
        }
        String symbolCopy = symbol.replaceAll("[a-zA-Z0-9.]", "");
        if (symbol.isEmpty() || symbol.length() > 5 || symbol.contains(" ") || symbolCopy.length() > 1 ){
            throw new DataValidationException("Invalid product symbol");
        }
        productBooks.put(symbol, new ProductBook(symbol));

    }


    public ProductBook getProductBook(String symbol) throws DataValidationException {
        if (!productBooks.containsKey(symbol)) {
            throw new DataValidationException("Product does not exist");
        }
      return productBooks.get(symbol);
    }
    public String getRandomProduct() throws DataValidationException {
        if (productBooks == null || productBooks.isEmpty()) {
            throw new DataValidationException("Invalid no products");
        }
        Random random = new Random();
        int index = random.nextInt(productBooks.size());
        ArrayList<String> products = new ArrayList<>(productBooks.keySet());
        return products.get(index);
    }
}
