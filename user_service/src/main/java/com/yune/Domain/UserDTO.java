package com.yune.Domain;

import lombok.Data;
import lombok.NonNull;
import org.bouncycastle.cms.PasswordRecipientId;

import javax.validation.constraints.NotNull;
@Data
public class UserDTO {
    @NotNull(message = "用户名不能为空")
    private String username;

    @NotNull(message = "密码不能为空")
    private String password;


}
