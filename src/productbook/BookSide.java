package productbook;

import java.text.ParseException;

public enum BookSide {
    BUY, SELL;
    private BookSide orderType;
    BookSide(BookSide type) throws ProductException {
        if (type == null) {
            throw new ProductException("Order type is null");
        };
        orderType = type;
    }


}
