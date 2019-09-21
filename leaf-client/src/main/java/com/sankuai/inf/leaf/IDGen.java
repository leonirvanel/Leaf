package com.sankuai.inf.leaf;

import com.sankuai.inf.leaf.common.Result;

public interface IDGen {
    default Result get() {
        return get(null);
    }
    Result get(String key);
    boolean init();
}
