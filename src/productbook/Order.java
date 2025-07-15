package productbook;

import price.Price;

public class Order implements Tradable {
    private String user;
    private String product;
    private Price price;
    private BookSide side;
    private int originalVolume;
    private int remainingVolume;
    private int canceledVolume;
    private int filledVolume;
    private String id;

    public Order(String orderUser, String orderProduct, Price orderPrice,int orderOriginalVolume, BookSide orderSide) throws ProductException {
        setUser(orderUser);
        setProduct(orderProduct);
        setPrice(orderPrice);
        setSide(orderSide);
        setOriginalVolume(orderOriginalVolume);
        remainingVolume = originalVolume;
        canceledVolume = 0;
        filledVolume = 0;
        id = user + product + price + System.nanoTime();
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

    private void setSide(BookSide orderType) throws ProductException {
        if (orderType == null) {
            throw new ProductException("Quote side is null");
        }
        side = orderType;
    }

    private void setOriginalVolume(int volume) throws ProductException {
        if (volume < 0 || volume > 10000) {
            throw new ProductException("Invalid volume");
        }
        originalVolume = volume;
    }

    @Override
    public String toString() {
        return String.format("%s %s order: %s at %s, Orig Vol: %s, Rem Vol: %s, Fill Vol: %s, CXL Vol: %s, ID: %s",
                            user , side, product, price, originalVolume, remainingVolume, filledVolume, canceledVolume, id);
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
    public void setCancelledVolume(int newVol) {
            canceledVolume += newVol;
    }

    @Override
    public int getCancelledVolume() {
        return canceledVolume;
    }

    @Override
    public void setRemainingVolume(int volume) {
        remainingVolume =- volume;
    }

    @Override
    public TradableDTO makeTradableDTO() {
        return new TradableDTO(this);
    }

    @Override
    public Price getPrice() {
        return price;
    }

    @Override
    public void setFilledVolume(int volume) {
        filledVolume = volume;

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

    public int getCanceledVolume() {
        return canceledVolume;
    }

    public void setCanceledVolume(int volume) {
        canceledVolume = volume;
    }
}
