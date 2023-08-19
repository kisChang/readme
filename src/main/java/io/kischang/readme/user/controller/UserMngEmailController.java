package io.kischang.readme.user.controller;


import io.kischang.readme.app.dao.UserTrustMailsDao;
import io.kischang.readme.app.model.UserTrustMails;
import io.kischang.readme.app.qo.BaseParam;
import io.kischang.readme.base.auth.JwtUtils;
import io.kischang.readme.base.bean.R;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user-api/email")
public class UserMngEmailController {

    @Resource
    private UserTrustMailsDao dao;

    @RequestMapping("/page")
    public R<Page<UserTrustMails>> feedSourcePage(BaseParam pageable){
        Page<UserTrustMails> page = dao.findByUser(JwtUtils.getCurrentUser().getUserId(), pageable.toPage());
        return R.data(page);
    }

    @Transactional
    @RequestMapping("/update")
    public R<?> updateFeed(@RequestBody UserTrustMails obj) {
        obj.setUserId(JwtUtils.getCurrentUser().getUserId());
        dao.save(obj);
        return R.ok("操作成功");
    }

    @Transactional
    @RequestMapping("/delete")
    public R<?> delete(@RequestBody UserTrustMails obj) {
        dao.deleteById(obj.getUserMailsId());
        return R.ok("操作成功");
    }

}
