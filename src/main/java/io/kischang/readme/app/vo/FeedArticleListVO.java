package io.kischang.readme.app.vo;

import io.kischang.readme.app.model.FeedArticle;
import lombok.Data;

@Data
public class FeedArticleListVO implements java.io.Serializable {

    private Long articleId;

    private Long feedId;
    private String hashKey;
    private String uri;

    private String source;
    private String title;
    // 发布时间
    private String publishedDate;
    private Integer status;

    private String feedName;

    public FeedArticleListVO(FeedArticle tmp, Integer status) {
        this.status = status;
        this.feedId = tmp.getFeedId();
        this.articleId = tmp.getArticleId();
        this.hashKey = tmp.getHashKey();
        this.uri = tmp.getUri();
        this.source = tmp.getSource();
        this.title = tmp.getTitle();
        this.publishedDate = tmp.getPublishedDate();
        this.parseSourceType(tmp.getSourceType());
    }

    public FeedArticleListVO(FeedArticle tmp, Integer status, String feedName) {
        this(tmp, status);
        this.feedName = feedName;
        this.parseSourceType(tmp.getSourceType());
    }

    private void parseSourceType(Integer sourceType) {
        if (sourceType != null){
            if (sourceType == 1) {
                this.feedName = "邮件订阅";
            }
        }
    }

}
