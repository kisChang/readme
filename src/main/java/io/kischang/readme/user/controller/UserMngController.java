package io.kischang.readme.user.controller;


import io.kischang.readme.app.controller.PubUserQrLoginController;
import io.kischang.readme.app.qo.ChangePasswordParam;
import io.kischang.readme.base.auth.JwtUtils;
import io.kischang.readme.base.bean.R;
import io.kischang.readme.base.biz.UserApiBiz;
import io.kischang.readme.user.dao.UserAccountDao;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user-api")
public class UserMngController {

    @Resource
    private UserAccountDao accountDao;
    @Resource
    private UserApiBiz userApiBiz;

    @Resource
    private PubUserQrLoginController qrLoginController;

    /**
     * 登录到设备 */
    @RequestMapping("/login_to_device")
    public R<?> changePassword(String key) {
        String jwt = JwtUtils.generateToken(JwtUtils.getCurrentUsername());
        qrLoginController.updateState(key, 1, jwt);
        return R.ok("操作成功");
    }

    @PostMapping("/change_password")
    public R<?> changePassword(@RequestBody ChangePasswordParam param) {
        return userApiBiz.updatePassword(JwtUtils.getCurrentUsername(), param);
    }

    @GetMapping("/change_username")
    public R<?> changeUsername(String username) {
        String user = JwtUtils.getCurrentUsername();
        if (user == null) {
            return R.fail(403, "未登录！");
        }
        return userApiBiz.changeUsername(user, username);
    }

}
