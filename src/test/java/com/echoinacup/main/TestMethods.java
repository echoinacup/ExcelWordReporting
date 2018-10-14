package com.echoinacup.main;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestMethods {


    @Test
    public void testNumbers(){
        BigInteger amount = new BigInteger("500000000");
        int i = 1000000000;
        //TODO format with decimal places

        System.out.println(StringUtils.isNotEmpty("DFG"));
//        assertEquals();
    }
}
