package com.takeout.takeoutcommon.common;

import lombok.Data;

@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private long current = 1;

    /**
     * 当前页大小
     */
    private long pageSize = 8;

    /**
     * 排序字段
     */
    private String sortedField;
}
