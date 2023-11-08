package com.takeout.takeoutmodel.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public enum ShopTagEnum {
    FOOD("food", 0),
    DRINK("drink", 1),
    FRUIT("fruit", 2),
    MEDICINE("medicine", 3);

    /**
     * 标签的名称
     */
    private final String text;
    /**
     * 标签所对应的 bitmap 的下标
     */
    private final Integer index;

    ShopTagEnum(String text, Integer index){
        this.text = text;
        this.index = index;
    }

    public static List<Integer> getValues(){
        return Arrays.stream(values()).map(shopTagEnum -> shopTagEnum.index).collect(Collectors.toList());
    }

    public static ShopTagEnum getEnumByValue(Integer index){
        if(Objects.isNull(index)){
            return null;
        }

        for (ShopTagEnum value : values()) {
            if(value.index.equals(index)){
                return value;
            }
        }

        return null;
    }

    public String getText() {
        return text;
    }

    public Integer getIndex() {
        return index;
    }
}
