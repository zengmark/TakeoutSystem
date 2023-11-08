package com.takeout.takeoutmodel.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public enum UserRoleEnum {

    USER("user", 0),
    SHOPKEEPER("shop", 1),
    ADMIN("admin", 2);

    private final String text;
    private final Integer value;

    /**
     * 获取值列表
     * @return
     */
    public static List<Integer> getValues(){
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据值获取枚举
     * @param value
     * @return
     */
    public static UserRoleEnum getEnumByValue(Integer value){
        if(Objects.isNull(value)){
            return null;
        }

        for (UserRoleEnum userRoleEnum : UserRoleEnum.values()) {
            if(userRoleEnum.value.equals(value)){
                return userRoleEnum;
            }
        }

        return null;
    }

    UserRoleEnum(String text, Integer value){
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public Integer getValue() {
        return value;
    }
}
