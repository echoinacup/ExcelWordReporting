package com.echoinacup.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class HelpUtils {


    public static String formatStringNumberWithDelimiters(String value) throws NumberFormatException {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        if (!StringUtils.isNumeric(value)) {
            return value;
        }
        double amount = 0;
        DecimalFormat formatter = new DecimalFormat("#,###");
        if (StringUtils.isNotEmpty(value)) {
            String cut = value.replace(",", "");
            amount = Double.parseDouble(cut);
        }
        return formatter.format(amount);
    }

    public static String formatStringToLatitude(String value) throws NumberFormatException {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        if (!StringUtils.isNumeric(value)) {
            return value;
        }
        DecimalFormat formatter = new DecimalFormat("0.000000");
        return formatter.format(Double.parseDouble(value)).replace(',', '.');
    }

    public static String formatToSqrMeters(String value) throws NumberFormatException {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        if (!StringUtils.isNumeric(value)) {
            return value;
        }
        double amount = 0;
        DecimalFormat formatter = new DecimalFormat("###,###");
        if (StringUtils.isNotEmpty(value)) {
            amount = Double.parseDouble(value);
        }
        return formatter.format(amount) + " sq. m";
    }

    public static String formatThousands(String value) {
        if (StringUtils.isNotEmpty(value)) {
            if (!StringUtils.isNumeric(value)) {
                return value;
            }
            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

            symbols.setGroupingSeparator(' ');
            formatter.setDecimalFormatSymbols(symbols);
            return formatter.format(Long.valueOf(value));
        }
        return "";
    }

    public static String trimWithNonBrakeSpace(String str) {
        String result = str.replace('\u00A0', ' ').trim();
        return result;
    }

    public static String roundDecimalValues(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        if (!StringUtils.isNumeric(value)) {
            return value;
        }
        String res = value.replace(",", ".");
        BigDecimal bd = new BigDecimal(res);
        return bd.setScale(1, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    public static String handleTwoDots(String value) {
        String result = value;
        if (StringUtils.isNotEmpty(value) && StringUtils.endsWith(value, ".")) {
            result = value.substring(0, value.length() - 1);
        }
        return result;
    }

    public static boolean isListEmpty(List<String> l) {
        List<String> result = l.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        return result.isEmpty();
    }

    public static List<List<String>> fileterEmptySubsets(List<List<String>> subsets) {
        List<List<String>> filtered = subsets.stream().filter(l -> !isListEmpty(l)).collect(Collectors.toList());
        return filtered;
    }
}
