package io.kischang.readme.app.user.controller;

import io.kischang.readme.app.dao.FeedArticleDao;
import io.kischang.readme.app.dao.FeedSourceDao;
import io.kischang.readme.app.model.FeedArticle;
import io.kischang.readme.app.model.FeedSource;
import io.kischang.readme.app.qo.BaseParam;
import io.kischang.readme.app.qo.FeedSourceParam;
import io.kischang.readme.app.vo.FeedArticleListVO;
import io.kischang.readme.app.vo.FeedSourceUserVo;
import io.kischang.readme.base.auth.JwtUtils;
import io.kischang.readme.base.bean.R;
import io.kischang.readme.base.biz.UserApiBiz;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Optional;

@RestController
@RequestMapping("/user-api")
public class UserApiController {

    @Resource
    private FeedSourceDao feedSourceDao;
    @Resource
    private FeedArticleDao feedArticleDao;
    @Resource
    private UserApiBiz userApiBiz;

    /**
     * 我的关注列表
     */
    @RequestMapping("/feed_source/self")
    public R<Page<FeedSourceUserVo>> feedSourcePage(BaseParam pageable){
        Page<FeedSourceUserVo> page = feedSourceDao.findByUser(JwtUtils.getCurrentUser().getUserId(), pageable.toPage());
        return R.data(page);
    }

    @RequestMapping("/feed_source/all/page")
    public R<Page<FeedSource>> allFeedPage(FeedSourceParam param){
        Page<FeedSource> page = feedSourceDao.findAll(builder ->
                builder.andLike("name", param.getName())
                        .build()
                , param.toPage());
        return R.data(page);
    }

    /**
     * 单个关注的文章列表
     */
    @RequestMapping("/user_article/list")
    public R<Page<FeedArticleListVO>> feedArticleList(long feedId, int page, int size){
        Page<FeedArticleListVO> data = feedArticleDao
                .findByUserAndFeedId(JwtUtils.getCurrentUser().getUserId(), feedId, PageRequest.of(page, size));
        return R.data(data);
    }
    /**
     * 单个关注的文章列表
     */
    @RequestMapping("/user_article/list_all")
    public R<Page<FeedArticleListVO>> feedArticleListAll(int page, int size){
        Page<FeedArticleListVO> data = feedArticleDao
                .findByUser(JwtUtils.getCurrentUser().getUserId(), PageRequest.of(page, size));
        return R.data(data);
    }

    /**
     * 单个文章内容
     */
    @RequestMapping("/user_article/get_one")
    public R<FeedArticle> feedArticleGetOnce(long articleId){
        return R.data(userApiBiz.haveRead(articleId, JwtUtils.getCurrentUser().getUserId()));
    }

}
