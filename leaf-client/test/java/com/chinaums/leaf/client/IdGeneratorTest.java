package com.chinaums.leaf.client;

public class IdGeneratorTest {

    public static void main(String[] args) {
        IdGenerator idGenerator = new IdGenerator(new String[]{"10.11.117.32:2181"});
        for(int i = 0; i < 10; i++){
            System.out.println(idGenerator.nextId("aaa"));
        }
        for(int i = 0; i < 10; i++){
            System.out.println(idGenerator.nextId("bbb"));
        }
    }
}
