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

    @Override
    public String toString() {
        return String.format("%s %s order: %s at %s, Orig Vol: %s, Rem Vol: %s, Fill Vol: %s, CXL Vol: %s, ID: %s",getUser(), getSide(), getPrice(), getOriginalVolume(), getRemainingVolume(), getFilledVolume(), getCancelledVolume(), getId()
        );
    }
    @Override
    public String getId() {
        return "";
    }

    @Override
    public int getRemainingVolume() {
        return 0;
    }

    @Override
    public void setCancelledVolume(int newVol) {

    }

    @Override
    public int getCancelledVolume() {
        return 0;
    }

    @Override
    public void setRemainingVolume(int newVol) {

    }

    @Override
    public TradableDTO makeTradableDTO() {
        return null;
    }

    @Override
    public Price getPrice() {
        return null;
    }

    @Override
    public void setFilledVolume(int newVol) {

    }

    @Override
    public int getFilledVolume() {
        return 0;
    }

    @Override
    public BookSide getSide() {
        return null;
    }

    @Override
    public String getUser() {
        return "";
    }

    @Override
    public String getProduct() {
        return "";
    }

    @Override
    public int getOriginalVolume() {
        return 0;
    }
}
