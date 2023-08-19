package io.kischang.readme.base.core;

import io.kischang.readme.app.dao.FeedSourceDao;
import io.kischang.readme.app.model.FeedSource;
import io.kischang.readme.base.biz.AdminApiBiz;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class FeedLoopPullJob {
    @Resource
    private FeedSourceDao feedSourceDao;
    @Resource
    private AdminApiBiz adminApiBiz;


    @Scheduled(cron = "0 0 0,6,8,11,14,17,20 * * ? ")
    public void loadAllFeed() {
        List<FeedSource> sourceList = feedSourceDao.findAll();
        for (FeedSource feedSource : sourceList) {
            adminApiBiz.pullFeed(feedSource.getFeedId());
        }
    }

}
