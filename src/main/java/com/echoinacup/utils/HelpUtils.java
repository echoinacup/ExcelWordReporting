package com.echoinacup.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class HelpUtils {


    public static String formatStringNumberWithDelimiters(String value) throws NumberFormatException {
        double amount = 0;
        DecimalFormat formatter = new DecimalFormat("#,###");
        if (StringUtils.isNotEmpty(value)) {
            String cut = value.replace(",", "");
            amount = Double.parseDouble(cut);
        }
        return formatter.format(amount);
    }

    public static String formatStringToLatitude(String value) throws NumberFormatException {
        DecimalFormat formatter = new DecimalFormat("0.000000");
        return formatter.format(Double.parseDouble(value)).replace(',', '.');
    }

    public static String formatToSqrMeters(String value) throws NumberFormatException {
        double amount = 0;
        DecimalFormat formatter = new DecimalFormat("###,###");
        if (StringUtils.isNotEmpty(value)) {
            amount = Double.parseDouble(value);
        }
        return formatter.format(amount) + " sq. m";
    }

    public static String formatThousands(String value) {
        if (StringUtils.isNotEmpty(value)) {
            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

            symbols.setGroupingSeparator(' ');
            formatter.setDecimalFormatSymbols(symbols);
            return formatter.format(Long.valueOf(value));
        }
        return "";
    }

    public static String trimWithNonBrackeSpace(String str) {
        String result = str.replace('\u00A0', ' ').trim();
        return result;
    }
}
