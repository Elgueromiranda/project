package price;

public class Price implements Comparable<Price>  {
    private int cents;

    public Price(int price){
        cents = price;
    }

    public boolean isNegative(){
        if (getCents() >= 0) {
            return false;
        }
        return true;
    }

    public Price add(Price price) throws InvalidPriceException {
        if (price != null) {
           return new Price(getCents() + price.getCents());
        }
        throw new InvalidPriceException("Price is null");
    }

    public Price subtract(Price price) throws InvalidPriceException {
        if (price != null) {
            return new Price(getCents() - price.getCents());
        }
        throw new InvalidPriceException("Price is null");
    }

    public Price multiply(int multiplier){
        return  new Price(getCents() * multiplier);
    }

    public boolean greaterOrEqual(Price price) throws InvalidPriceException {
        if (price != null) {
            if (getCents() >= price.getCents()) {
                return true;
            }
            return false;
        }
        throw new InvalidPriceException("Price is null");
    }

    public boolean lessOrEqual(Price price) throws InvalidPriceException {
        if (price != null) {
            if (getCents() <= price.getCents()) {
                return true;
            }
            return false;
        }
        throw new InvalidPriceException("Price is null");
    }

    public boolean greaterThan(Price price) throws InvalidPriceException {
        if (price != null) {
            if (getCents() > price.getCents()) {
                return true;
            }
        }
        throw new InvalidPriceException("Price is null");
    }

    public boolean lessThan(Price price)  throws InvalidPriceException  {
        if (price != null) {
            if (getCents() < price.getCents()) {
                return true;
            }
            return false;
        }
        throw new InvalidPriceException("Price is null");
    }



    @Override
    public String toString() {
        String price = Integer.toString(getCents());
        if (price.length() == 1) {
            return  "$" + "0.0" + price;
        } else if (price.length() < 3) {
            if (isNegative()) {
                int position = price.length() - 2;
                return  "$" + "0" + price.substring(0, position) + "." + price.substring(position);
            }
            int position = price.length() - 2;
            return "$" + "0"  + price.substring(0, position) + "." + price.substring(position);
        } else if (price.length() <= 3 && isNegative()) {
                int position = price.length() - 2;
                return  "$" + "-" + "0" + price.substring(1, position) + "." + price.substring(position);
        } else {
            if (isNegative()) {
                int position = price.length() - 2;
                return  "$" + price.substring(0, position) + "." + price.substring(position);
            }
            int position = price.length() - 2;
            char insertChar = ',';
            StringBuilder wholeNumber = new StringBuilder(price.substring(0, position));
            wholeNumber.reverse();
            for (int i = 3; i < wholeNumber.length(); i += 4) { // Increment by 4 to account for inserted characters
                wholeNumber.insert(i, insertChar);
            }
            wholeNumber.reverse();
            return "$" + wholeNumber + "." + price.substring(position);
        }

    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getCents());
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public int getCents() {
        return cents;
    }

    @Override
    public int compareTo(Price price) {
        if (price == null) {
        return -1;
        }
        return getCents() - price.getCents();
    }
}
