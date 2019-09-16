package com.chinaums.leaf.client.util;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

//@Slf4j
public class NumberConverter {

    private static char[] dict = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };
    private static Map<Integer, Character> charMap = new HashMap<>();
    private static Map<Character, Integer> intMap = new HashMap<>();

    static {
        for (int i = 0; i < dict.length; i++) {
            charMap.put(i, dict[i]);
            intMap.put(dict[i], i);
        }
    }

    private static char _int_to_char(final int i) {
        Character c = charMap.get(i);
        if (c == null) {
            throw new IllegalArgumentException("数字[" + i + "]不在[0, 62)区间");
        }
        return c;
    }

    private static int _char_to_int(final char c) {
        Integer i = intMap.get(c);
        if (i == null) {
            throw new IllegalArgumentException("字母[" + c + "]不在[0, 62)区间");
        }
        return i;
    }

    private static <T> String splice_to_62radix(T v, LinkedList<Integer> list) {
        StringBuilder sb = new StringBuilder();
        for (int i : list) {
            sb.append(_int_to_char(i));
        }
        String str = sb.toString();

//        log.debug("{} -> {}{}", v, str, list);

        return str;
    }

    public static String convert_10radix_to_62radix(final long v) {
        LinkedList<Integer> list = new LinkedList<Integer>();

        if (v < 0) {
            throw new IllegalArgumentException();
        } else if (v == 0) {
            list.addFirst(0);
        } else {
            long t = v;
            int r = 0;
            while (t > 0) {
                r = (int) (t % 62);
                t = (t - r) / 62;
                list.addFirst(r);
            }
        }

        return splice_to_62radix(v, list);
    }

    public static long convert_62radix_to_10radix(final String s) {
        long v = 0;
        long r = 1;
        for (int i = s.length() - 1; i >= 0; i--) {
            v += _char_to_int(s.charAt(i)) * r;
            r *= 62;
        }

//        log.debug("{} <- {}", s, v);

        return v;
    }

    private static final BigInteger _62_BIGINT = BigInteger.valueOf(62);

    public static String convert_bigint_to_62radix(final BigInteger v) {
        LinkedList<Integer> list = new LinkedList<Integer>();

        int ret = v.compareTo(BigInteger.ZERO);
        if (ret < 0) {
            throw new IllegalArgumentException();
        } else if (ret == 0) {
            list.addFirst(0);
        } else {
            BigInteger t = v;
            BigInteger r;
            while (t.compareTo(BigInteger.ZERO) > 0) {
                r = t.mod(_62_BIGINT);
                t = t.subtract(r).divide(_62_BIGINT);
                list.addFirst(r.intValue());
            }
        }

        return splice_to_62radix(v, list);
    }

    public static BigInteger convert_62radix_to_bigint(final String s) {
        BigInteger v = BigInteger.ZERO;
        BigInteger r = BigInteger.ONE;
        for (int i = s.length() - 1; i >= 0; i--) {
            v = v.add(BigInteger.valueOf(_char_to_int(s.charAt(i))).multiply(r));
            r = r.multiply(_62_BIGINT);
        }

//        log.debug("{} <- {}", s, v);

        return v;
    }

    public static void main(String[] args) {
        System.out.println(convert_10radix_to_62radix(Long.MAX_VALUE - 1) + " " + Long.MAX_VALUE);

//        System.out.println(convert_10radix_to_62radix(0));
//        System.out.println(convert_10radix_to_62radix(200));
//        System.out.println(convert_62radix_to_10radix("ca"));

        System.out.println(convert_62radix_to_10radix(convert_10radix_to_62radix(0)));
        System.out.println(convert_62radix_to_10radix(convert_10radix_to_62radix(72)));
        System.out.println(convert_62radix_to_10radix(convert_10radix_to_62radix(200)));
        System.out.println(convert_62radix_to_10radix(convert_10radix_to_62radix(20000)));
        System.out.println(convert_62radix_to_10radix(convert_10radix_to_62radix(200000)));
        System.out.println(convert_62radix_to_10radix(convert_10radix_to_62radix(2000000)));
        System.out.println(convert_62radix_to_10radix(convert_10radix_to_62radix(20000000)));
        System.out.println(convert_62radix_to_10radix(convert_10radix_to_62radix(200000000)));
        System.out.println(convert_62radix_to_10radix(convert_10radix_to_62radix(2000000000)));
        System.out.println(convert_62radix_to_bigint(convert_bigint_to_62radix(new BigInteger("20000000000"))));
        System.out.println(convert_62radix_to_bigint(convert_bigint_to_62radix(new BigInteger("200000000000"))));
        System.out.println(convert_62radix_to_bigint(convert_bigint_to_62radix(new BigInteger("2000000000000"))));
        System.out.println(convert_62radix_to_bigint(convert_bigint_to_62radix(new BigInteger("20000000000000"))));
    }

}