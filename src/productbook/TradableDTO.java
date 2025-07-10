package productbook;

import price.Price;

public record TradableDTO(String user, String product, Price price, int originalVolume, int remainingVolume, int cancelledVolume, int filledVolume, BookSide side, String tradableId) {
    TradableDTO(Tradable tradable) {
        this(tradable.getUser(), tradable.getProduct(), tradable.getPrice(), tradable.getOriginalVolume(), tradable.getRemainingVolume(), tradable.getCancelledVolume(), tradable.getFilledVolume(), tradable.getId());
    }

}
