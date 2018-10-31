package com.echoinacup.main;

import com.echoinacup.service.word.Status;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.file.FileSystem;
import java.text.DecimalFormat;
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
        assertEquals("Public", Status.PUBLIC.status());
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
        List<Integer> intList = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);
        List<String> list = Arrays.asList("Arab National Bank", "Arab National Bank", "Arab National Bank",
                "Arab National Bank", "Test", "Test", "Test", "SHit", "SHit", "SHit");
        List<List<String>> subSets = Lists.partition(list, 4);

        subSets.forEach(sub -> System.out.println(sub));

//        List<Integer> lastPartition = subSets.get(1);
//        List<Integer> expectedLastPartition = Lists.newArrayList(1,23, 4);
//        assertEquals(subSets.size(), 4);
//        assertEquals(lastPartition, expectedLastPartition);
    }

    @Test
    public void formatterTest(){
        String number = "10000000000";
        double amount = Double.parseDouble(number);
        DecimalFormat formatter = new DecimalFormat("#,###");

        System.out.println(formatter.format(amount));
    }

    @Test
    public void sbTest(){
        String a = " wbc";
        String b = "efg";
        StringBuilder s = new StringBuilder();

        s.append(a).replace(0,2 , " W");

        System.out.println(s.toString());

    }


}
