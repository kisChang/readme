package io.kischang.readme.base.config;

import io.kischang.readme.base.bean.WxMpConfigStorage;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(WxMpConfigStorage.class)
public class WxServiceFactory {

    @Bean
    public WxMpService getWxMpService(WxMpConfigStorage configStorage) {
        WxMpService mpService = new WxMpServiceImpl();
        WxMpDefaultConfigImpl storage = new WxMpDefaultConfigImpl();
        storage.setAppId(configStorage.getAppid());
        storage.setSecret(configStorage.getAppsecret());
        storage.setToken(configStorage.getToken());
        storage.setAesKey(configStorage.getAesKey());
        mpService.setWxMpConfigStorage(storage);
        return mpService;
    }

}