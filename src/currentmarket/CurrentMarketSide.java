package currentmarket;

import price.Price;

public class CurrentMarketSide {
    private Price price;
    private int volume;

    public CurrentMarketSide(Price sidePrice, int sideVolume) throws CurrentMarketException {
        setPrice(sidePrice);
        setVolume(sideVolume);
    }
    private void setVolume(int sideVolume) throws CurrentMarketException {
        if (sideVolume < 0) {
            throw new CurrentMarketException("invalid volume for current market side");
        }
        volume = sideVolume;
    }

    private void setPrice(Price sidePrice) throws CurrentMarketException {
        if (sidePrice == null) {
            throw new CurrentMarketException("Price cannot be null for current market side");
        }
        price = sidePrice;
    }

    @Override
    public String toString() {
        return price + "x" + volume;
    }
}
