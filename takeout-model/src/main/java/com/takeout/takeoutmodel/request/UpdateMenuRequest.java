package com.takeout.takeoutmodel.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateMenuRequest extends AddMenuRequest {

    private static final long serialVersionUID = 465906472045374161L;

    // 多出来这个 id 是为了逻辑删除菜品的
    private Long id;

}
