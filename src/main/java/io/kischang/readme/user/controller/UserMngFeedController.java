package io.kischang.readme.user.controller;


import io.kischang.readme.app.dao.FeedSourceDao;
import io.kischang.readme.app.dao.UserFeedsDao;
import io.kischang.readme.app.model.FeedSource;
import io.kischang.readme.app.model.UserFeeds;
import io.kischang.readme.base.auth.JwtUtils;
import io.kischang.readme.base.bean.R;
import io.kischang.readme.base.biz.AdminApiBiz;
import io.kischang.readme.base.biz.UserApiBiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user-api/feed")
public class UserMngFeedController {

    @Resource
    private UserApiBiz userApiBiz;
    @Resource
    private FeedSourceDao feedSourceDao;
    @Resource
    private UserFeedsDao userFeedsDao;
    @Resource
    private AdminApiBiz adminApiBiz;


    @RequestMapping("/addFeedById")
    public R<?> addFeedById(long feedId) {
        return userApiBiz.addFeedById(JwtUtils.getCurrentUser().getUserId(), feedId);
    }

    @RequestMapping("/addFeed")
    public R<?> addFeed(@RequestBody FeedSource feedSource) {
        UserFeeds uf = userApiBiz.addFeed(feedSource, JwtUtils.getCurrentUser().getUserId());
        // 单独现成处理
        new Thread(() -> adminApiBiz.pullFeed(uf.getFeedId())).start();
        return R.ok("操作成功");
    }

    @Transactional
    @RequestMapping("/updateFeed")
    public R<?> updateFeed(@RequestBody FeedSource feedSource) {
        feedSourceDao.save(feedSource);
        return R.ok("操作成功");
    }

    @Transactional
    @RequestMapping("/deleteFeed")
    public R<?> deleteFeed(@RequestBody UserFeeds userFeeds) {
        userFeedsDao.deleteById(userFeeds.getUserFeedId());
        return R.ok("操作成功");
    }

}
