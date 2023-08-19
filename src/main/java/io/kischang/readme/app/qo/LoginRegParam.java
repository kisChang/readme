package io.kischang.readme.app.qo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginRegParam implements Serializable {

    private String nickname;

    private String username;

    private String password;

    private String code;

}
