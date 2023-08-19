package io.kischang.readme.app.vo;

import io.kischang.readme.app.model.FeedSource;
import io.kischang.readme.app.model.UserFeeds;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedSourceUserVo {

    private Long feedId;
    private String name;
    private String logo;
    private String url;
    private String descText;
    private String cron;
    private String lastUpdate;

    private Long userFeedId;
    private Long userId;


    public FeedSourceUserVo(FeedSource feedSource, UserFeeds userFeeds) {
        this.feedId = feedSource.getFeedId();
        this.name = feedSource.getName();
        this.logo = feedSource.getLogo();
        this.url = feedSource.getUrl();
        this.descText = feedSource.getDescText();
        this.cron = feedSource.getCron();
        this.lastUpdate = feedSource.getLastUpdate();
        this.userFeedId = userFeeds.getUserFeedId();
        this.userId = userFeeds.getUserId();
    }
}
