package com.chinaums.leaf.client;

import com.chinaums.leaf.client.util.NumberConverter;

public class IdGenerator {

    public static long nextLongId() {
        return 0;
    }

    public static String nextStringId() {
        return NumberConverter.convert_10radix_to_62radix(nextLongId());
    }

    public static long nextRandomId() {
        return 0;
    }

}
