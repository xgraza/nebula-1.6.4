package ez.nebula.client.util.text;

import java.util.TreeMap;

/**
 * @author xgraza
 * @since 3/20/2026
 */
public final class FormattingUtil
{
    private static final TreeMap<Integer, String> ROMAN_NUMERALS_MAP = new TreeMap<>();

    static
    {
        // this genius solution is from StackOverFlow, I am lazy
        // https://stackoverflow.com/questions/12967896/converting-integers-to-roman-numerals-java
        ROMAN_NUMERALS_MAP.put(1000, "M");
        ROMAN_NUMERALS_MAP.put(900, "CM");
        ROMAN_NUMERALS_MAP.put(500, "D");
        ROMAN_NUMERALS_MAP.put(400, "CD");
        ROMAN_NUMERALS_MAP.put(100, "C");
        ROMAN_NUMERALS_MAP.put(90, "XC");
        ROMAN_NUMERALS_MAP.put(50, "L");
        ROMAN_NUMERALS_MAP.put(40, "XL");
        ROMAN_NUMERALS_MAP.put(10, "X");
        ROMAN_NUMERALS_MAP.put(9, "IX");
        ROMAN_NUMERALS_MAP.put(5, "V");
        ROMAN_NUMERALS_MAP.put(4, "IV");
        ROMAN_NUMERALS_MAP.put(1, "I");
    }

    public static String formatRomanNumeral(final int number)
    {
        int floored = ROMAN_NUMERALS_MAP.floorKey(number);
        if (floored == number)
        {
            return ROMAN_NUMERALS_MAP.get(number);
        }
        return ROMAN_NUMERALS_MAP.get(floored) + formatRomanNumeral(number - floored);
    }

    public static String formatSize(final int bytes)
    {
        if (bytes > 1E+9)
        {
            return String.format("%.1fGB", bytes / 1E+9);
        } else if (bytes > 1E+6)
        {
            return String.format("%.1fMB", bytes / 1E+6);
        } else if (bytes > 1000)
        {
            return String.format("%.1fKB", bytes / 1000.0);
        }

        return bytes + "B";
    }
}
