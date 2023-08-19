package io.kischang.readme.app.qo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChangePasswordParam implements Serializable {

    private String oldPassword;

    private String newPassword;

}
