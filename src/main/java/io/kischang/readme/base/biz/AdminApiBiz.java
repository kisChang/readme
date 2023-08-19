package io.kischang.readme.base.biz;

import com.rometools.rome.io.FeedException;
import io.kischang.readme.app.dao.FeedArticleDao;
import io.kischang.readme.app.dao.FeedSourceDao;
import io.kischang.readme.app.dao.UserArticleDao;
import io.kischang.readme.app.dao.UserFeedsDao;
import io.kischang.readme.app.model.FeedArticle;
import io.kischang.readme.app.model.FeedSource;
import io.kischang.readme.app.model.UserArticle;
import io.kischang.readme.app.model.UserFeeds;
import io.kischang.readme.app.utils.RSSReaderUtil;
import io.kischang.readme.app.utils.ReadmeUtils;
import io.kischang.readme.base.exception.BaseException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

@Component
public class AdminApiBiz {

    @Resource
    private FeedSourceDao feedSourceDao;
    @Resource
    private FeedArticleDao articleDao;
    @Resource
    private UserFeedsDao userFeedsDao;
    @Resource
    private UserArticleDao userArticleDao;

    private final ExecutorService executor = new ThreadPoolExecutor(1, 3, 120, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    public static class ArticleContentParseRunnable implements Runnable {
        FeedArticle article;
        FeedArticleDao dao;

        public ArticleContentParseRunnable(FeedArticle article, FeedArticleDao dao) {
            this.article = article;
            this.dao = dao;
        }

        public void run() {
            String html = RSSReaderUtil.parseHtmlContent(article.getContent());
            article.setContent(html);
            dao.save(article);
        }
    }

    @Transactional
    public void pullFeed(long feedId) {
        Optional<FeedSource> opt = feedSourceDao.findById(feedId);
        if (opt.isEmpty()) {
            return;
        }
        FeedSource feedSource = opt.get();
        List<UserFeeds> userFeeds = userFeedsDao.findAllByFeedId(feedId);
        try {
            List<RSSReaderUtil.NewsArticle> newsArticleList = RSSReaderUtil.read(feedSource.getUrl());
            for (RSSReaderUtil.NewsArticle once : newsArticleList) {
                FeedArticle article = articleDao.findByFeedIdAndUri(feedSource.getFeedId(), once.getUri());
                if (article == null) {
                    article = new FeedArticle();
                    article.setFeedId(feedSource.getFeedId());
                    article.setUri(once.getUri());
                    article.setTitle(once.getTitle());
                    article.setSource(once.getLink());
                    article.setPublishedDate(once.getPublishedDate());
                    article.setContent(once.getContent());
                    // 在这里去处理格式化问题
                    executor.submit(new ArticleContentParseRunnable(article, articleDao));
                    article = articleDao.save(article);
                    // 更新用户关注
                    for (UserFeeds userFeed : userFeeds) {
                        UserArticle userArticle = userArticleDao.findFirstByUserIdAndArticleIdAndFeedId(userFeed.getUserId(), article.getArticleId(), article.getFeedId());
                        if (userArticle == null) {
                            userArticle = new UserArticle();
                            userArticle.setArticleId(article.getArticleId());
                            userArticle.setUserId(userFeed.getUserId());
                            userArticle.setFeedId(article.getFeedId());
                        }
                        userArticle.setStatus(0);
                        userArticleDao.save(userArticle);
                    }
                }
            }
            feedSource.setLastUpdate(ReadmeUtils.dateTime());
            feedSourceDao.save(feedSource);
        } catch (IOException | FeedException e) {
            e.printStackTrace();
            throw new BaseException(-1, "加载失败：" + e.getMessage());
        }
    }

}
