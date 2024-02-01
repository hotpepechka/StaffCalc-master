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

    @RepeatedTest(10)
    public void test1() {
        int creationSize = 10000000;
        long startTime = System.nanoTime();
        ArrayList<String> stringsList = new ArrayList<>(10000000);
        for (int i = 0; i < creationSize; i++){
            stringsList.add("test" + i);
        }
        long endFillTime = System.nanoTime();
        System.out.println("Fill for:" + TimeUnit.MILLISECONDS.convert(endFillTime - startTime, TimeUnit.NANOSECONDS) + "size" + stringsList.size());
    }

    @RepeatedTest(10)
    public void test2(){
        int creationSize = 10000000;
        long startTime = System.nanoTime();
        LinkedList<String> stringsList = new LinkedList<>();
        for (int i = 0; i < creationSize; i++){
            stringsList.add("test" + i);
        }
        long endFillTime = System.nanoTime();
        System.out.println("Fill for: " + TimeUnit.MILLISECONDS.convert(endFillTime - startTime, TimeUnit.NANOSECONDS) + "size" + stringsList.size());
    }

    @Test
    public void test3(){
        HashMap<TestKey, String> map = new HashMap<TestKey, String>();
        TestKey key1 = new TestKey("test", 1l);
        TestKey key2 = new TestKey("test3", 2l);

        map.put(key1,"value1");
        map.put(key2,"value2");

        System.out.println(map.get(key1) + "/" + map.get(key2));

        key1.setField1("test2");
        System.out.println(map.get(key1) + "/" + map.get(key2));
    }

    @Data
    @AllArgsConstructor
    public static class TestKey {
        private String field1;
        private Long field2;

        @Override
        public int hashCode(){
            return 4;
        }

        @Override
        public boolean equals(Object obj){
            return field2.equals(((TestKey)obj).getField2());
        }
    }
}
