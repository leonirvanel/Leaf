package com.chinaums.leaf.client;

public class Test {

    public static void main(String[] args) {
        IdGenerator idGenerator = new IdGenerator("test", new String[]{"10.11.117.33:2181", "10.11.117.34:2181", "10.11.117.35:2181"});

        long prev = 0, cur = 0;
        for (int i = 0; i < 1000; i++) {
            prev = cur;
            cur = idGenerator.nextSequenceId("Thread-12");
            System.out.println(cur);
            if(cur <= prev) {
                System.out.println("zaogao !!!");
                return;
            }
        }
        for (int i = 0; i < 100; i++) {
            System.out.println(idGenerator.nextSequenceId("ccc"));
        }
//        for (int i = 0; i < 10; i++) {
//            System.out.println(idGenerator.nextSequenceId("ddd"));
//        }

    }
}
