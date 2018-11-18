package com.echoinacup.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;

public class HelpUtils {


    public static String formatStringToNumber(String value) throws NumberFormatException {
        double amount = 0;
        DecimalFormat formatter = new DecimalFormat("#,###");
        if (StringUtils.isNotEmpty(value)) {
            String cut = value.replace(",", "");
            amount = Double.parseDouble(cut);
        }
        return formatter.format(amount);
    }

    public static String formatStringToLatitude(String value) throws NumberFormatException {
        double amount = 0;
        DecimalFormat formatter = new DecimalFormat("0.000000");
        amount = Double.parseDouble(value);
        return formatter.format(amount);
    }

    public static String formatToSqrMeters(String value) throws NumberFormatException {
        double amount = 0;
        DecimalFormat formatter = new DecimalFormat("###,###");
        if (StringUtils.isNotEmpty(value)) {
            amount = Double.parseDouble(value);
        }
        return formatter.format(amount) + " sq. m";
    }
}
