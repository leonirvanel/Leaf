package com.sankuai.inf.leaf.snowflake;

import com.sankuai.inf.leaf.IDGen;
import com.sankuai.inf.leaf.common.Result;
import org.junit.Test;

public class SnowflakeIDGenImplTest {
    @Test
    public void testGetId() {
        IDGen idGen = new SnowflakeIDGenImpl("10.11.117.37:2181,10.11.117.38:2181,10.11.117.39:2181",
                8080,
                true,
                2);
        for (int i = 1; i <= 10; ++i) {
            Result r = idGen.get("a");
            System.out.println(r);
        }
    }
}
