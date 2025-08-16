package productbook;

import user.DataValidationException;
import user.UserManager;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static productbook.BookSide.BUY;
import static productbook.BookSide.SELL;

public final class ProductManager {
    private static ProductManager instance = new ProductManager();
    private HashMap<String, ProductBook> productBooks;

    private ProductManager() {
        productBooks = new HashMap<>();
    }

    public static ProductManager getInstance() {
        return instance;
    }

    public TradableDTO[] addQuote(Quote q) throws DataValidationException {
        if (q == null) {
            throw new DataValidationException("Quote cannot be null");
        }
        ProductBook book = productBooks.get(q.getSymbol());
        TradableDTO buyside = null;
        TradableDTO sellside = null;
        try {
            TradableDTO[] sides = book.removeQuotesForUser(q.getUser());
            buyside = addTradable(q.getQuoteSide(BUY));
            sellside = addTradable(q.getQuoteSide(SELL));
        } catch (ProductException e) {
            System.out.println("Error adding quote for user " + e.getMessage());
        }
        return new TradableDTO[]{buyside, sellside};
    }

    public TradableDTO addTradable(Tradable o) throws DataValidationException {
        if (o == null) {
            throw new DataValidationException("Tradable cannot be null. cannot add null.");
        }
        ProductBook book = productBooks.get(o.getProduct());
        TradableDTO tradable = null;
        try {
            tradable = book.add(o);
        } catch (ProductException e) {
            System.out.println("Error adding tradable for user to product manager " + e.getMessage());
        }
        UserManager manager = UserManager.getInstance();
        manager.updateTradable(o.getUser(), new TradableDTO(o));
        return tradable;
    }

    public TradableDTO cancel(TradableDTO o) throws ProductException {
        ProductBook book = productBooks.get(o.product());
        TradableDTO canceled = book.cancel(o.side(), o.tradableId());
        if (canceled == null) {
            System.out.println("Failed to cancel order");
        }
        return canceled;
    }

    public TradableDTO[] cancelQuote(String symbol, String user) throws ProductException {
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

    public void addProduct(String symbol) throws DataValidationException {
        if (symbol == null) {
            throw new DataValidationException("Invalid product symbol");
        }
        String symbolCopy = symbol.replaceAll("[a-zA-Z0-9.]", "");
        if (symbol.isEmpty() || symbol.length() > 5 || symbol.contains(" ") || symbolCopy.length() > 1 ){
            throw new DataValidationException("Invalid product symbol");
        }
        try {
            productBooks.put(symbol, new ProductBook(symbol));
        } catch (ProductException e) {
            System.out.println("Error adding product for user " + e.getMessage());
        }

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
