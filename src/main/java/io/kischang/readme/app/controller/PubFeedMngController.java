package io.kischang.readme.app.controller;

import io.kischang.readme.app.dao.FeedSourceDao;
import io.kischang.readme.app.model.FeedArticle;
import io.kischang.readme.app.model.FeedSource;
import io.kischang.readme.app.qo.FeedSourceSearchQO;
import io.kischang.readme.base.bean.R;
import io.kischang.readme.base.biz.AdminApiBiz;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/open-api/feed")
public class PubFeedMngController {

    @Resource
    private FeedSourceDao feedSourceDao;
    @Resource
    private AdminApiBiz adminApiBiz;

    @RequestMapping("/page")
    public R<Page<FeedSource>> feedSourcePage(FeedSourceSearchQO qo){
        Page<FeedSource> page = feedSourceDao.findAll(Example.of(qo.toParam(FeedSource.class)), qo.toPage());
        return R.data(page);
    }

    @RequestMapping("/pull")
    public R<Page<FeedArticle>> feedPull(){
        adminApiBiz.pullFeed(2);
        return R.ok("操作成功");
    }

}
