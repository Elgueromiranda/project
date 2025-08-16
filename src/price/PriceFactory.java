package price;

import java.util.*;

public abstract class PriceFactory {
    private static final Map<Integer, Price> priceCache = new HashMap<>();
    public static Price makePrice(int cents) {
        Price price = priceCache.get(cents);
        if (price == null) {
            price = new Price(cents);
            priceCache.put(cents, price);
            return price;
        }
        return price;
    }

    public static Price makePrice(String cents) throws InvalidPriceException {
        int length = cents.length();

        if (length > 0) {
            HashMap<Character, Integer> occurrences = new HashMap<>();
            String centsCopy = cents;

            for (char c : cents.toCharArray()) {
                occurrences.put(c, occurrences.getOrDefault(c,0) + 1);
            }

            centsCopy = centsCopy.replace("$", "").replace(",", "").replace(".", "").replace("-", "");

            if ((occurrences.containsKey('$') && occurrences.get('$') > 1) || (occurrences.containsKey('-') && occurrences.get('-') > 1) || (occurrences.containsKey('.') && occurrences.get('.') > 1) || !centsCopy.matches("^\\d+$")) {
                throw new InvalidPriceException("Invalid price String value: " + cents);
            }

            int dotTarget = cents.indexOf(".");
            if (dotTarget == -1) {
                String validatedCopy = cents;
                String mantissa = "00";
                if (occurrences.containsKey('$') && occurrences.containsKey('-') && !validatedCopy.substring(0,2).equals("$-")){
                    throw new InvalidPriceException("Invalid price. Inappropriate format: " + cents);
                } else if (occurrences.containsKey('$') && occurrences.containsKey('-') && validatedCopy.substring(0,2).equals("$-")) {
                    validatedCopy = validatedCopy.substring(2);
                    validatedCopy = commaValidation(occurrences, validatedCopy, cents);
                    return makePrice(Integer.parseInt("-" + validatedCopy + mantissa));
                } else if (occurrences.containsKey('$') && String.valueOf(validatedCopy.charAt(0)).equals("$")) {
                    validatedCopy = validatedCopy.substring(1);
                    validatedCopy = commaValidation(occurrences,validatedCopy,cents);
                    return makePrice(Integer.parseInt(validatedCopy + mantissa));
                } else if (occurrences.containsKey('-') && String.valueOf(validatedCopy.charAt(0)).equals("-")) {
                    validatedCopy = validatedCopy.substring(1);
                    validatedCopy = commaValidation(occurrences,validatedCopy,cents);
                    return makePrice(Integer.parseInt("-" + validatedCopy + mantissa));
                } else {
                    validatedCopy = commaValidation(occurrences,validatedCopy,cents);
                    return makePrice(Integer.parseInt(validatedCopy + mantissa));
                }
            } else if (dotTarget == length - 1 || dotTarget == length - 3 ) {
                String validatedCopy = cents.replace(".","");
                String mantissa = "00";
                if (dotTarget == length - 3) {
                    mantissa = validatedCopy.substring(validatedCopy.length() - 2);
                    validatedCopy = validatedCopy.substring(0,validatedCopy.length() - 2);
                }
                if (occurrences.containsKey('$') && occurrences.containsKey('-') && !validatedCopy.substring(0,2).equals("$-")){
                    throw new InvalidPriceException("Invalid price. Inappropriate format: " + cents);
                } else if (occurrences.containsKey('$') && occurrences.containsKey('-') && validatedCopy.substring(0,2).equals("$-")) {
                    validatedCopy = validatedCopy.substring(2);
                    validatedCopy = commaValidation(occurrences, validatedCopy, cents);
                    return makePrice(Integer.parseInt("-" + validatedCopy + mantissa));
                } else if (occurrences.containsKey('$') && String.valueOf(validatedCopy.charAt(0)).equals("$")) {
                    validatedCopy = validatedCopy.substring(1);
                    validatedCopy = commaValidation(occurrences,validatedCopy,cents);
                    return makePrice(Integer.parseInt(validatedCopy + mantissa));
                } else if (occurrences.containsKey('-') && String.valueOf(validatedCopy.charAt(0)).equals("-")) {
                    validatedCopy = validatedCopy.substring(1);
                    validatedCopy = commaValidation(occurrences,validatedCopy,cents);
                    return makePrice(Integer.parseInt("-" + validatedCopy + mantissa));
                } else {
                    validatedCopy = commaValidation(occurrences,validatedCopy,cents);
                    return makePrice(Integer.parseInt(validatedCopy + mantissa));
                }
            } else {
                    throw new InvalidPriceException("Invalid price String value: " + cents);
            }
        }
         throw new InvalidPriceException("No price value provided");
    }

    private static String commaValidation(HashMap<Character, Integer> occurrences,String validatedCopy, String cents) throws InvalidPriceException {
        if (occurrences.containsKey(',') && validatedCopy.length() > 3) {
            StringBuilder reversed = new StringBuilder(validatedCopy).reverse();
            for (int i = 3; i < validatedCopy.length(); i += 4) {
                if (reversed.charAt(i) != ',') {
                    throw new InvalidPriceException("Invalid price. comma error: " + cents);
                }
            }
            return validatedCopy.replace(",", "");
        }
        return validatedCopy;
    }

}
