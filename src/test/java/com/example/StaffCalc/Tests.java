package com.example.StaffCalc;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class Tests {

    /**
     * Fill for:1504 size:10000000
     * Fill for:1149 size:10000000
     * Fill for:1334 size:10000000
     * Fill for:1282 size:10000000
     * Fill for:757 size:10000000
     * Fill for:786 size:10000000
     * Fill for:807 size:10000000
     * Fill for:848 size:10000000
     * Fill for:1090 size:10000000
     * Fill for:958 size:10000000
     *
     *
     *
     *
     * Fill for:2128 size:10000000
     * Fill for:1545 size:10000000
     * Fill for:1759 size:10000000
     * Fill for:1577 size:10000000
     * Fill for:1603 size:10000000
     * Fill for:1468 size:10000000
     * Fill for:1453 size:10000000
     * Fill for:1235 size:10000000
     * Fill for:1938 size:10000000
     * Fill for:2168 size:10000000
     *
     *
     * Fill for:1779 size:10000000
     * Fill for:1900 size:10000000
     * Fill for:2666 size:10000000
     * Fill for:2449 size:10000000
     * Fill for:2060 size:10000000
     * Fill for:2357 size:10000000
     * Fill for:1351 size:10000000
     * Fill for:1098 size:10000000
     * Fill for:2708 size:10000000
     * Fill for:1808 size:10000000
     */
    @RepeatedTest(10)
    public void test1() {
        int creationSize = 10000000;
        long startTime = System.nanoTime();
        ArrayList<String> stringsList = new ArrayList<>(10000000);
        for (int i = 0; i < creationSize; i++) {
            stringsList.add("test" + i);
        }
        long endFillTime = System.nanoTime();
        System.out.println("Fill for:" + TimeUnit.MILLISECONDS.convert(endFillTime - startTime, TimeUnit.NANOSECONDS) + " size:" + stringsList.size());
    }

    /**
     * Fill for:2490 size:10000000
     * Fill for:2233 size:10000000
     * Fill for:2196 size:10000000
     * Fill for:2724 size:10000000
     * Fill for:2868 size:10000000
     * Fill for:3017 size:10000000
     * Fill for:3627 size:10000000
     * Fill for:3557 size:10000000
     * Fill for:3645 size:10000000
     * Fill for:3043 size:10000000
     */
    @RepeatedTest(10)
    public void test2() {
        int creationSize = 10000000;
        long startTime = System.nanoTime();
        LinkedList<String> stringsList = new LinkedList<>();
        for (int i = 0; i < creationSize; i++) {
            stringsList.add("test" + i);
        }
        long endFillTime = System.nanoTime();
        System.out.println("Fill for:" + TimeUnit.MILLISECONDS.convert(endFillTime - startTime, TimeUnit.NANOSECONDS) + " size:" + stringsList.size());
    }

    @Test
    public void test3() {
        HashMap<TestKey, String> map = new HashMap<TestKey, String>();
        TestKey key1 = new TestKey("test", 1l);
        TestKey key2 = new TestKey("test3", 2l);

        map.put(key1, "value1");
        map.put(key2, "value2");

        System.out.println(map.get(key1) + "/" + map.get(key2));

        key1.setField1("test2");
        System.out.println(map.get(key1) + "/" + map.get(key2));
    }

    @Data
    @AllArgsConstructor
    public static class TestKey
    {
        private String field1;
        private Long field2;

        @Override
        public int hashCode() {
            return 4;
        }

        @Override
        public boolean equals(Object obj) {
            return field2.equals(((TestKey)obj).getField2());
        }
    }
}
