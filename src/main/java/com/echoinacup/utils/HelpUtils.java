package com.echoinacup.utils;

import java.text.DecimalFormat;

public class HelpUtils {


    public static String formatString(String value) {
        double amount = Double.parseDouble(value);
        DecimalFormat formatter = new DecimalFormat("#,###");

        return formatter.format(amount);
    }
}
