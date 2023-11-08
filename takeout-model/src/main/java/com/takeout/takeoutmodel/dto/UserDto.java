package com.takeout.takeoutmodel.dto;

import com.takeout.takeoutmodel.entity.User;
import com.takeout.takeoutmodel.enums.UserRoleEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserDto implements Serializable {
    private static final long serialVersionUID = -3752605852684634302L;

    private User user;

    private UserRoleEnum userRoleEnum;
}
