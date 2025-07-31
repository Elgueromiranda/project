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


    public QuoteSide(String userQuote, String productQuote, Price priceQuote, int originalVolumeQuote, BookSide sideQuote) throws ProductException {
        setPrice(priceQuote);
        try {
            setOriginalVolume(originalVolumeQuote);
            setUser(userQuote);
        } catch (Exception e) {
            System.out.println("invalid volume:"+e);
        }

        setProduct(productQuote);
        id = user + product + price + System.nanoTime();
        remainingVolume = originalVolume;
        cancelledVolume = 0;
        filledVolume = 0;
        setSide(sideQuote);
    }

    @Override
    public String toString() {
        return String.format("%s %s side quote for %s: %s, Orig Vol: %s, Rem Vol: %s, Fill Vol: %s, CXL Vol: %s, ID: %s",
                user, side, product, price, originalVolume,remainingVolume, filledVolume, cancelledVolume, id);
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
        return new TradableDTO(this);
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
        if (volume < 0 || volume > 10000) {
            throw new ProductException("Invalid volume");
        }
        originalVolume = volume;
    }

    private void setUser(String usercode) throws ProductException {
        if (usercode.length() != 3 || usercode.contains(" ") || !usercode.matches("[a-zA-Z]+")) {
            throw new ProductException("Invalid user code");
        }
        user = usercode;
    }

    private void setProduct(String symbol) throws ProductException {
        String symbolCopy = symbol.replaceAll("[a-zA-Z0-9.]", "");
        if (symbol.isEmpty() || symbol.length() > 5 || symbol.contains(" ") || symbolCopy.length() > 1 ){
            throw new ProductException("Invalid stock symbol");
        }
        product = symbol;
    }

    private void setPrice(Price priceObject) throws ProductException {
        if (priceObject == null) {
            throw new ProductException("Quote price is null");
        }
        price = priceObject;
    }

    private void setId(String id) {
        id = id;
    }

    private void setSide(BookSide orderType) throws ProductException {
        if (orderType == null) {
            throw new ProductException("Quote side is null");
        }
        side = orderType;
    }

    @Override
    public void setCancelledVolume(int volume) {
        cancelledVolume += volume;
    }
    @Override
    public void setFilledVolume(int volume) {
        filledVolume = volume;
    }

    @Override
    public void setRemainingVolume(int volume) {
        remainingVolume = volume;
    }


}
