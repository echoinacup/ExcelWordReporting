package com.echoinacup.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;

public class HelpUtils {


    public static String formatString(String value) {
        double amount = 0;
        DecimalFormat formatter = new DecimalFormat("#,###");
        if (StringUtils.isNotEmpty(value)) {
            amount = Double.parseDouble(value);
        }
        return formatter.format(amount);
    }
}
