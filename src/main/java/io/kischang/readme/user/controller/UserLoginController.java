package io.kischang.readme.user.controller;

import io.kischang.readme.app.controller.PubUserQrLoginController;
import io.kischang.readme.app.qo.LoginRegParam;
import io.kischang.readme.app.utils.ReadmeUtils;
import io.kischang.readme.base.auth.JwtUtils;
import io.kischang.readme.base.bean.R;
import io.kischang.readme.base.biz.UserApiBiz;
import io.kischang.readme.user.dao.UserAccountDao;
import io.kischang.readme.user.model.UserAccount;
import lombok.Data;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.transaction.Transactional;

@RestController
@RequestMapping("/open-api")
public class UserLoginController {

    @Value("${config.host}")
    private String host;
    @Resource
    private UserAccountDao userAccountDao;
    @Resource
    private UserApiBiz userApiBiz;

    @PostMapping("/login")
    public R<UserAccount> login(@RequestBody LoginRegParam param) {
        return userApiBiz.login(param);
    }

    @PostMapping("/reg")
    public R<UserAccount> reg(@RequestBody LoginRegParam param) {
        return userApiBiz.reg(param);
    }

    @Resource
    private WxMpService wxMpService;


    @GetMapping("/wx_login")
    public R<String> wxLoginApply(String url, String state){
        String retUrl = String.format("http://wx.kischang.top/get-weixin-code.html?" +
                "appid=%s&scope=%s&state=%s" +
                "&redirect_uri=%s%s"
                , wxMpService.getWxMpConfigStorage().getAppId()
                , "snsapi_base" // 仅获得基础信息
                , state == null ? "" : state
                , host + "/%23"
                , url
        );
        return R.data(retUrl);

//        String uri = host + "/%23" + url;
//        return R.data(
//                wxMpService.getOAuth2Service().buildAuthorizationUrl(uri, "snsapi_base", state)
//        );
    }


    @Resource
    private PubUserQrLoginController qrLoginController;

    @Data
    static class LoginRedirectParam {
        String code;
        String state;
        String deviceCode;
    }

    @Transactional
    @RequestMapping("/wx_login/wx_redirect_to_device")
    public R<UserAccount> wxLoginRedirectToDevice(@RequestBody LoginRedirectParam param) throws WxErrorException {
        R<UserAccount> r = this.wxLoginRedirect(param);
        if (r.isState()) {
            qrLoginController.updateState(param.deviceCode, 1, r.getData().getToken());
        }
        return r;
    }

    @Transactional
    @RequestMapping("/wx_login/redirect_code")
    public R<UserAccount> wxLoginRedirect(@RequestBody LoginRedirectParam param) throws WxErrorException {
        WxOAuth2AccessToken auth2AccessToken = wxMpService.getOAuth2Service().getAccessToken(param.getCode());
        WxOAuth2UserInfo userInfo = wxMpService.getOAuth2Service().getUserInfo(auth2AccessToken, null);
        UserAccount loginUser = userAccountDao.findByOpenid(userInfo.getOpenid());
        if (loginUser == null) {
            loginUser = new UserAccount();
            loginUser.setUsername(userInfo.getOpenid());
            loginUser.setOpenid(userInfo.getOpenid());
            loginUser.setAccess_token(auth2AccessToken.getAccessToken());
            loginUser.setReg_time(ReadmeUtils.dateTime());
        }
        loginUser.setNickname(userInfo.getNickname());
        loginUser.setHeadimgurl(userInfo.getHeadImgUrl());
        loginUser = userAccountDao.save(loginUser);

        // 自动登录 返回jwt
        String jwt = JwtUtils.generateToken(loginUser.getUsername());
        loginUser.setToken(jwt);
        return R.data(loginUser).setMsg("登录成功");
    }

}
