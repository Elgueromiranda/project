package productbook;

public class ProductBook {
    String product;
    ProductBookSide buySide;
    ProductBookSide sellSide;

    @Override
    public String toString() {
        return "ProductBook{}";
    }

    public TradableDTO add(Tradable tradable) throws ProductException {
        System.out.println("**ADD: " + tradable);
        if (tradable == null) {
            throw new ProductException("Tradable is null");
        }
    }


    public String getTopOfBookString(Side side) {

    }
}