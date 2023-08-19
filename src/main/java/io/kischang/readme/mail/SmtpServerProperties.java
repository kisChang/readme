package io.kischang.readme.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "smtp.server")
public class SmtpServerProperties {

    private int smtpPort = 25;

    private int timeout = 120;

}