package productbook;

import price.Price;

public class QuoteSide implements Tradable {
    private String user;
    private String product;
    private Price price;
    private int originalVolume;
    private int remainingVolume;
    private int cancelledVolume;
    private int filledVolume;
    private String id;
    private BookSide side;


    public QuoteSide(String userQuote, String productQuote, Price priceQuote, int originalVolumeQuote) {
        setPrice(priceQuote);
        setOriginalVolume(originalVolumeQuote);
        setUser(userQuote);
        setProduct(productQuote);
        id = user + product + price + System.nanoTime();
        remainingVolume = originalVolume;
        cancelledVolume = 0;
        filledVolume = 0;

    }

    @Override
    public String toString() {
        return String.format("%s %s side quote for %s: %s, Orig Vol: %s, Rem Vol: %s, Fill Vol: %s, CXL Vol: %s, ID: %s", user,
                side, product, price, originalVolume,remainingVolume, filledVolume, cancelledVolume, id);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getRemainingVolume() {
        return remainingVolume;
    }



    @Override
    public int getCancelledVolume() {
        return cancelledVolume;
    }


    public TradableDTO makeTradableDTO() {

    }

    @Override
    public Price getPrice() {
        return price;
    }


    @Override
    public int getFilledVolume() {
        return filledVolume;
    }

    @Override
    public BookSide getSide() {
        return side;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public String getProduct() {
        return product;
    }

    @Override
    public int getOriginalVolume() {
        return originalVolume;
    }

    private void setOriginalVolume(int volume) throws ProductException {
        if (volume < 0 || volume < 10000) {
            throw new ProductException("Invalid volume");
        }
        originalVolume = volume;
    }

    private void setUser(String usercode) throws ProductException {
        if (usercode.length() != 3 || usercode.contains(" ") || !usercode.matches("[a-zA-Z]+") {
            throw new ProductException("Invalid user code");
        }
        user = usercode;
    }

    private void setProduct(String stockSymbol) {
        String stockSymbolCopy = stockSymbol.replaceAll("[a-zA-Z]", "").replaceAll("\\d", "").replace(".","");
        if (stockSymbol.isEmpty() || stockSymbol.length() > 5 || stockSymbol.contains(" ")) || stockSymbolCopy.length() > 1 ){
            throw new ProductException("Invalid stock symbol");
        }
        product = stockSymbolCopy;
    }

    private void setPrice(Price priceObject) {
        if (priceObject == null) {
            throw new ProductException("Quote price is null");
        }
        price = priceObject;
    }

    private void setId(String id) {
        id = id;
    }

    private void setSide(BookSide side) {
        side = side;
    }

    @Override
    public void setCancelledVolume(int newVol) {

    }
    @Override
    public void setFilledVolume(int newVol) {

    }

    @Override
    public void setRemainingVolume(int newVol) {

    }


}
