package io.kischang.readme.app.controller;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.kischang.readme.app.utils.ZXingUtils;
import io.kischang.readme.base.bean.R;
import io.kischang.readme.base.exception.BaseException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/open-api/user")
public class PubUserQrLoginController {

    @Resource
    private WxMpService wxMpService;
    @Value("${config.host}")
    private String host;

    private static final Cache<String, QrCodeState> stateMap = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static final class QrCodeState implements java.io.Serializable {
        int state;
        String code;
        String deviceInfo;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static final class NavigatorParams implements java.io.Serializable {
        String userAgent;
        String appName;
        String appCodeName;
        String appVersion;
        String platform;
    }

    public void updateState(String code, int state, String authKey) {
        QrCodeState old = stateMap.getIfPresent(code);
        if (old == null) {
            old = new QrCodeState(state, authKey, null);
        }
        stateMap.put(code, old);
    }

    @RequestMapping("/loop_login_status")
    public R<?> loop_state(String code) {
        QrCodeState state = stateMap.getIfPresent(code);
        if (state == null) {
            return R.fail(-1, "未初始化，请重试", null);
        } else {
            if (state.getState() == 1) {
                // 成功后清理
                stateMap.invalidate(code);
            }
            return R.data(state);
        }
    }

    // 获取微信登录的链接
    @RequestMapping("/getcode_login_wx")
    public R<?> getCodeLogin_Wx(String key) {
        String url = String.format("http://wx.kischang.top/get-weixin-code.html?appid=%s&scope=%s&state=&" +
                        "redirect_uri=%s/login/wx_redirect_to_device?key=%s"
                , wxMpService.getWxMpConfigStorage().getAppId()
                , "snsapi_base"
                , host + "/%23"
                , key);
        return R.ok().setData(url);
    }

    // 扫描登录二维码
    @RequestMapping("/get_login_qr")
    public R<?> get_login_qr(NavigatorParams params) {
        try {
            String key = RandomStringUtils.random(20, "ABCDEFGHIJKLMOPQRSTUVWXYZ1234567890");
            // 访问登陆页面
            String url = String.format("%s/#/login/device_qrcode?key=%s", host, key);
            String qrCode = ZXingUtils.QrCodeBuilder.genBuilder(url)
                    .genBase64();
            Map<String, String> map = new HashMap<>();
            map.put("qrCode", qrCode);
            map.put("code", key);
            stateMap.put(key, new QrCodeState(0, null, params.getUserAgent()));
            return R.data(map);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(-1, e.getMessage());
        }
    }
}
