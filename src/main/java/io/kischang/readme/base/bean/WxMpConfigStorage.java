package io.kischang.readme.base.bean;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "config.weixin.mp")
public class WxMpConfigStorage {

    private String appid;

    private String appsecret;

    private String token;

    private String aesKey;

}