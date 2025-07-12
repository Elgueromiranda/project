package productbook;

import price.Price;

public class Quote {
    private String user;
    private String product;
    private QuoteSide buySide;
    private QuoteSide sellSide;

    public Quote(String symbol, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume, String userName) {
        String SymbolCopy = symbol.replaceAll("[a-zA-Z]", "").replaceAll("\\d", "").replace(".","");
        if (symbol.isEmpty() || symbol.length() > 5 || symbol.contains(" ") || SymbolCopy.length() > 1 ){
            throw new ProductException("Invalid stock symbol");
        }
        product = symbol;
        if (userName.length() != 3 || userName.contains(" ") || !userName.matches("[a-zA-Z]+")) {
            throw new ProductException("Invalid user code");
        }
        user = userName;


    }
    public QuoteSide getQuoteSide(BookSide sideIn) {
        switch (sideIn) {
            case BUY:
                return buySide;
            case SELL:
                return sellSide;
            default:
                throw new ProductException("Invalid side in");
        }
    }
    public String getUser() {
        return user;
    }
    public String getSymbol(){
        return product;
    }
}
