package io.kischang.readme.mail;

import lombok.extern.slf4j.Slf4j;
import org.apache.james.protocols.api.handler.ProtocolHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

@Slf4j
@Configuration
@EnableConfigurationProperties(SmtpServerProperties.class)
public class SmtpServerConfiguration {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public ReadMailServer smtpServer(SmtpServerProperties properties, Collection<ProtocolHandler> handlers) {
        return new ReadMailServer(properties, handlers);
    }

}