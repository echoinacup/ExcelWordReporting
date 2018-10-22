package com.echoinacup.main;

import com.echoinacup.service.word.Status;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestMethods {


    @Test
    public void testNumbers() {
        BigInteger amount = new BigInteger("500000000");
        int i = 1000000000;
        //TODO format with decimal places

        System.out.println(StringUtils.isNotEmpty("DFG"));
//        assertEquals();
    }


    @Test
    public void testStatusEnum() {
        assertEquals(Status.PUBLIC, Status.PUBLIC.valueOf("Public".toUpperCase()));
        assertEquals("Public", Status.PUBLIC);
        assertEquals("Private", Status.PRIVATE.status());
    }


    @Test
    public void chekc() {
        List<String> list = Arrays.asList("Arab National Bank", "Arab National Bank", "Arab National Bank",
                "Arab National Bank", "Test", "Test", "Test", "SHit", "SHit", "SHit");

        HashMap<String, Integer> frequencyMap = new LinkedHashMap<>();
        for (String a : list) {
            if (frequencyMap.containsKey(a)) {
                frequencyMap.put(a, frequencyMap.get(a) + 1);
            } else {
                frequencyMap.put(a, 1);
            }
        }

        frequencyMap.forEach((k, v) -> System.out.println(k + "  " + v));

    }

    @Test
    public void checkUtils() {

        assertEquals(false, StringUtils.isNotEmpty(null));
    }


}
